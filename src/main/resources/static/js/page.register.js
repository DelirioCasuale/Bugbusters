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
    document.getElementById('registerForm')?.addEventListener('submit', handleRegister);
    
    // Listener globale per logout (se l'utente è loggato)
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });
});

// handleLogin è duplicato qui perché il modal è su questa pagina
async function handleLogin(event) {
    event.preventDefault();
    // ... (Codice identico a page.landing.js) ...
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
        saveLoginData(data.token, data);
        loginModal.hide();
        if (isAdmin()) window.location.href = 'admin.html';
        else if (isPlayer()) window.location.href = 'player.html';
        else if (isMaster()) window.location.href = 'master.html';
        else window.location.href = 'profile.html';
    } else if (!data) {
        loginModal.showError('Errore durante il login. Controlla la console.');
    } else {
        loginModal.showError(data.message || 'Credenziali non valide.');
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const errorDiv = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');
    if (errorDiv) errorDiv.style.display = 'none';
    if (successDiv) successDiv.style.display = 'none';

    const username = document.getElementById('reg-username')?.value;
    const email = document.getElementById('reg-email')?.value;
    const password = document.getElementById('reg-password')?.value;

    if (!username || !email || !password) {
         if (errorDiv) { errorDiv.textContent = 'Tutti i campi sono obbligatori.'; errorDiv.style.display = 'block'; }
         return;
    }
    
    const data = await apiCall('/api/auth/register', 'POST', { username, email, password });
    if (data && data.message) {
        if (data.message.toLowerCase().includes('successo')) {
             if (successDiv) {
                 successDiv.textContent = data.message + " Ora puoi accedere.";
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
         if (errorDiv) { errorDiv.textContent = 'Errore durante la registrazione. Controlla la console.'; errorDiv.style.display = 'block'; }
    }
}