import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, handleLogout } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

let campaignId = null;

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
    if (!isAuthenticated()) {
        window.location.replace('landing.html');
        return;
    }
    if (!isPlayer()) {
        window.location.replace('profile.html');
        return;
    }

    const params = new URLSearchParams(window.location.search);
    campaignId = params.get('id');
    if (!campaignId) {
        alert("ID campagna non specificato.");
        window.location.href = 'player.html';
        return;
    }
    
    updateGeneralUI();
    loadCampaignData();

    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });
});


/**
 * Carica tutti i dati della campagna (chiamata singola)
 */
async function loadCampaignData() {
    const data = await apiCall(`/api/player/campaigns/${campaignId}`);

    if (data) {
        // Popola Info Base
        document.getElementById('campaign-name-title').textContent = data.title || "Dettaglio Campagna";
        document.getElementById('master-username').textContent = data.masterUsername || "N/D";
        document.getElementById('campaign-description').textContent = data.description || "Nessuna descrizione.";
        
        // Popola Date Confermate
        if(data.startDate) {
            document.getElementById('start-date-text').textContent = new Date(data.startDate).toLocaleDateString();
        }
        if(data.scheduledNextSession) {
            document.getElementById('next-session-text').textContent = new Date(data.scheduledNextSession).toLocaleDateString();
        }

        // Popola Lista Giocatori
        populatePlayerList(data.fellowPlayers || []);
        
        // Popola Proposte di Voto
        populateProposalList(data.activeProposals || []);
    }
    // else: apiCall ha già gestito l'errore e reindirizzato
}

/**
 * Popola la tabella dei giocatori
 */
function populatePlayerList(players) {
    const tbody = document.getElementById('player-list-tbody');
    if (!tbody) return;
    
    if (players.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3">Nessun giocatore in questa campagna.</td></tr>';
        return;
    }
    
    tbody.innerHTML = players.map(p => `
        <tr>
            <td>${p.username}</td>
            <td>${p.characterName} (ID: ${p.characterId})</td>
            <td>${p.characterClass} / Lvl ${p.characterLevel}</td>
            </tr>
    `).join('');
}

/**
 * Popola la lista delle proposte di sessione per votare
 */
function populateProposalList(proposals) {
    const container = document.getElementById('proposal-list-container');
    if (!container) return;

    if (proposals.length === 0) {
        container.innerHTML = '<p>Nessuna proposta da votare.</p>';
        return;
    }

    // Usiamo lo stesso layout di card di player.html
    container.innerHTML = proposals.map(p => `
        <div class="card proposal-card" style="max-width: 100%;">
            <h3>${p.campaignTitle || 'Votazione'}</h3>
            <p>Data Proposta: ${new Date(p.proposedDate).toLocaleString()}</p>
            <p>Scadenza Voto: ${new Date(p.expiresOn).toLocaleString()}</p>
            ${p.hasVoted
                ? '<span class="voted">Votato ✔️</span>'
                : `<button class="btn-primary" onclick="handleVoteProposal(${p.proposalId})">Vota Sì</button>`
             }
        </div>
    `).join('');
}

// Handler per il voto (uguale a page.player.js)
async function handleVoteProposal(proposalId) {
     console.log("Voto per proposta:", proposalId);
     const data = await apiCall(`/api/player/proposals/${proposalId}/vote`, 'POST');
     if (data) {
         alert("Voto registrato!");
         loadCampaignData(); // Ricarica i dati della pagina
     }
}
// Rendi l'handler accessibile globalmente per l'onclick
window.handleVoteProposal = handleVoteProposal;