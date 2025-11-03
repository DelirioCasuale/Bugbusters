import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, handleLogout } from './modules/auth.js';
import { Modal, updateGeneralUI } from './modules/ui.js';

let createSheetModal, joinCampaignModal;

document.addEventListener('DOMContentLoaded', () => {

    // --- GUARDIA DI AUTENTICAZIONE ---
    if (!isAuthenticated()) {
        console.warn("Utente non autenticato. Reindirizzamento a landing.html");
        window.location.replace('landing.html');
        return;
    }
    if (!isPlayer()) { // Se è loggato ma non è player
        console.warn("Utente non player su player.html. Reindirizzamento...");
        window.location.replace('profile.html'); // Manda a scegliere il ruolo
        return;
    }
    // ---------------------------------

    // Se la guardia passa, inizializza la pagina
    updateGeneralUI();

    createSheetModal = new Modal('createSheetModal');
    joinCampaignModal = new Modal('joinCampaignModal');

    document.getElementById('createSheetForm')?.addEventListener('submit', handleCreateSheet);
    document.getElementById('joinCampaignForm')?.addEventListener('submit', handleJoinCampaign);

    document.getElementById('card-show-create-sheet-modal')?.addEventListener('click', () => createSheetModal?.show());
    document.getElementById('card-show-join-campaign-modal')?.addEventListener('click', () => joinCampaignModal?.show());

    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    loadPlayerData();
});

// ... (tutte le funzioni handleCreateSheet, handleJoinCampaign, handleVoteProposal, 
//      e loadPlayerData rimangono invariate, come nel file JS monolitico) ...

// Handlers specifici
async function handleCreateSheet(event) {
    event.preventDefault();
    if (!createSheetModal) return;
    createSheetModal.hideError();
    const name = document.getElementById('sheet-name')?.value;
    const primaryClass = document.getElementById('sheet-class')?.value;
    const race = document.getElementById('sheet-race')?.value;
    if (!name || !primaryClass || !race) {
        createSheetModal.showError("Tutti i campi sono obbligatori.");
        return;
    }
    const data = await apiCall('/api/player/sheets', 'POST', { name, primaryClass, race });
    if (data) {
        createSheetModal.hide();
        loadPlayerData();
    }
}
async function handleJoinCampaign(event) {
    event.preventDefault();
    if (!joinCampaignModal) return;
     
    // 1. Pulisce eventuali errori precedenti
    joinCampaignModal.hideError();
     
    const inviteCode = document.getElementById('join-campaign-code')?.value;
    const characterSheetId = document.getElementById('join-campaign-sheet-id')?.value;
     
    // 2. Validazione lato client (campi vuoti)
    if (!inviteCode || !characterSheetId) {
        joinCampaignModal.showError("Codice invito e scheda sono obbligatori.");
        return;
    }
     
    // 3. Chiamata API (api.js restituirà {status, message} se 4xx)
    const data = await apiCall('/api/player/campaigns/join', 'POST', { 
        inviteCode, 
        characterSheetId: Number(characterSheetId) 
    });

    // 4. GESTIONE ERRORE PERSONALIZZATO
    // Se 'data' esiste e ha uno 'status', è un oggetto errore da api.js
    if (data && data.status) {
        // Mostra il messaggio specifico del backend (es. "Codice invito non valido", 
        // "Fai già parte di questa campagna.", "Scheda non trovata.", ecc.)
        joinCampaignModal.showError(data.message || 'Si è verificato un errore.');
        return; 
    }
     
    // 5. GESTIONE SUCCESSO
    if(data && data.message) {
        joinCampaignModal.hide();
        loadPlayerData(); 
        
        // (Opzionale) Mostra un alert di successo (o usa il modal 'infoModal' se lo hai)
        alert(data.message); // Es. "Ti sei unito alla campagna '...'!"
    }
}
async function handleVoteProposal(proposalId) {
    console.log("Voto per proposta:", proposalId);
    const data = await apiCall(`/api/player/proposals/${proposalId}/vote`, 'POST');
    if (data) {
        loadPlayerData();
    }
}
window.handleVoteProposal = handleVoteProposal;

