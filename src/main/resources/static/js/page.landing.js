import { apiCall } from './modules/api.js';
import {
  saveLoginData,
  isAdmin,
  isPlayer,
  isMaster,
  handleLogout,
  isAuthenticated,
  getCurrentUserFromStorage,
} from './modules/auth.js';
import { Modal, updateGeneralUI, initLogoNavigation } from './modules/ui.js';

let loginModal;

document.addEventListener('DOMContentLoaded', () => {
  // 1. Inizializza UI (Navbar e Modal)
  updateGeneralUI();
  initLogoNavigation();
  loginModal = new Modal('loginModal');

  // 2. Update landing content based on authentication status
  updateLandingContent();

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

  // 4. Add role switching button listeners
  document
    .getElementById('become-master-btn')
    ?.addEventListener('click', handleBecomeMaster);
  document
    .getElementById('become-player-btn')
    ?.addEventListener('click', handleBecomePlayer);

  // 4. Password toggle functionality
  initPasswordToggle();

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
 * Initializes password toggle functionality for the login modal
 */
function initPasswordToggle() {
  const passwordToggle = document.getElementById('password-toggle');
  const passwordInput = document.getElementById('login-password');
  const eyeIconVisible = document.getElementById('eye-icon-visible');
  const eyeIconHidden = document.getElementById('eye-icon-hidden');

  if (!passwordToggle || !passwordInput || !eyeIconVisible || !eyeIconHidden)
    return;

  passwordToggle.addEventListener('click', (e) => {
    e.preventDefault();

    const isPassword = passwordInput.type === 'password';

    // Toggle input type
    passwordInput.type = isPassword ? 'text' : 'password';

    // Toggle icon visibility
    if (isPassword) {
      // Show hidden icon (password is now visible)
      eyeIconVisible.classList.add('hidden');
      eyeIconHidden.classList.remove('hidden');
    } else {
      // Show visible icon (password is now hidden)
      eyeIconVisible.classList.remove('hidden');
      eyeIconHidden.classList.add('hidden');
    }

    // Update aria-label for accessibility
    passwordToggle.setAttribute(
      'aria-label',
      isPassword ? 'Nascondi password' : 'Mostra password'
    );

    // Briefly focus back to input for better UX
    passwordInput.focus();
  });

  // Also allow Enter key to toggle
  passwordToggle.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      passwordToggle.click();
    }
  });
}

/**
 * Updates the landing page content based on user authentication status and roles
 */
function updateLandingContent() {
  const isAuth = isAuthenticated();

  if (!isAuth) {
    // Show content for non-authenticated users
    document.getElementById('hero-not-authenticated').style.display = 'block';
    document.getElementById('hero-admin').style.display = 'none';
    document.getElementById('hero-authenticated').style.display = 'none';
    document.getElementById('about-not-authenticated').style.display = 'block';
    document.getElementById('about-authenticated').style.display = 'none';
  } else {
    // Hide non-authenticated content
    document.getElementById('hero-not-authenticated').style.display = 'none';
    document.getElementById('about-not-authenticated').style.display = 'none';

    if (isAdmin()) {
      // Show admin content
      document.getElementById('hero-admin').style.display = 'block';
      document.getElementById('hero-authenticated').style.display = 'none';
      document.getElementById('about-authenticated').style.display = 'none'; // Admins don't see about section
    } else {
      // Show authenticated user content
      document.getElementById('hero-admin').style.display = 'none';
      document.getElementById('hero-authenticated').style.display = 'block';
      document.getElementById('about-authenticated').style.display = 'block';

      // Show appropriate dashboard buttons based on roles
      const btnPlayer = document.getElementById('btn-player-dashboard');
      const btnMaster = document.getElementById('btn-master-dashboard');
      const btnBecomeMaster = document.getElementById('become-master-btn');
      const btnBecomePlayer = document.getElementById('become-player-btn');

      const hasPlayerRole = isPlayer();
      const hasMasterRole = isMaster();

      if (hasPlayerRole && hasMasterRole) {
        // User has both roles - show both dashboard buttons
        btnPlayer.style.display = 'inline-block';
        btnMaster.style.display = 'inline-block';
        btnBecomeMaster.style.display = 'none';
        btnBecomePlayer.style.display = 'none';
      } else if (hasPlayerRole) {
        // User is only a player - show player dashboard and option to become master
        btnPlayer.style.display = 'inline-block';
        btnMaster.style.display = 'none';
        btnBecomeMaster.style.display = 'inline-block';
        btnBecomePlayer.style.display = 'none';
      } else if (hasMasterRole) {
        // User is only a master - show master dashboard and option to become player
        btnPlayer.style.display = 'none';
        btnMaster.style.display = 'inline-block';
        btnBecomeMaster.style.display = 'none';
        btnBecomePlayer.style.display = 'inline-block';
      } else {
        // User has no specific role - show both role options
        btnPlayer.style.display = 'none';
        btnMaster.style.display = 'none';
        btnBecomeMaster.style.display = 'inline-block';
        btnBecomePlayer.style.display = 'inline-block';
      }
    }
  }
}

// Make updateLandingContent globally accessible for components
window.updateLandingContent = updateLandingContent;

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
  if (data && data.token) {
    saveLoginData(data.token, data); // Salva i dati PRIMA di controllare i ruoli
    loginModal.hide();

    // Update UI components
    updateGeneralUI();
    updateLandingContent();

    // Optional: comment out the redirects to stay on landing page and see dynamic content
    // Uncomment these lines if you want automatic redirection after login:
    /*
        if (isAdmin()) {
            window.location.href = 'admin.html';
        } else if (isPlayer()) { // Priorità a Player
            window.location.href = 'player.html';
        } else if (isMaster()) { // Se è *solo* master
            window.location.href = 'master.html';
        } else { // Se non è nessuno dei tre (solo ROLE_USER)
            window.location.href = 'profile.html';
        }
        */
  } else if (!data) {
    loginModal.showError('Errore durante il login. Controlla la console.');
  } else {
    loginModal.showError(data.message || 'Credenziali non valide.');
  }
}

/**
 * Handles becoming a Master (using same logic as profile page)
 */
async function handleBecomeMaster(event) {
  event.preventDefault();

  try {
    const data = await apiCall('/api/profile/become-master', 'POST');

    if (data && data.token) {
      // Save the updated token with new roles
      saveLoginData(data.token, data);

      // Direct redirect to master dashboard without alert
      window.location.replace('master.html');
    } else {
      alert("Errore durante l'aggiunta del ruolo Master.");
    }
  } catch (error) {
    console.error('Error becoming master:', error);
    alert("Errore durante l'aggiunta del ruolo Master.");
  }
}

/**
 * Handles becoming a Player (using same logic as profile page)
 */
async function handleBecomePlayer(event) {
  event.preventDefault();

  try {
    const data = await apiCall('/api/profile/become-player', 'POST');

    if (data && data.token) {
      // Save the updated token with new roles
      saveLoginData(data.token, data);

      // Direct redirect to player dashboard without alert
      window.location.replace('player.html');
    } else {
      alert("Errore durante l'aggiunta del ruolo Player.");
    }
  } catch (error) {
    console.error('Error becoming player:', error);
    alert("Errore durante l'aggiunta del ruolo Player.");
  }
}
