import { isAuthenticated, getCurrentUserFromStorage, isPlayer, isMaster, isAdmin } from './auth.js';

/**
 * Gestione Modali (Classe unica per tutti)
 */
export class Modal {
    // ... (Il codice della classe Modal è invariato, copialo da prima) ...
    constructor(modalId) {
        this.modal = document.getElementById(modalId);
        if (!this.modal) return;
        this.overlay = this.modal.querySelector('.modal-overlay');
        this.closeBtn = this.modal.querySelector('.modal-close');
        this.cancelBtns = this.modal.querySelectorAll('.modal-cancel');
        this.form = this.modal.querySelector('form');
        this.errorDiv = this.modal.querySelector('.error-message');
        this._initListeners();
    }
    _initListeners() {
        if (this.overlay) this.overlay.onclick = () => this.hide();
        if (this.closeBtn) this.closeBtn.onclick = () => this.hide();
        this.cancelBtns.forEach(btn => btn.onclick = () => this.hide());
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isVisible()) this.hide();
        });
    }
    show() { if (this.modal) { this.modal.classList.add('show'); document.body.style.overflow = 'hidden'; } }
    hide() { if (this.modal) { this.modal.classList.remove('show'); document.body.style.overflow = ''; this.clearForm(); } }
    isVisible() { return this.modal ? this.modal.classList.contains('show') : false; }
    showError(message) { if(this.errorDiv) { this.errorDiv.textContent = message; this.errorDiv.classList.add('show'); } }
    hideError() { if(this.errorDiv) { this.errorDiv.textContent = ''; this.errorDiv.classList.remove('show'); } }
    clearForm() { if(this.form) this.form.reset(); this.hideError(); }
}


/**
 * Aggiorna la Navbar (Globale)
 * --- CORRETTA LA LOGICA DI SWITCH ---
 */
export function updateGeneralUI() {
     const isAuth = isAuthenticated();
     const user = getCurrentUserFromStorage();
     const nav = document.querySelector('header nav');
     if (!nav) return;

     if (isAuth && user) {
        const currentPage = window.location.pathname.split('/').pop() || 'landing.html';
        let navHTML = `<span id="welcome-user">Benvenuto, ${user.username}</span>`;

        if (isAdmin()) {
            if (currentPage !== 'admin.html') navHTML += `<a href="admin.html">Dashboard Admin</a>`;
        } else {
            // Se non sei né Player né Master, E non sei su profile.html -> vai a profile.html
            if (!isPlayer() && !isMaster() && currentPage !== 'profile.html') {
                navHTML += `<a href="profile.html">Scegli Ruolo</a>`;
            }

            // Se sei Player, mostra il link (se non sei già lì)
            if (isPlayer() && currentPage !== 'player.html') {
                navHTML += `<a href="player.html">Vista Player</a>`;
            }

            // Se sei Master, mostra il link (se non sei già lì)
            if (isMaster() && currentPage !== 'master.html') {
                navHTML += `<a href="master.html">Vista Master</a>`;
            }
        }
         navHTML += `<a href="#" id="logout-button">Logout</a>`;
         nav.innerHTML = navHTML;

     } else {
        // Navbar utente NON loggato
        nav.innerHTML = `
            <a href="landing.html">Home</a>
            <a href="#" class="login-trigger">Accedi</a>
            <a href="register.html">Registrati</a>
        `;
         // Riattacca i listener (gestiti da page.landing.js e page.register.js)
         document.querySelectorAll('.login-trigger').forEach(el => el.onclick = (e) => {
             e.preventDefault();
             if (window.loginModal) window.loginModal.show();
         });
     }
}