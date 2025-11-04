// components.js - Reusable component loader
import { apiCall } from './api.js';
import { saveLoginData, isAuthenticated } from './auth.js';
import { updateGeneralUI, initLogoNavigation } from './ui.js';

export class ComponentLoader {
  static async loadFragment(elementSelector, fragmentPath) {
    try {
      const response = await fetch(fragmentPath);
      if (!response.ok) {
        throw new Error(`Failed to load fragment: ${response.status}`);
      }
      const html = await response.text();
      const element = document.querySelector(elementSelector);
      if (element) {
        element.innerHTML = html;
        return true;
      }
      return false;
    } catch (error) {
      console.error(`Error loading fragment ${fragmentPath}:`, error);
      return false;
    }
  }

  static async loadHeader() {
    const success = await this.loadFragment(
      '#header-placeholder',
      'fragments/header.html'
    );
    if (success) {
      // Update navigation based on authentication status
      updateGeneralUI();
      // Re-initialize header functionality
      this.initializeHeaderEvents();
      // Initialize logo navigation
      initLogoNavigation();
    }
    return success;
  }

  static async loadFooter() {
    return await this.loadFragment(
      '#footer-placeholder',
      'fragments/footer.html'
    );
  }

  static async loadLoginModal() {
    const success = await this.loadFragment(
      '#modal-placeholder',
      'fragments/login-modal.html'
    );
    if (success) {
      // Re-initialize modal functionality after loading
      this.initializeModalEvents();
      // Re-initialize password toggle functionality
      if (window.initSharedPasswordToggle) {
        window.initSharedPasswordToggle();
      }
    }
    return success;
  }

  static initializeModalEvents() {
    // Handle modal close events
    const modalClose = document.querySelector('.modal-close');
    const modalCancel = document.querySelector('.modal-cancel');
    const modalOverlay = document.querySelector('.modal-overlay');
    const loginModal = document.getElementById('loginModal');

    const closeModal = () => {
      if (loginModal) {
        loginModal.classList.remove('show');
      }
    };

    if (modalClose) modalClose.addEventListener('click', closeModal);
    if (modalCancel) modalCancel.addEventListener('click', closeModal);
    if (modalOverlay) modalOverlay.addEventListener('click', closeModal);

    // Handle form submission
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
      loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await this.handleLogin(e);
      });
    }
  }

  static initializeHeaderEvents() {
    // Handle login trigger (for non-authenticated users)
    const loginTrigger = document.querySelector('.login-trigger');
    if (loginTrigger) {
      loginTrigger.addEventListener('click', (e) => {
        e.preventDefault();
        window.showLoginModal();
      });
    }

    // Handle logout button (for authenticated users)
    const logoutButton = document.getElementById('logout-button');
    if (logoutButton) {
      logoutButton.addEventListener('click', (e) => {
        e.preventDefault();
        // Import and use the handleLogout function
        import('./auth.js').then(({ handleLogout }) => {
          handleLogout(e);
        });
      });
    }

    // Handle login buttons from hero section (landing page specific)
    const loginBtns = document.querySelectorAll('.login-btn');
    loginBtns.forEach((btn) => {
      btn.addEventListener('click', function (e) {
        e.preventDefault();
        window.showLoginModal();
      });
    });
  }

  static async loadAllComponents() {
    const promises = [
      this.loadHeader(),
      this.loadFooter(),
      this.loadLoginModal(),
    ];

    const results = await Promise.all(promises);
    console.log('Components loaded:', {
      header: results[0],
      footer: results[1],
      modal: results[2],
    });

    return results.every((result) => result);
  }

  static async handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('login-username')?.value;
    const password = document.getElementById('login-password')?.value;
    const errorElement = document.getElementById('loginError');

    // Clear previous errors
    if (errorElement) {
      errorElement.textContent = '';
      errorElement.style.display = 'none';
    }

    if (!username || !password) {
      this.showLoginError('Inserisci username e password');
      return;
    }

    try {
      // Use fetch directly for login to handle errors without alerts
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username,
          password,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        if (data && data.token) {
          saveLoginData(data.token, data);

          // Close modal
          const loginModal = document.getElementById('loginModal');
          if (loginModal) {
            loginModal.classList.remove('show');
          }

          // Update UI to reflect authentication status
          updateGeneralUI();

          // Always redirect to landing page after successful login
          console.log('Login successful! Redirecting to landing page...');
          window.location.replace('landing.html');
        } else {
          this.showLoginError('Combinazione username e password non valida');
        }
      } else {
        // Handle HTTP error responses (401, 403, etc.) - always show our custom message
        this.showLoginError('Combinazione username e password non valida');
      }
    } catch (error) {
      console.error('Login error:', error);
      this.showLoginError('Errore durante il login. Riprova.');
    }
  }

  static showLoginError(message) {
    const errorElement = document.getElementById('loginError');
    const passwordField = document.getElementById('login-password');

    if (errorElement) {
      errorElement.textContent = message;
      errorElement.style.display = 'block';
    }

    // Clear password field on error
    if (passwordField) {
      passwordField.value = '';
    }
  }
}

// Global function to show login modal
window.showLoginModal = function () {
  const loginModal = document.getElementById('loginModal');
  if (loginModal) {
    loginModal.classList.add('show');
  }
};

// Auto-load components when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  ComponentLoader.loadAllComponents();
});
