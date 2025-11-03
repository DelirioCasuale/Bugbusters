import { apiCall } from './modules/api.js';
import { isAuthenticated, isMaster, handleLogout } from './modules/auth.js';
import { Modal, updateGeneralUI } from './modules/ui.js';

// Dichiarazione delle variabili per i modali
let createCampaignModal;
let claimCampaignModal;
let confirmationModal, infoModal;

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

  createCampaignModal = new Modal('createCampaignModal');
  claimCampaignModal = new Modal('claimCampaignModal');
  confirmationModal = new Modal('confirmationModal');
  infoModal = new Modal('infoModal');

  // NUOVO LISTENER: Rende le card di aggiunta cliccabili
  document.getElementById('card-show-create-campaign-modal')?.addEventListener('click', () => createCampaignModal?.show());
  document.getElementById('card-show-claim-campaign-modal')?.addEventListener('click', () => claimCampaignModal?.show());

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

// 3. NUOVA FUNZIONE HELPER: Per mostrare il modal di Notifica (Successo/Errore)
function showInfoModal(title, text, isError = false) {
  const titleEl = document.getElementById('infoModalTitle');
  const textEl = document.getElementById('infoModalText');

  if (titleEl) {
    titleEl.textContent = title;
    // Se è un errore, colora il titolo di rosso (come da tue variabili CSS)
    titleEl.style.color = isError ? 'var(--error-color)' : 'var(--primary-purple-light)';
  }
  if (textEl) {
    textEl.textContent = text;
  }

  infoModal?.show();
}


// 4. HANDLER ELIMINAZIONE (Logica completamente aggiornata)
// (Rimuove async, ora gestisce solo l'APERTURA del modal di conferma)
function handleDeleteCampaign(campaignId, campaignTitle) {

  // Testo di conferma personalizzato
  const confirmationText = `Sei sicuro di voler eliminare la campagna "${campaignTitle}"?\n\nLa campagna sarà eliminata SOLO se è finita O non ha giocatori.`;

  // Imposta il testo nel modal di conferma
  document.getElementById('confirmationModalTitle').textContent = "Conferma Eliminazione";
  document.getElementById('confirmationModalText').textContent = confirmationText;

  const confirmBtn = document.getElementById('confirmationModalConfirmBtn');

  // TRUCCO per evitare listener multipli: 
  // Cloniamo il bottone per rimuovere vecchi listener 'onclick'
  const newConfirmBtn = confirmBtn.cloneNode(true);
  confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

  // Aggiungiamo il listener per l'azione REALE
  newConfirmBtn.onclick = async () => {
    // 1. Chiudi il modal di conferma
    confirmationModal.hide();

    // 2. Esegui la chiamata API
    const data = await apiCall(`/api/master/campaigns/${campaignId}`, 'DELETE');

    // 3. Gestisci la risposta con il modal INFO

    // Caso A: Errore 4xx (Es. 400 Bad Request dal Service)
    if (data && data.status) {
      // Messaggio di errore (es. "Impossibile eliminare: La campagna ha ancora giocatori attivi...")
      showInfoModal("Eliminazione Fallita", data.message, true); // true = isError
    }
    // Caso B: Successo (l'API ha restituito il MessageResponse)
    else if (data && data.message) {
      // Messaggio di successo (es. "Campagna eliminata con successo.")
      showInfoModal("Operazione Riuscita", data.message, false);
      loadMasterData(); // Ricarica la lista
    }
    // Caso C: Errore di rete (apiCall ha restituito null e già gestito il 401/403)
    else if (data === null) {
      // Non fare nulla, apiCall ha già gestito l'errore di rete o autenticazione.
    }
  };

  // 4. Mostra il modal di conferma
  confirmationModal.show();
}
window.handleDeleteCampaign = handleDeleteCampaign; // Rende la funzione accessibile dall'HTML


async function handleCreateCampaign(event) {
  event.preventDefault();
  if (!createCampaignModal) return;

  // 1. Usa la funzione hideError() del modale per pulire i messaggi precedenti
  createCampaignModal.hideError();

  const title = document.getElementById('create-campaign-title')?.value;
  const description = document.getElementById(
    'create-campaign-description'
  )?.value;

  // 2. Controllo specifico per l'avviso
  if (!title || !description) {
    // Se i campi sono vuoti, usa showError() che imposterà il div.error-message nel modale
    createCampaignModal.showError('Titolo e descrizione della campagna sono obbligatori.');
    return;
  }

  // La funzione apiCall, se fallisce con un errore 4xx (es. validazione lato server),
  // restituirà un oggetto {status, message}, che verrà gestito dal blocco 'else if' sottostante.
  const data = await apiCall('/api/master/campaigns', 'POST', {
    title,
    description,
  });

  // 3. Gestione della risposta API
  if (data && data.status) { // Se è un oggetto errore restituito da apiCall (es. 400 Bad Request)
    createCampaignModal.showError(data.message || 'Si è verificato un errore durante la creazione.');
    return;
  }

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

  // Chiama apiCall (che ora restituisce l'oggetto errore in caso di 4xx)
  const data = await apiCall('/api/master/campaigns/claim', 'POST', {
    inviteMastersCode,
  });

  // VERIFICA SE DATA È UN OGGETTO DI ERRORE (contiene 'message' ma NON 'token')
  if (data && data.status) {
    // Se è un errore 4xx (gestito dal service, es. Codice non valido, già master, ecc.)
    // Mostra il messaggio nella modale e termina.
    claimCampaignModal.showError(data.message || 'Errore nella richiesta.');
    return;
  }

  // Se 'data' esiste, e non era un oggetto errore, allora è successo
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
                    
                    <p>Codice Player: <span class="code">${c.invitePlayersCode}</span></p>
                    <p>Codice Master: <span class="code">${c.inviteMastersCode}</span></p>
                    
                    <div class="btn-group" style="margin-top: 15px;">
                       <button class="btn-primary" onclick="viewCampaignDetails(${c.id})">Gestisci</button>
                       
                       <button class="btn-primary btn-delete-custom" 
                           onclick="handleDeleteCampaign(${c.id}, '${c.title.replace(/'/g, "\\'")}')">Elimina</button>
                    </div>
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


