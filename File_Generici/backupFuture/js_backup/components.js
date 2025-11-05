// ============================================================================
// COMPONENT SYSTEM - Sistema di componenti HTML riutilizzabili
// ============================================================================

class ComponentSystem {
  constructor() {
    this.components = {};
    this.initializeComponents();
  }

  // ============================================================================
  // HEADER COMPONENT
  // ============================================================================
  createHeader() {
    return `
      <header>
        <div class="logo-flex">
          <i><img src="../static/images/dice.png" class="logo-icon" alt="Tavern Portal Logo" /></i>
          <div class="logo">Tavern Portal</div>
        </div>

        <nav>
          <a href="#" class="login">Accedi</a>
          <a href="signup.html">Registrati</a>
          <!-- lo script fa il resto per ora -->
        </nav>
      </header>
    `;
  }

  // ============================================================================
  // FOOTER COMPONENT
  // ============================================================================
  createFooter() {
    return `
      <footer class="footer">
        <p>© 2025 Tavern Portal. Tutti i diritti riservati.</p>
      </footer>
    `;
  }

  // ============================================================================
  // LOGIN MODAL COMPONENT
  // ============================================================================
  createLoginModal() {
    return `
      <div id="loginModal" class="custom-modal">
        <div class="modal-overlay" id="modalOverlay"></div>
        <div class="modal-container">
          <div class="modal-header">
            <h2 class="modal-title">Accedi</h2>
            <button type="button" class="modal-close" id="modalClose">&times;</button>
          </div>
          <div class="modal-body">
            <form id="loginForm">
              <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required placeholder="Inserisci username">
              </div>
              <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required placeholder="Inserisci password">
              </div>
              <div id="loginError" class="error-message"></div>
            </form>
          </div>
          <div class="modal-footer">
            <a href="signup.html">Non hai un account? Registrati</a>
            <div class="btn-group">
              <button type="button" class="btn btn-secondary" id="modalCancel">Chiudi</button>
              <button type="button" class="btn btn-primary" id="loginButton">Accedi</button>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  // ============================================================================
  // SCRIPTS COMPONENT (common scripts)
  // ============================================================================
  createScripts() {
    return `
      <script src="../static/js/frontend-development-script.js"></script>
      <script src="../static/js/script.js"></script>
    `;
  }

  // ============================================================================
  // COMPONENT INJECTION METHODS
  // ============================================================================
  injectHeader() {
    const headerContainer = document.querySelector('[data-component="header"]');
    if (headerContainer) {
      headerContainer.outerHTML = this.createHeader();
    }
  }

  injectFooter() {
    const footerContainer = document.querySelector('[data-component="footer"]');
    if (footerContainer) {
      footerContainer.outerHTML = this.createFooter();
    }
  }

  injectLoginModal() {
    const modalContainer = document.querySelector(
      '[data-component="login-modal"]'
    );
    if (modalContainer) {
      modalContainer.outerHTML = this.createLoginModal();
    }
  }

  injectScripts() {
    const scriptsContainer = document.querySelector(
      '[data-component="scripts"]'
    );
    if (scriptsContainer) {
      scriptsContainer.outerHTML = this.createScripts();
    }
  }

  // ============================================================================
  // AUTO INITIALIZATION
  // ============================================================================
  initializeComponents() {
    // Wait for DOM to be ready
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () =>
        this.injectAllComponents()
      );
    } else {
      this.injectAllComponents();
    }
  }

  injectAllComponents() {
    this.injectHeader();
    this.injectFooter();
    this.injectLoginModal();
    this.injectScripts();
  }
}

// ============================================================================
// AUTO-INITIALIZE COMPONENT SYSTEM
// ============================================================================
window.ComponentSystem = new ComponentSystem();

// Export for global access
window.TavernComponents = {
  header: window.ComponentSystem.createHeader(),
  footer: window.ComponentSystem.createFooter(),
  loginModal: window.ComponentSystem.createLoginModal(),
  scripts: window.ComponentSystem.createScripts(),
};
