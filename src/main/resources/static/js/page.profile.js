import { apiCall } from './modules/api.js';
import {
  isAuthenticated,
  isPlayer,
  isMaster,
  saveLoginData,
  handleLogout,
  getCurrentUserFromStorage,
} from './modules/auth.js';
import { updateGeneralUI, initLogoNavigation } from './modules/ui.js';

function updatePreview(imageUrl) {
  const previewEl = document.getElementById('profile-image-preview');
  // Usa l'URL fornito, o un'immagine di default (dice.png) se l'URL è vuoto/nullo
  const src = imageUrl || 'images/dice.png';
  if (previewEl && src) {
    previewEl.src = src;
  }
}

/**
 * Setup per la navigazione tra le sezioni del profilo
 */
function setupProfileNavigation() {
  const navItems = document.querySelectorAll('.profile-nav-item');
  const contentPanels = document.querySelectorAll('.profile-content-panel');

  navItems.forEach((item) => {
    item.addEventListener('click', (e) => {
      e.preventDefault();

      const target = item.dataset.target;

      // Rimuovi la classe active da tutti i nav items
      navItems.forEach((nav) => nav.classList.remove('active'));
      // Aggiungi active al nav item cliccato
      item.classList.add('active');

      // Nascondi tutti i content panels
      contentPanels.forEach((panel) => panel.classList.remove('active'));
      // Mostra il panel target
      const targetPanel = document.getElementById(`profile-content-${target}`);
      if (targetPanel) {
        targetPanel.classList.add('active');
      }
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  // --- GUARDIA ---
  if (!isAuthenticated()) {
    window.location.replace('error403.html');
    return;
  }

  updateGeneralUI(); // Aggiorna la navbar
  initLogoNavigation();

  // Setup profile navigation
  setupProfileNavigation();

  // Popola i form con i dati utente attuali
  const user = getCurrentUserFromStorage();
  if (user) {
    document.getElementById('profile-username').value = user.username;
    document.getElementById('profile-email').value = user.email;
    const imageUrlInput = document.getElementById('profile-image-url');
    if (imageUrlInput) {
      imageUrlInput.value = user.profileImageUrl || '';

      // 1. CHIAMATA INIZIALE: Popola l'anteprima al caricamento della pagina
      updatePreview(user.profileImageUrl);

      // 2. AGGIUNGI LISTENER: Aggiorna l'anteprima ogni volta che l'utente scrive
      imageUrlInput.addEventListener('input', (e) => {
        updatePreview(e.target.value);
      });
    }
  }

  // --- GESTIONE DINAMICA BOTTONI RUOLO (MODIFICATA) ---
  const btnPlayer = document.getElementById('btn-become-player');
  const btnMaster = document.getElementById('btn-become-master');

  // --- NUOVA LOGICA: Se hai entrambi i ruoli, nascondi tutto ---
  if (isPlayer() && isMaster()) {
    const subtitle = document.getElementById('dashboard-subtitle');
    if (subtitle) subtitle.style.display = 'none';

    const profileActions = document.getElementById('profile-actions');
    if (profileActions) profileActions.style.display = 'none';
  } else {
    // --- VECCHIA LOGICA (semplificata) ---
    // (Se non hai entrambi i ruoli, mostra i bottoni normalmente)

    if (isPlayer()) {
      btnPlayer.textContent = 'Già Player (Vai alla Dashboard)';
      // (Rimosse le righe classList.remove/add)
      btnPlayer.addEventListener('click', () => {
        // Cambia azione
        window.location.href = 'player.html';
      });
    } else {
      // Aggiunge listener solo se non è già player
      btnPlayer.addEventListener('click', () => handleBecomeRole('player'));
    }

    if (isMaster()) {
      btnMaster.textContent = 'Già Master (Vai alla Dashboard)';
      // (Rimosse le righe classList.remove/add)
      btnMaster.addEventListener('click', () => {
        // Cambia azione
        window.location.href = 'master.html';
      });
    } else {
      // Aggiunge listener solo se non è già master
      btnMaster.addEventListener('click', () => handleBecomeRole('master'));
    }
  }
  // --- FINE MODIFICA ---

  // Listener Form Modifica Profilo
  document
    .getElementById('profileEditForm')
    ?.addEventListener('submit', handleProfileUpdate);

  // Listener Form Modifica Password
  document
    .getElementById('passwordChangeForm')
    ?.addEventListener('submit', handlePasswordChange);

  // Listener Logout
  document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-button') handleLogout(e);
  });
});

// Handler "Diventa Ruolo" (invariato)
async function handleBecomeRole(role) {
  const endpoint =
    role === 'player'
      ? '/api/profile/become-player'
      : '/api/profile/become-master';
  const profileMessage = document.getElementById('profile-message');
  if (profileMessage) {
    profileMessage.textContent = 'Aggiornamento ruolo...';
    profileMessage.className = 'info-message show';
  }
  const data = await apiCall(endpoint, 'POST');
  if (data && data.token) {
    saveLoginData(data.token, data);
    if (profileMessage) {
      profileMessage.textContent = 'Ruolo assegnato! Reindirizzamento...';
      profileMessage.className = 'success-message show';
    }
    const targetPage = role === 'player' ? 'player.html' : 'master.html';
    window.location.replace(targetPage); // Ricarica la pagina
  } else if (profileMessage) {
    profileMessage.textContent =
      "Operazione fallita. L'errore è stato mostrato in un popup.";
    profileMessage.className = 'error-message show';
  }
}

// Handler Modifica Profilo (invariato)
async function handleProfileUpdate(event) {
  event.preventDefault();
  const errorDiv = document.getElementById('profileEditError');
  const successDiv = document.getElementById('profileEditSuccess');
  errorDiv.style.display = 'none';
  successDiv.style.display = 'none';

  const newUsername = document.getElementById('profile-username').value;
  const newEmail = document.getElementById('profile-email').value;
  const newImageUrl = document.getElementById('profile-image-url').value;

  const data = await apiCall('/api/user/profile', 'PUT', {
    newUsername,
    newEmail,
    newImageUrl,
  });

  if (data && data.token) {
    saveLoginData(data.token, data);
    updateGeneralUI(); // Aggiorna la navbar con i nuovi dati
    successDiv.textContent = 'Profilo aggiornato con successo!';
    successDiv.style.display = 'block';
  } else {
    errorDiv.textContent = data?.message || 'Errore sconosciuto';
    errorDiv.style.display = 'block';
  }
}

// Handler Modifica Password (invariato)
async function handlePasswordChange(event) {
  event.preventDefault();
  const errorDiv = document.getElementById('passwordChangeError');
  const successDiv = document.getElementById('passwordChangeSuccess');
  errorDiv.style.display = 'none';
  successDiv.style.display = 'none';

  const oldPassword = document.getElementById('profile-old-password').value;
  const newPassword = document.getElementById('profile-new-password').value;

  const data = await apiCall('/api/user/password', 'PUT', {
    oldPassword,
    newPassword,
  });

  if (data && data.message.includes('successo')) {
    successDiv.textContent = data.message;
    successDiv.style.display = 'block';
    event.target.reset();
  } else {
    errorDiv.textContent = data?.message || 'Errore sconosciuto';
    errorDiv.style.display = 'block';
  }
}