// Funzione di caricamento dati
async function loadPlayerData() {
    console.log("Caricamento dati Player...");
    if (!isPlayer()) return; 

    const sheets = await apiCall('/api/player/sheets');
    const sheetsList = document.getElementById('player-sheets-list');
    
    if (sheets && sheetsList) {
        if(sheets.length > 0) {
            sheetsList.innerHTML = sheets.map(sheet => `
                <div class="card">
                    <h3>${sheet.name || 'Senza nome'}</h3>
                    
                    <p>${sheet.primaryClass || '?'} Lvl ${sheet.primaryLevel || '?'} · ${sheet.race || '?'}</p>
                    
                    <a href="edit-sheet.html?id=${sheet.id}" class="btn-primary">Modifica</a>
                </div>
            `).join('');
        } else {
            sheetsList.innerHTML = ''; // Lasciare vuoto per un layout pulito
        }
        // Popola la select nel modal join campaign 
        const joinSheetSelect = document.getElementById('join-campaign-sheet-id');
        if (joinSheetSelect) {
            joinSheetSelect.innerHTML = '<option value="" disabled selected>Seleziona una scheda...</option>';
            sheets.forEach(sheet => {
                joinSheetSelect.innerHTML += `<option value="${sheet.id}">${sheet.name} (Lvl ${sheet.primaryLevel})</option>`;
            });
        }
    } else if (sheetsList) {
        sheetsList.innerHTML = '<p>Errore nel caricamento delle schede.</p>';
    }

    const joinedCampaigns = await apiCall('/api/player/campaigns/joined');
    const campaignsList = document.getElementById('player-campaigns-list');

    if (joinedCampaigns && campaignsList) {
        if (joinedCampaigns.length > 0) {
            campaignsList.innerHTML = joinedCampaigns.map(jc => `
                <div class="card">
                    <h3>${jc.campaignTitle || 'Campagna sconosciuta'}</h3>
                    
                    <p>Usando: ${jc.characterUsed?.name || '?'} (Lvl ${jc.characterUsed?.primaryLevel || '?'})</p>
                    <a href="player-campaign-detail.html?id=${jc.campaignId}" class="btn-primary">Entra</a>
                </div>
            `).join('');
        } else {
            campaignsList.innerHTML = ''; // Lasciare vuoto per un layout pulito
        }
    } else if (campaignsList) {
        campaignsList.innerHTML = '<p>Errore nel caricamento delle campagne.</p>';
    }
    const orphanedCampaigns = await apiCall('/api/player/campaigns/orphaned');
    const orphanedList = document.getElementById('player-orphaned-campaigns-list');
    const orphanedSection = orphanedList?.closest('.dashboard-section');
    if (orphanedCampaigns && orphanedList && orphanedSection) {
        if (orphanedCampaigns.length > 0) {
            orphanedList.innerHTML = orphanedCampaigns.map(oc => `
                 <div class="card">
                     <h3>${oc.campaignTitle || 'Campagna sconosciuta'} (Orfana!)</h3>
                     <p>ID Camp: ${oc.campaignId}</p>
                     <p>Invita un nuovo Master con:</p>
                     <span class="code">${oc.inviteMastersCode}</span>
                     <small>Scadenza: ${new Date(oc.deletionDeadline).toLocaleString()}</small>
                 </div>
             `).join('');
            orphanedSection.style.display = 'block';
        } else {
            orphanedList.innerHTML = '';
            orphanedSection.style.display = 'none';
        }
    } else if (orphanedSection) {
        orphanedSection.style.display = 'none';
    }
    const proposals = await apiCall('/api/player/proposals');
    const proposalsList = document.getElementById('player-proposals-list');
    const proposalsSection = proposalsList?.closest('.dashboard-section');
    if (proposals && proposalsList && proposalsSection) {
        if (proposals.length > 0) {
            proposalsList.innerHTML = proposals.map(p => `
                <div class="card proposal-card">
                    <h3>${p.campaignTitle || '?'}</h3>
                    <p>Data Proposta: ${new Date(p.proposedDate).toLocaleString()}</p>
                    <p>Scadenza Voto: ${new Date(p.expiresOn).toLocaleString()}</p>
                    ${p.hasVoted
                    ? '<span class="voted">Votato ✔️</span>'
                    : `<button class="btn-primary" onclick="handleVoteProposal(${p.proposalId})">Vota Sì</button>`
                }
                </div>
            `).join('');
            proposalsSection.style.display = 'block';
        } else {
            proposalsList.innerHTML = '<p>Nessuna proposta di sessione attiva al momento.</p>';
            proposalsSection.style.display = 'block';
        }
    } else if (proposalsSection) {
        proposalsSection.style.display = 'none';
    }
}