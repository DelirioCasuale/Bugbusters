// Spring Security Frontend Validator Mock

class SpringSecurityValidator {
  constructor() {
    this.users = [
      {
        username: 'admin',
        password: 'admin',
        role: 'ADMIN',
        authorities: ['ROLE_ADMIN'],
      },
      {
        username: 'guest1',
        password: 'password1',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        // Non ha master (non è un master)
        player: {
          characters: ['My Warrior', 'My Mage'],
        },
      },
      {
        username: 'guest2',
        password: 'password2',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        master: {
          campaigns: ['My Epic Campaign'],
        },
        // Non ha player (non è un player)
      },
      {
        username: 'guest3',
        password: 'password3',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        master: {
          campaigns: ['Adventure Quest', 'Dark Mystery'],
        },
        player: {
          characters: ['Rogue Assassin'],
        },
      },
      {
        username: 'guest4',
        password: 'password4',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        master: {}, // È un master ma non ha ancora campagne
        player: {}, // È un player ma non ha ancora personaggi
      },
      {
        username: 'guest5',
        password: 'password5',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        // Non ha né master né player (non può accedere a nessuna vista)
      },
    ];

    this.currentUser = null;
    this.init();
  }

  init() {
    // Controlla se c'è un utente salvato nel sessionStorage
    const savedUser = sessionStorage.getItem('currentUser');
    if (savedUser) {
      this.currentUser = JSON.parse(savedUser);
      this.updateUI();
    }
  }

  // Simula l'autenticazione Spring Security
  authenticate(username, password) {
    const user = this.users.find(
      (u) => u.username === username && u.password === password
    );

    if (user) {
      this.currentUser = {
        username: user.username,
        role: user.role,
        authorities: user.authorities,
        authenticated: true,
        master: user.master,
        player: user.player,
      };

      // Salva nel sessionStorage
      sessionStorage.setItem('currentUser', JSON.stringify(this.currentUser));

      return {
        success: true,
        user: this.currentUser,
      };
    }

    return {
      success: false,
      error: 'Invalid credentials',
    };
  }

  // Verifica se l'utente è autenticato
  isAuthenticated() {
    return this.currentUser && this.currentUser.authenticated;
  }

  // Verifica se l'utente ha un ruolo specifico
  hasRole(role) {
    if (!this.isAuthenticated()) return false;
    return this.currentUser.authorities.includes(`ROLE_${role}`);
  }

  // Verifica se l'utente ha un'autorità specifica
  hasAuthority(authority) {
    if (!this.isAuthenticated()) return false;
    return this.currentUser.authorities.includes(authority);
  }

  // Verifica se l'utente è un master
  isMaster() {
    if (!this.isAuthenticated()) return false;
    return (
      this.currentUser.master !== undefined && this.currentUser.master !== null
    );
  }

  // Verifica se l'utente è un giocatore
  isPlayer() {
    if (!this.isAuthenticated()) return false;
    return (
      this.currentUser.player !== undefined && this.currentUser.player !== null
    );
  }

  // Ottiene l'utente corrente
  getCurrentUser() {
    return this.currentUser;
  }

  // Logout
  logout() {
    this.currentUser = null;
    sessionStorage.removeItem('currentUser');
    this.updateUI();
  }

