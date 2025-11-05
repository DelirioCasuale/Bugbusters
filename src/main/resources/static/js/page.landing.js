import { apiCall } from './modules/api.js';
import {
  saveLoginData,
  isAuthenticated,
  isAdmin,
  isPlayer,
  isMaster,
  handleLogout,
} from './modules/auth.js';
import { Modal, updateGeneralUI } from './modules/ui.js';

let loginModal;

document.addEventListener('DOMContentLoaded', () => {
  // 1. Inizializza UI (Navbar)
  updateGeneralUI();
  loginModal = new Modal('loginModal');

  // 2. MODIFICA 2: Aggiunta la chiamata per aggiornare i bottoni del banner
  updateBannerButtons();

  // 3. Aggiungi Listener (Form, bottoni, ecc.)
  document.querySelectorAll('.login-trigger').forEach(
    (el) =>
      (el.onclick = (e) => {
        e.preventDefault();
        loginModal?.show();
      })
  );
  document.getElementById('loginForm')?.addEventListener('submit', handleLogin);
  document.getElementById('contactForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    alert('Supporto (WIP): Messaggio inviato! (non ancora implementato)');
    document.getElementById('contactForm').reset();
  });
  document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-button') handleLogout(e);
  });

  // --- NUOVA AGGIUNTA: Gestione Toggle Password (Copiato da register.js) ---
  const toggleButtons = document.querySelectorAll('.password-toggle-btn');

  toggleButtons.forEach((button) => {
    const targetInput = document.getElementById(button.dataset.target);
    const icon = button.querySelector('i');

    if (!targetInput) return;

    // Mostra password (tieni premuto)
    button.addEventListener('mousedown', (e) => {
      e.preventDefault();
      targetInput.type = 'text';
      icon.classList.remove('fa-eye');
      icon.classList.add('fa-eye-slash');
    });

    // Nascondi password (rilascia)
    button.addEventListener('mouseup', (e) => {
      e.preventDefault();
      targetInput.type = 'password';
      icon.classList.remove('fa-eye-slash');
      icon.classList.add('fa-eye');
    });

    // Gestisce il caso in cui l'utente sposti il mouse fuori dal bottone
    button.addEventListener('mouseleave', () => {
      if (targetInput.type === 'text') {
        targetInput.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
      }
    });
  });

  // --- NUOVA AGGIUNTA: Intersection Observer per animazioni on-scroll ---

  // 1. Opzioni per l'observer
  const observerOptions = {
    root: null, // usa il viewport come area di intersezione
    threshold: 0.1, // attiva l'animazione quando il 10% dell'elemento è visibile
  };

  // 2. La funzione da eseguire quando un elemento entra nel viewport
  const observerCallback = (entries, observer) => {
    entries.forEach((entry) => {
      // Se l'elemento è visibile...
      if (entry.isIntersecting) {
        // ...aggiungi la classe che attiva la transizione CSS
        entry.target.classList.add('is-visible');
        // ...e smetti di osservarlo per non ripetere l'animazione
        observer.unobserve(entry.target);
      }
    });
  };

  // 3. Crea l'observer
  const scrollObserver = new IntersectionObserver(
    observerCallback,
    observerOptions
  );

  // 4. Trova tutti gli elementi da animare (.tile e #contact) e avvia l'osservazione
  const elementsToObserve = document.querySelectorAll('.tile, #contact');
  elementsToObserve.forEach((el) => scrollObserver.observe(el));

  // --- FINE NUOVA AGGIUNTA ---
});

/**
 * Controlla se l'utente è loggato e, in caso affermativo,
 * sostituisce i pulsanti "Inizia Ora" / "Già Iscritto?"
 * con i link alle dashboard appropriate.
 */
function updateBannerButtons() {
  // Se l'utente NON è loggato, non fare nulla (i pulsanti HTML di default vanno bene)
  if (!isAuthenticated()) {
    return;
  }

  // Se l'utente È loggato, trova il contenitore dei pulsanti nel banner
  const bannerBtnGroup = document.querySelector('#banner .btn-group');
  if (!bannerBtnGroup) return;

  // Costruisci i nuovi pulsanti
  let newButtonsHTML = '';

  // Logica di priorità per i pulsanti
  const hasAdmin = isAdmin();

  if (hasAdmin) {
    newButtonsHTML +=
      '<a href="admin.html" class="btn-primary">Vista Admin</a>';
  }
  if (isMaster()) {
    // Se c'è Admin, Master è sempre secondario. Altrimenti è primario
    const btnClass = hasAdmin ? 'btn-secondary' : 'btn-primary';
    newButtonsHTML += `<a href="master.html" class="${btnClass}">Vista Master</a>`;
  }
  if (isPlayer()) {
    // Se c'è Admin, Player è sempre secondario. Altrimenti è primario
    const btnClass = hasAdmin ? 'btn-secondary' : 'btn-primary';
    newButtonsHTML += `<a href="player.html" class="${btnClass}">Vista Player</a>`;
  }

  // Se l'utente è loggato ma non ha ruoli (solo ROLE_USER)
  if (!isAdmin() && !isMaster() && !isPlayer()) {
    newButtonsHTML +=
      '<a href="profile.html" class="btn-primary">Vai al Profilo</a>';
  }

  // Sostituisci i vecchi pulsanti
  bannerBtnGroup.innerHTML = newButtonsHTML;
}

async function handleLogin(event) {
  event.preventDefault();
  if (!loginModal) return;
  loginModal.hideError();
  const username = document.getElementById('login-username')?.value;
  const password = document.getElementById('login-password')?.value;

  if (!username || !password) {
    loginModal.showError('Inserisci username e password');
    return;
  }

  const data = await apiCall('/api/auth/login', 'POST', { username, password });

  // Gestione Errore Login (Credenziali errate)
  if (data && data.status) {
    if (data.status === 401 || data.status === 403) {
      loginModal.showError(
        'Credenziali non valide. Controlla username e password.'
      );
    } else {
      loginModal.showError(data.message || 'Errore sconosciuto.');
    }
    return;
  }

  // Gestione Successo
  if (data && data.token) {
    saveLoginData(data.token, data);
    loginModal.hide();

    // MODIFICA 4: Ricarica la pagina dopo il login
    // Questo forzerà l'aggiornamento sia della navbar che dei bottoni del banner
    window.location.reload();

    /* // Vecchia logica di reindirizzamento (ora gestita dal reload)
        if (isAdmin()) {
            window.location.href = 'admin.html';
        } else if (isPlayer()) { 
            window.location.href = 'player.html';
        } else if (isMaster()) { 
            window.location.href = 'master.html';
        } else { 
            window.location.href = 'profile.html';
        }
        */
  } else if (!data) {
    loginModal.showError('Errore durante il login. Controlla la console.');
  }
}
