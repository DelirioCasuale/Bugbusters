import { apiCall } from './modules/api.js';
import { isAuthenticated, isMaster, handleLogout } from './modules/auth.js';
import { Modal, updateGeneralUI } from './modules/ui.js';

// --- GUARDIA DI AUTENTICAZIONE ---
if (!isAuthenticated()) {
    console.warn("Utente non autenticato. Reindirizzamento a landing.html");
    window.location.replace('landing.html');
} else if (!isMaster()) {
    // Se è loggato ma non è master, manda a scegliere il ruolo
    console.warn("Utente non master su master.html. Reindirizzamento a profile.html");
    window.location.replace('profile.html');
}
// ---------------------------------

let createCampaignModal, claimCampaignModal;

document.addEventListener('DOMContentLoaded', () => {
    updateGeneralUI();
    
    // Istanzia Modali
    createCampaignModal = new Modal('createCampaignModal');
    claimCampaignModal = new Modal('claimCampaignModal');

    // Listener per aprire modali
    document.getElementById('btn-show-create-campaign-modal')?.addEventListener('click', () => createCampaignModal?.show());
    document.getElementById('btn-show-claim-campaign-modal')?.addEventListener('click', () => claimCampaignModal?.show());

    // Listener per submit form
    document.getElementById('createCampaignForm')?.addEventListener('submit', handleCreateCampaign);
    document.getElementById('claimCampaignForm')?.addEventListener('submit', handleClaimCampaign);

    // Listener Logout
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    // Carica dati iniziali
    loadMasterData();
});

// Handlers specifici
async function handleCreateCampaign(event) {
    event.preventDefault();
    if (!createCampaignModal) return;
    createCampaignModal.hideError();
    const title = document.getElementById('create-campaign-title')?.value;
    const description = document.getElementById('create-campaign-description')?.value;
     if (!title || !description) {
         createCampaignModal.showError("Titolo e descrizione sono obbligatori.");
         return;
     }
     const data = await apiCall('/api/master/campaigns', 'POST', { title, description });
     if(data) {
         createCampaignModal.hide();
         loadMasterData(); 
     }
}
async function handleClaimCampaign(event) {
    event.preventDefault();
    if (!claimCampaignModal) return;
    claimCampaignModal.hideError();
    const inviteMastersCode = document.getElementById('claim-campaign-code')?.value;
      if (!inviteMastersCode) {
          claimCampaignModal.showError("Il codice invito master è obbligatorio.");
          return;
      }
      const data = await apiCall('/api/master/campaigns/claim', 'POST', { inviteMastersCode });
      if(data) {
          claimCampaignModal.hide();
          loadMasterData();
      }
}
// Rende viewCampaignDetails accessibile globalmente per l'onclick
window.viewCampaignDetails = (campaignId) => {
     alert(`Funzionalità "Gestisci Campagna ${campaignId}" non ancora implementata.`);
}

// Funzione di caricamento dati
async function loadMasterData() {
    console.log("Caricamento dati Master...");
    const campaigns = await apiCall('/api/master/campaigns');
    const campaignsList = document.getElementById('master-campaigns-list');
    if (campaigns && campaignsList) {
         if(campaigns.length > 0) {
            campaignsList.innerHTML = campaigns.map(c => `
                <div class="card">
                    <h3>${c.title || 'Senza Titolo'}</h3>
                    <p>ID: ${c.id}</p>
                    <p>Codice Player: <span class="code">${c.invitePlayersCode}</span></p>
                    <p>Codice Master: <span class="code">${c.inviteMastersCode}</span></p>
                     <button class="btn-primary" onclick="viewCampaignDetails(${c.id})">Gestisci (WIP)</button>
                </div>
            `).join('');
         } else {
              campaignsList.innerHTML = '<p>Non hai ancora creato nessuna campagna.</p>';
         }
    } else if (campaignsList) {
         campaignsList.innerHTML = '<p>Errore nel caricamento delle campagne.</p>';
    }
}