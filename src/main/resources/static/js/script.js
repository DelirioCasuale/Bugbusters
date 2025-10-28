// Variabili globali per il modal di login
let loginModal;

document.addEventListener('DOMContentLoaded', function () {
  // Inizializza il modal di login personalizzato
  loginModal = new CustomModal('loginModal');
});

// Classe per gestire il modal personalizzato
class CustomModal {
  constructor(modalId) {
    this.modal = document.getElementById(modalId);
    this.overlay = this.modal?.querySelector('.modal-overlay');
    this.closeBtn = this.modal?.querySelector('.modal-close');
    this.cancelBtn = this.modal?.querySelector('#modalCancel');

    if (this.modal) {
      this.init();
    }
  }

  init() {
    // Event listeners per chiudere il modal
    if (this.overlay) {
      this.overlay.addEventListener('click', () => this.hide());
    }

    if (this.closeBtn) {
      this.closeBtn.addEventListener('click', () => this.hide());
    }

    if (this.cancelBtn) {
      this.cancelBtn.addEventListener('click', () => this.hide());
    }

    // Chiudi con ESC
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && this.isVisible()) {
        this.hide();
      }
    });
  }

  show() {
    if (this.modal) {
      this.modal.classList.add('show');
      document.body.style.overflow = 'hidden'; // Previene lo scroll della pagina

      // Focus sul primo input
      const firstInput = this.modal.querySelector('input');
      if (firstInput) {
        setTimeout(() => firstInput.focus(), 100);
      }
    }
  }

  hide() {
    if (this.modal) {
      this.modal.classList.remove('show');
      document.body.style.overflow = ''; // Ripristina lo scroll

      // Pulisci il form
      this.clearForm();
    }
  }

  isVisible() {
    return this.modal?.classList.contains('show') || false;
  }

  clearForm() {
    const form = this.modal?.querySelector('form');
    if (form) {
      form.reset();
      this.hideError();
    }
  }

  hideError() {
    const errorDiv = this.modal?.querySelector('#loginError');
    if (errorDiv) {
      errorDiv.classList.remove('show');
      errorDiv.textContent = '';
    }
  }
}

// Event listener per il link di login
document.addEventListener('click', function (event) {
  if (event.target.classList.contains('login')) {
    event.preventDefault(); // blocca il link
    if (loginModal) {
      loginModal.show();
    }
  }
});

// Event listener per il bottone di login
document.addEventListener('click', function (event) {
  if (event.target.id === 'loginButton') {
    event.preventDefault();

    const username = document.getElementById('username')?.value;
    const password = document.getElementById('password')?.value;
    const errorDiv = document.getElementById('loginError');

    // Pulisci eventuali errori precedenti
    if (errorDiv) {
      errorDiv.classList.remove('show');
      errorDiv.textContent = '';
    }

    // Valida i campi
    if (!username || !password) {
      showLoginError('Inserisci username e password');
      return;
    }

    // Effettua il login usando il security validator
    if (typeof securityLogin !== 'undefined') {
      const success = securityLogin(username, password, function (error) {
        showLoginError('Credenziali non valide');
      });

      if (success) {
        // Login riuscito - chiudi il modal e ricarica la pagina
        if (loginModal) {
          loginModal.hide();
        }
        location.reload();
      }
    } else {
      showLoginError('Sistema di autenticazione non disponibile');
    }
  }
});

// Funzione per mostrare errori di login
function showLoginError(message) {
  const errorDiv = document.getElementById('loginError');
  if (errorDiv) {
    errorDiv.textContent = message;
    errorDiv.classList.add('show');
  }
}

// Event listener per il logout (se presente nella navbar)
document.addEventListener('click', function (event) {
  if (event.target.id === 'logoutLink') {
    event.preventDefault();
    if (typeof logout !== 'undefined') {
      logout();
      location.reload();
    }
  }
});

// Funzione per aggiornare la navbar in base allo stato di autenticazione
function updateNavbar() {
  if (typeof isAuthenticated !== 'undefined' && isAuthenticated()) {
    const user = getCurrentUser();
    const nav = document.querySelector('nav');

    if (nav && user) {
      nav.innerHTML = `
        <span style="color: white; margin-right: 15px;">Benvenuto, ${user.username}</span>
        <a href="#" id="logoutLink">Logout</a>
      `;
    }
  }
}

// Aggiorna la navbar quando la pagina viene caricata
document.addEventListener('DOMContentLoaded', function () {
  // Aspetta che il security validator sia inizializzato
  setTimeout(updateNavbar, 100);
});
