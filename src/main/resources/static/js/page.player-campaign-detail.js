import { apiCall } from './modules/api.js';
import {
  isAuthenticated,
  isPlayer,
  handleLogout,
  getCurrentUserFromStorage,
} from './modules/auth.js';
import { updateGeneralUI, initLogoNavigation } from './modules/ui.js';

let campaignId = null;
let currentUserId = null; // Salviamo l'ID dell'utente loggato

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
  const user = getCurrentUserFromStorage();
  if (!user) {
    console.warn('Utente non autenticato. Reindirizzamento a landing.html');
    window.location.replace('landing.html');
    return;
  }
  currentUserId = user.id; // Salva l'ID utente per evidenziarlo

  if (!isPlayer()) {
    console.warn('Utente non player su pagina dettaglio. Reindirizzamento...');
    window.location.replace('profile.html');
    return;
  }

  const params = new URLSearchParams(window.location.search);
  campaignId = params.get('id');
  if (!campaignId) {
    alert('ID campagna non specificato.');
    window.location.href = 'player.html';
    return;
  }

  updateGeneralUI();
  initLogoNavigation();
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
    document.getElementById('campaign-name-title').textContent =
      data.title || 'Dettaglio Campagna';
    document.getElementById('master-username').textContent =
      data.masterUsername || 'N/D';
    document.getElementById('campaign-description').textContent =
      data.description || 'Nessuna descrizione.';

    if (data.startDate) {
      document.getElementById('start-date-text').textContent = new Date(
        data.startDate
      ).toLocaleDateString();
    }
    if (data.scheduledNextSession) {
      document.getElementById('next-session-text').textContent = new Date(
        data.scheduledNextSession
      ).toLocaleDateString();
    }

    // --- CORREZIONE BUG GIOCATORI ---
    // Il DTO ora invia 'players', non 'fellowPlayers'
    populatePlayerList(data.players || []);

    // --- LOGICA PROPOSTE DIVISA ---
    populateProposalList(data.activeProposals || []);
    populatePastProposalList(data.pastProposals || []);
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
    tbody.innerHTML =
      '<tr><td colspan="3">Nessun giocatore in questa campagna.</td></tr>';
    return;
  }

  tbody.innerHTML = players
    .map(
      (p) => `
        <tr class="${
          p.playerId === currentUserId ? 'highlight-row' : ''
        }"> <td>${p.username} ${p.playerId === currentUserId ? '(Tu)' : ''}</td>
            <td>${p.characterName} (ID: ${p.characterId})</td>
            <td>${p.characterClass} / Lvl ${p.characterLevel}</td>
        </tr>
    `
    )
    .join('');
}

/**
 * Popola la lista delle proposte di sessione ATTIVE (da votare)
 */
function populateProposalList(proposals) {
  const container = document.getElementById('proposal-list-container');
  if (!container) return;

  if (proposals.length === 0) {
    container.innerHTML = '<p>Nessuna proposta da votare.</p>';
    return;
  }

  // Il backend ha già filtrato, mostriamo solo il pulsante di voto
  container.innerHTML = proposals
    .map(
      (p) => `
        <div class="card proposal-card">
            <h3>Votazione Aperta</h3>
            <p>Data Proposta: ${new Date(p.proposedDate).toLocaleString()}</p>
            <p>Scadenza Voto: ${new Date(p.expiresOn).toLocaleString()}</p>
            <button class="btn-primary" onclick="handleVoteProposal(${
              p.proposalId
            })">Vota Sì</button>
        </div>
    `
    )
    .join('');
}

/**
 * NUOVA FUNZIONE: Popola la lista delle proposte PASSATE (scadute, votate, confermate)
 */
function populatePastProposalList(proposals) {
  const container = document.getElementById('past-proposal-list-container');
  if (!container) return;

  if (proposals.length === 0) {
    container.innerHTML = '<p>Nessuna proposta passata.</p>';
    return;
  }

  container.innerHTML = proposals
    .map((p) => {
      let statusText = 'Scaduta'; // Default se non confermata e non votata
      if (p.isConfirmed) statusText = 'Confermata ✔️';
      else if (p.hasVoted) statusText = 'Votato';

      return `
        <div class="proposal-item ${p.isConfirmed ? 'confirmed' : ''}">
            <p><strong>${new Date(p.proposedDate).toLocaleString()}</strong>
               (${statusText})
            </p>
            <small>ID Proposta: ${p.proposalId}</small>
        </div>
    `;
    })
    .join('');
}

// Handler per il voto (uguale a page.player.js)
async function handleVoteProposal(proposalId) {
  console.log('Voto per proposta:', proposalId);
  const data = await apiCall(
    `/api/player/proposals/${proposalId}/vote`,
    'POST'
  );
  if (data) {
    alert('Voto registrato!');
    loadCampaignData(); // Ricarica i dati della pagina
  }
}
// Rendi l'handler accessibile globalmente per l'onclick
window.handleVoteProposal = handleVoteProposal;