  // Aggiorna l'interfaccia utente in base allo stato di autenticazione
  updateUI() {
    const usernameEl = document.querySelector(
      "span[sec\\:authentication='username']"
    );
    const testAreaEl = document.getElementById('test-area');

    if (this.isAuthenticated()) {
      // Crea o aggiorna l'elemento username se non esiste
      if (!usernameEl) {
        const newUsernameEl = document.createElement('span');
        newUsernameEl.setAttribute('sec:authentication', 'username');
        newUsernameEl.textContent = this.currentUser.username;
        newUsernameEl.style.display = 'none'; // Nascosto ma presente per compatibilità
        document.body.appendChild(newUsernameEl);
      } else {
        usernameEl.textContent = this.currentUser.username;
      }

      if (testAreaEl) {
        testAreaEl.innerHTML = `
          <div class="user-info">
            <p><strong>Utente:</strong> ${this.currentUser.username}</p>
            <p><strong>Ruolo:</strong> ${this.currentUser.role}</p>
            <p><strong>Autorità:</strong> ${this.currentUser.authorities.join(
              ', '
            )}</p>
            <button id="logoutBtn" class="btn-secondary">Logout</button>
          </div>
        `;

        // Aggiungi event listener per il logout
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
          logoutBtn.addEventListener('click', () => {
            this.logout();
            location.reload(); // Ricarica la pagina dopo il logout
          });
        }
      }

      // Mostra/nasconde elementi basati sui ruoli
      this.applySecurityDisplay();
    } else {
      if (usernameEl) {
        usernameEl.textContent = '';
      }

      if (testAreaEl) {
        testAreaEl.innerHTML = '';
      }

      // Nasconde elementi che richiedono autenticazione
      this.applySecurityDisplay();
    }
  }

  // Applica la logica di visualizzazione basata sui ruoli (simula Thymeleaf sec:authorize)
  applySecurityDisplay() {
    // Elementi che richiedono autenticazione
    const authElements = document.querySelectorAll(
      '[sec\\:authorize="isAuthenticated()"]'
    );
    authElements.forEach((el) => {
      el.style.display = this.isAuthenticated() ? 'inline-block' : 'none';
    });

    // Elementi per utenti non autenticati
    const anonElements = document.querySelectorAll(
      '[sec\\:authorize="!isAuthenticated()"]'
    );
    anonElements.forEach((el) => {
      el.style.display = !this.isAuthenticated() ? 'inline-block' : 'none';
    });

    // Elementi per ruolo ADMIN
    const adminElements = document.querySelectorAll(
      '[sec\\:authorize="hasRole(\'ADMIN\')"]'
    );
    adminElements.forEach((el) => {
      el.style.display = this.hasRole('ADMIN') ? 'block' : 'none';
    });

    // Elementi per ruolo GUEST
    const guestElements = document.querySelectorAll(
      '[sec\\:authorize="hasRole(\'GUEST\')"]'
    );
    guestElements.forEach((el) => {
      el.style.display = this.hasRole('GUEST') ? 'block' : 'none';
    });

    // Elementi per utenti che SONO player
    const playerElements = document.querySelectorAll(
      '[sec\\:authorize="isPlayer()"]'
    );
    playerElements.forEach((el) => {
      el.style.display =
        this.isAuthenticated() && this.isPlayer() ? 'block' : 'none';
    });

    // Elementi per utenti che SONO master
    const masterElements = document.querySelectorAll(
      '[sec\\:authorize="isMaster()"]'
    );
    masterElements.forEach((el) => {
      el.style.display =
        this.isAuthenticated() && this.isMaster() ? 'block' : 'none';
    });
  }

  // Metodo per il login form
  login(username, password, errorCallback) {
    const result = this.authenticate(username, password);

    if (result.success) {
      this.updateUI();
      return true;
    } else {
      if (errorCallback) {
        errorCallback(result.error);
      }
      return false;
    }
  }
}

// Istanza globale del validator
const securityValidator = new SpringSecurityValidator();

// Inizializzazione quando il DOM è caricato
document.addEventListener('DOMContentLoaded', () => {
  securityValidator.updateUI();

  // Esponi metodi globali per compatibilità
  window.isAuthenticated = () => securityValidator.isAuthenticated();
  window.hasRole = (role) => securityValidator.hasRole(role);
  window.hasAuthority = (authority) =>
    securityValidator.hasAuthority(authority);
  window.isMaster = () => securityValidator.isMaster();
  window.isPlayer = () => securityValidator.isPlayer();
  window.getCurrentUser = () => securityValidator.getCurrentUser();
  window.logout = () => securityValidator.logout();
  window.securityLogin = (username, password, errorCallback) =>
    securityValidator.login(username, password, errorCallback);
});
