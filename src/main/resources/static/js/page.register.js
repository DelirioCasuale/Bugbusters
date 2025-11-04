import { apiCall } from './modules/api.js';
import {
  saveLoginData,
  isAdmin,
  isPlayer,
  isMaster,
  handleLogout,
} from './modules/auth.js';
import { Modal, updateGeneralUI, initLogoNavigation } from './modules/ui.js';

document.addEventListener('DOMContentLoaded', () => {
  // 1. Inizializza UI
  updateGeneralUI();
  initLogoNavigation();

  // 2. Aggiungi Listener per login trigger usando event delegation
  document.addEventListener('click', (e) => {
    if (e.target.closest('.login-trigger')) {
      e.preventDefault();
      if (window.showLoginModal) window.showLoginModal();
    }
  });

  // 2.1. Chiudi modal quando si clicca "Registrati" nel modal (siamo già nella register page)
  document.addEventListener('click', (e) => {
    if (e.target.closest('.register-link')) {
      e.preventDefault();
      const modalClose = document.querySelector('.modal-close');
      if (modalClose) modalClose.click();
    }
  });

  // 3. Listener per il form di registrazione
  document
    .getElementById('registerForm')
    ?.addEventListener('submit', handleRegister);

  // 4. Initialize shared password toggle for registration fields
  initSharedPasswordToggle();

  document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-button') handleLogout(e);
  });
});

/**
 * Initializes shared password toggle functionality for registration form
 * When one eye icon is clicked, both password fields toggle visibility
 */
function initSharedPasswordToggle() {
  const passwordInput = document.getElementById('reg-password');
  const confirmPasswordInput = document.getElementById('reg-confirm-password');

  const eyeIconVisible1 = document.getElementById('eye-icon-visible-reg');
  const eyeIconHidden1 = document.getElementById('eye-icon-hidden-reg');
  const eyeIconVisible2 = document.getElementById('eye-icon-visible-confirm');
  const eyeIconHidden2 = document.getElementById('eye-icon-hidden-confirm');

  const toggleButton1 = document.getElementById('password-toggle-reg');
  const toggleButton2 = document.getElementById('password-toggle-confirm');

  if (
    !passwordInput ||
    !confirmPasswordInput ||
    !eyeIconVisible1 ||
    !eyeIconHidden1 ||
    !eyeIconVisible2 ||
    !eyeIconHidden2 ||
    !toggleButton1 ||
    !toggleButton2
  )
    return;

  function togglePasswordVisibility() {
    const isPassword = passwordInput.type === 'password';

    // Toggle both input types
    passwordInput.type = isPassword ? 'text' : 'password';
    confirmPasswordInput.type = isPassword ? 'text' : 'password';

    // Toggle all icons simultaneously
    if (isPassword) {
      // Show hidden icons (passwords are now visible)
      eyeIconVisible1.classList.add('hidden');
      eyeIconHidden1.classList.remove('hidden');
      eyeIconVisible2.classList.add('hidden');
      eyeIconHidden2.classList.remove('hidden');
    } else {
      // Show visible icons (passwords are now hidden)
      eyeIconVisible1.classList.remove('hidden');
      eyeIconHidden1.classList.add('hidden');
      eyeIconVisible2.classList.remove('hidden');
      eyeIconHidden2.classList.add('hidden');
    }

    // Update aria-labels for both buttons
    const ariaLabel = isPassword ? 'Nascondi password' : 'Mostra password';
    toggleButton1.setAttribute('aria-label', ariaLabel);
    toggleButton2.setAttribute('aria-label', ariaLabel);
  }

  // Add click listeners to both toggle buttons
  toggleButton1.addEventListener('click', (e) => {
    e.preventDefault();
    togglePasswordVisibility();
  });

  toggleButton2.addEventListener('click', (e) => {
    e.preventDefault();
    togglePasswordVisibility();
  });

  // Add keyboard support for both buttons
  [toggleButton1, toggleButton2].forEach((button) => {
    button.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        togglePasswordVisibility();
      }
    });
  });
}

// handleRegister
async function handleRegister(event) {
  event.preventDefault();
  const errorDiv = document.getElementById('registerError');
  const successDiv = document.getElementById('registerSuccess');
  if (errorDiv) errorDiv.style.display = 'none';
  if (successDiv) successDiv.style.display = 'none';

  const username = document.getElementById('reg-username')?.value;
  const email = document.getElementById('reg-email')?.value;
  const password = document.getElementById('reg-password')?.value;
  const confirmPassword = document.getElementById(
    'reg-confirm-password'
  )?.value;

  if (!username || !email || !password || !confirmPassword) {
    if (errorDiv) {
      errorDiv.textContent = 'Tutti i campi sono obbligatori.';
      errorDiv.style.display = 'block';
    }
    return;
  }

  if (password !== confirmPassword) {
    if (errorDiv) {
      errorDiv.textContent = 'Le password non corrispondono.';
      errorDiv.style.display = 'block';
    }
    return;
  }

  const data = await apiCall('/api/auth/register', 'POST', {
    username,
    email,
    password,
  });
  if (data && data.message) {
    if (data.message.toLowerCase().includes('successo')) {
      if (successDiv) {
        successDiv.textContent = data.message + ' Ora puoi accedere.';
        successDiv.style.display = 'block';
      }
      document.getElementById('registerForm')?.reset();
    } else {
      if (errorDiv) {
        errorDiv.textContent = data.message;
        errorDiv.style.display = 'block';
      }
    }
  } else if (!data) {
    if (errorDiv) {
      errorDiv.textContent =
        'Errore durante la registrazione. Controlla la console.';
      errorDiv.style.display = 'block';
    }
  }
}
