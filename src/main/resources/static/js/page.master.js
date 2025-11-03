import { apiCall } from './modules/api.js';
import { isAuthenticated, isMaster, handleLogout } from './modules/auth.js';
import { Modal, updateGeneralUI, initLogoNavigation } from './modules/ui.js';

// Dichiarazione delle variabili per i modali
let createCampaignModal;
let claimCampaignModal;

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
  if (!isAuthenticated()) {
    console.warn('Utente non autenticato. Reindirizzamento a landing.html');
    window.location.replace('landing.html');
    return;
  }
  if (!isMaster()) {
    console.warn(
      'Utente non master su master.html. Reindirizzamento a profile.html'
    );
    window.location.replace('profile.html');
    return;
  }
  // ---------------------------------

  // Se la guardia passa, inizializza
  updateGeneralUI();
  initLogoNavigation();

  createCampaignModal = new Modal('createCampaignModal');
  claimCampaignModal = new Modal('claimCampaignModal');

  document
    .getElementById('btn-show-create-campaign-modal')
    ?.addEventListener('click', () => createCampaignModal?.show());
  document
    .getElementById('btn-show-claim-campaign-modal')
    ?.addEventListener('click', () => claimCampaignModal?.show());

  document
    .getElementById('createCampaignForm')
    ?.addEventListener('submit', handleCreateCampaign);
  document
    .getElementById('claimCampaignForm')
    ?.addEventListener('submit', handleClaimCampaign);

  document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-button') handleLogout(e);
  });

  loadMasterData();
});

// ... (tutte le funzioni handleCreateCampaign, handleClaimCampaign,
//      viewCampaignDetails, e loadMasterData rimangono invariate) ...

async function handleCreateCampaign(event) {
  event.preventDefault();
  if (!createCampaignModal) return;
  createCampaignModal.hideError();
  const title = document.getElementById('create-campaign-title')?.value;
  const description = document.getElementById(
    'create-campaign-description'
  )?.value;
  if (!title || !description) {
    createCampaignModal.showError('Titolo e descrizione sono obbligatori.');
    return;
  }
  const data = await apiCall('/api/master/campaigns', 'POST', {
    title,
    description,
  });
  if (data) {
    createCampaignModal.hide();
    loadMasterData();
  }
}
async function handleClaimCampaign(event) {
  event.preventDefault();
  if (!claimCampaignModal) return;
  claimCampaignModal.hideError();
  const inviteMastersCode = document.getElementById(
    'claim-campaign-code'
  )?.value;
  if (!inviteMastersCode) {
    claimCampaignModal.showError('Il codice invito master è obbligatorio.');
    return;
  }
  const data = await apiCall('/api/master/campaigns/claim', 'POST', {
    inviteMastersCode,
  });
  if (data) {
    claimCampaignModal.hide();
    loadMasterData();
  }
}
window.viewCampaignDetails = (campaignId) => {
  // Reindirizza alla nuova pagina di dettaglio, passando l'ID come parametro URL
  window.location.href = `master-campaign-detail.html?id=${campaignId}`;
};
async function loadMasterData() {
  console.log('Caricamento dati Master...');
  if (!isMaster()) return;
  const campaigns = await apiCall('/api/master/campaigns');
  const campaignsList = document.getElementById('master-campaigns-list');
  if (campaigns && campaignsList) {
    if (campaigns.length > 0) {
      campaignsList.innerHTML = campaigns
        .map(
          (c) => `
                <div class="card">
                    <h3>${c.title || 'Senza Titolo'}</h3>
                    <p>ID: ${c.id}</p>
                    <p>Codice Player: <span class="code">${
                      c.invitePlayersCode
                    }</span></p>
                    <p>Codice Master: <span class="code">${
                      c.inviteMastersCode
                    }</span></p>
                     <button class="btn-primary" onclick="viewCampaignDetails(${
                       c.id
                     })">Gestisci (WIP)</button>
                </div>
            `
        )
        .join('');
    } else {
      campaignsList.innerHTML =
        '<p>Non hai ancora creato nessuna campagna.</p>';
    }
  } else if (campaignsList) {
    campaignsList.innerHTML = '<p>Errore nel caricamento delle campagne.</p>';
  }
}
