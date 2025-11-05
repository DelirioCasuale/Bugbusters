import { apiCall } from './modules/api.js';
import { isAuthenticated, isMaster, handleLogout } from './modules/auth.js';
import { updateGeneralUI, initLogoNavigation } from './modules/ui.js';

let currentCampaignId = null;

/**
 * Gestisce la risposta API con logica di errore migliorata
 * @param {*} data - Risposta dell'API
 * @param {string} operation - Descrizione dell'operazione per il log
 * @returns {boolean} - true se i dati sono validi, false altrimenti
 */
function handleApiResponse(data, operation = 'API call') {
  console.log(`handleApiResponse for ${operation}:`, data);

  if (data === null) {
    // API call fallita - potrebbe essere 403, 401, 404, etc.
    // Se l'utente è autenticato ma la richiesta è fallita,
    // assumiamo che sia un problema di autorizzazione (403)
    console.log(`${operation} failed - redirecting to error403`);
    window.location.href = 'error403.html';
    return false;
  }

  if (
    data === undefined ||
    (typeof data === 'object' && Object.keys(data).length === 0)
  ) {
    // Risposta vuota ma API call riuscita - risorsa non trovata (404)
    console.log(`${operation} returned empty data - redirecting to error404`);
    window.location.href = 'error404.html';
    return false;
  }

  return true;
}

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
  if (!isAuthenticated()) {
    window.location.replace('landing.html');
    return;
  }
  if (!isMaster()) {
    window.location.replace('master.html');
    return;
  }
  const params = new URLSearchParams(window.location.search);
  campaignId = params.get('id');
  if (!campaignId) {
    window.location.href = 'master.html';
    return;
  }

  updateGeneralUI();
  initLogoNavigation();
  loadAllCampaignData();

  document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-button') handleLogout(e);
  });
  document
    .getElementById('editCampaignForm')
    ?.addEventListener('submit', handleUpdateDetails);
  document
    .getElementById('setStartDateForm')
    ?.addEventListener('submit', handleSetStartDate);
  document
    .getElementById('proposeSessionForm')
    ?.addEventListener('submit', handleProposeSession);
});

async function loadAllCampaignData() {
  const [campaignData, proposalsData] = await Promise.all([
    apiCall(`/api/master/campaigns/${campaignId}`, 'GET', null, false), // Silent API call
    apiCall(
      `/api/master/campaigns/${campaignId}/proposals`,
      'GET',
      null,
      false
    ), // Silent API call
  ]);

  // Check campaign data with improved error handling
  if (!handleApiResponse(campaignData, 'Campaign data load')) {
    return;
  }

  populateCampaignDetails(campaignData);
  populatePlayerList(campaignData.players || []);

  if (proposalsData) {
    populateProposalList(proposalsData);
  }
}

function populateCampaignDetails(campaign) {
  document.getElementById('campaign-name-title').textContent = campaign.title;
  document.getElementById('campaign-title').value = campaign.title;
  document.getElementById('campaign-description').value = campaign.description;
  const startDateInput = document.getElementById('start-date');
  if (campaign.startDate) {
    startDateInput.value = campaign.startDate;
    startDateInput.disabled = true;
    document.querySelector('#setStartDateForm button').style.display = 'none';
    document.getElementById(
      'startDateSuccess'
    ).textContent = `Data inizio: ${campaign.startDate}`;
    document.getElementById('startDateSuccess').style.display = 'block';
  }
}

function populatePlayerList(players) {
  const tbody = document.getElementById('player-list-tbody');
  if (!tbody) return;
  if (players.length === 0) {
    tbody.innerHTML =
      '<tr><td colspan="4">Nessun giocatore si è ancora unito.</td></tr>';
    return;
  }
  tbody.innerHTML = players
    .map(
      (p) => `
        <tr>
            <td>${p.username}</td>
            <td>${p.characterName} (ID: ${p.characterId})</td>
            <td>${p.characterClass} / Lvl ${p.characterLevel}</td>
            <td>
                <button class="action-button kick" onclick="handleKickPlayer(${p.characterId}, '${p.characterName}')">Espelli</button>
            </td>
        </tr>
    `
    )
    .join('');
}

function populateProposalList(proposals) {
  const container = document.getElementById('proposal-list-container');
  if (!container) return;
  if (proposals.length === 0) {
    container.innerHTML = '<p>Nessuna proposta creata.</p>';
    return;
  }
  container.innerHTML = proposals
    .map(
      (p) => `
        <div class="proposal-item ${p.confirmed ? 'confirmed' : ''}">
            <p><strong>${new Date(p.proposedDate).toLocaleString()}</strong>
               ${p.confirmed ? '(Confermata)' : `(Voti: ${p.voteCount})`}
            </p>
            <small>Scade: ${new Date(p.expiresOn).toLocaleString()}</small>
        </div>
    `
    )
    .join('');
}

async function handleUpdateDetails(event) {
  event.preventDefault();
  const title = document.getElementById('campaign-title').value;
  const description = document.getElementById('campaign-description').value;
  const successDiv = document.getElementById('editCampaignSuccess');
  const data = await apiCall(`/api/master/campaigns/${campaignId}`, 'PUT', {
    title,
    description,
  });
  if (data) {
    successDiv.textContent = 'Dettagli aggiornati!';
    successDiv.style.display = 'block';
    document.getElementById('campaign-name-title').textContent = data.title;
    setTimeout(() => (successDiv.style.display = 'none'), 3000);
  }
}

async function handleSetStartDate(event) {
  event.preventDefault();
  const startDate = document.getElementById('start-date').value;
  const successDiv = document.getElementById('startDateSuccess');
  if (!startDate) return;
  const data = await apiCall(
    `/api/master/campaigns/${campaignId}/start-date`,
    'PATCH',
    { startDate }
  );
  if (data) {
    successDiv.textContent = data.message;
    successDiv.style.display = 'block';
    document.getElementById('start-date').disabled = true;
    event.target.querySelector('button').style.display = 'none';
  }
}

async function handleProposeSession(event) {
  event.preventDefault();
  const proposedDate = document.getElementById('propose-date').value;
  const successDiv = document.getElementById('proposeSuccess');
  if (!proposedDate) return;
  const data = await apiCall(
    `/api/master/campaigns/${campaignId}/propose-session`,
    'POST',
    { proposedDate }
  );
  if (data) {
    successDiv.textContent = 'Proposta inviata!';
    successDiv.style.display = 'block';
    event.target.reset();
    setTimeout(() => (successDiv.style.display = 'none'), 3000);
    const proposalsData = await apiCall(
      `/api/master/campaigns/${campaignId}/proposals`
    );
    if (proposalsData) {
      populateProposalList(proposalsData);
    }
  }
}

async function handleKickPlayer(characterId, characterName) {
  if (
    confirm(
      `Sei sicuro di voler espellere ${characterName} (ID: ${characterId}) da questa campagna?`
    )
  ) {
    const data = await apiCall(
      `/api/master/campaigns/${campaignId}/players/${characterId}`,
      'DELETE'
    );
    if (data) {
      loadAllCampaignData(); // Ricarica tutto
    }
  }
}
window.handleKickPlayer = handleKickPlayer;
