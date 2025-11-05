import {
  isAuthenticated,
  getCurrentUserFromStorage,
  hasRole,
  clearLoginData,
} from '../../../../src/main/resources/static/js/modules/auth.js';

/**
 * Protezione delle pagine lato client
 * Questo modulo controlla se l'utente ha i permessi per accedere alla pagina corrente
 */

// Mapping delle pagine e ruoli richiesti
const pagePermissions = {
  'profile.html': null, // Qualsiasi utente autenticato
  'master.html': 'MASTER',
  'player.html': 'PLAYER',
  'admin.html': 'ADMIN',
};

// Pagine pubbliche che non richiedono autenticazione
const publicPages = ['landing.html', 'register.html', 'index.html'];

/**
 * Controlla se l'utente può accedere alla pagina corrente
 */
export function checkPageAccess() {
  const currentPage = getCurrentPageName();

  // Se è una pagina pubblica, permettere sempre l'accesso
  if (isPublicPage(currentPage)) {
    return true;
  }

  // Se la pagina richiede autenticazione
  if (currentPage in pagePermissions) {
    // Controlla se l'utente è autenticato
    if (!isAuthenticated()) {
      console.warn(`Accesso negato a ${currentPage}: utente non autenticato`);
      redirectToLogin();
      return false;
    }

    // Controlla se la pagina richiede un ruolo specifico
    const requiredRole = pagePermissions[currentPage];
    if (requiredRole && !hasRole(requiredRole)) {
      console.warn(
        `Accesso negato a ${currentPage}: ruolo ${requiredRole} richiesto`
      );
      showAccessDenied();
      return false;
    }

    console.log(`Accesso consentito a ${currentPage}`);
    return true;
  }

  // Pagina non definita, permettere l'accesso
  return true;
}

/**
 * Ottiene il nome della pagina corrente
 */
function getCurrentPageName() {
  const path = window.location.pathname;
  const filename = path.split('/').pop();
  return filename || 'index.html';
}

/**
 * Controlla se la pagina è pubblica
 */
function isPublicPage(pageName) {
  return publicPages.includes(pageName);
}

/**
 * Reindirizza alla pagina di login
 */
function redirectToLogin() {
  // Salva la pagina di destinazione per il redirect post-login
  sessionStorage.setItem('intendedPage', window.location.pathname);
  window.location.href = '/landing.html';
}

/**
 * Mostra messaggio di accesso negato
 */
function showAccessDenied() {
  // Redirect to 403 error page instead of showing alert
  window.location.href = '/error403.html';
}

/**
 * Reindirizza basato sui ruoli dell'utente
 */
function redirectBasedOnRole() {
  if (!isAuthenticated()) {
    window.location.href = '/landing.html';
    return;
  }

  const user = getCurrentUserFromStorage();
  if (hasRole('ADMIN')) {
    window.location.href = '/admin.html';
  } else if (hasRole('MASTER')) {
    window.location.href = '/master.html';
  } else if (hasRole('PLAYER')) {
    window.location.href = '/player.html';
  } else {
    window.location.href = '/profile.html';
  }
}

/**
 * Gestisce il redirect post-login
 */
export function handlePostLoginRedirect() {
  const intendedPage = sessionStorage.getItem('intendedPage');
  if (intendedPage) {
    sessionStorage.removeItem('intendedPage');
    window.location.href = intendedPage;
  } else {
    // Redirect di default basato sui ruoli
    redirectBasedOnRole();
  }
}

/**
 * Inizializza la protezione della pagina
 * Chiama questa funzione su ogni pagina protetta
 */
export function initPageProtection() {
  // Controlla l'accesso quando la pagina si carica
  document.addEventListener('DOMContentLoaded', () => {
    if (!checkPageAccess()) {
      return; // Il redirect è già stato gestito
    }

    // Se l'utente è autenticato, aggiorna l'UI di conseguenza
    if (isAuthenticated()) {
      updateAuthenticatedUI();
    }
  });
}

/**
 * Aggiorna l'UI per utenti autenticati
 */
function updateAuthenticatedUI() {
  // Nasconde i pulsanti di login/registrazione se presenti
  const loginBtns = document.querySelectorAll('.btn-login, .login-btn');
  const registerBtns = document.querySelectorAll(
    '.btn-register, .register-btn'
  );

  loginBtns.forEach((btn) => (btn.style.display = 'none'));
  registerBtns.forEach((btn) => (btn.style.display = 'none'));

  // Mostra informazioni utente se presente un container
  const userInfoContainer = document.querySelector('.user-info');
  if (userInfoContainer) {
    const user = getCurrentUserFromStorage();
    userInfoContainer.innerHTML = `
            <p>Benvenuto, <strong>${user.username}</strong>!</p>
            <p>Ruoli: ${user.roles.join(', ')}</p>
        `;
  }
}
