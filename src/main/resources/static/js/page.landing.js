import { apiCall } from './modules/api.js';
import { saveLoginData, isAdmin, isPlayer, isMaster, handleLogout } from './modules/auth.js';
import { Modal, updateGeneralUI } from './modules/ui.js';

let loginModal;

document.addEventListener('DOMContentLoaded', () => {
    // Inizializza UI
    updateGeneralUI();
    loginModal = new Modal('loginModal');

    // Listener
    document.querySelectorAll('.login-trigger').forEach(el => el.onclick = (e) => {
        e.preventDefault();
        loginModal?.show();
    });
    document.getElementById('loginForm')?.addEventListener('submit', handleLogin);
    
    // Listener globale per logout (necessario se l'utente è loggato su questa pagina)
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });
});

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
        
        // Logica di Reindirizzamento
        if (isAdmin()) {
            window.location.href = 'admin.html';
        } else if (isPlayer()) { // Priorità a Player
            window.location.href = 'player.html';
        } else if (isMaster()) { // Se è *solo* master
            window.location.href = 'master.html';
        } else { // Se non è nessuno dei tre (solo ROLE_USER)
            window.location.href = 'profile.html';
        }
    } else if (!data) {
        loginModal.showError('Errore durante il login. Controlla la console.');
    } else {
        loginModal.showError(data.message || 'Credenziali non valide.');
    }
}