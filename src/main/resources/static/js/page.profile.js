import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, isMaster, saveLoginData, handleLogout, getCurrentUserFromStorage } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

document.addEventListener('DOMContentLoaded', () => {
    
    // --- GUARDIA DI AUTENTICAZIONE ---
    if (!isAuthenticated()) {
        window.location.replace('landing.html');
        return; 
    }
    // (Rimuoviamo la guardia che reindirizza SE sei player/master,
    // perché ora questa è la pagina di modifica profilo per TUTTI)
    // ---------------------------------

    updateGeneralUI(); // Aggiorna la navbar

    // Popola i form con i dati utente attuali
    const user = getCurrentUserFromStorage();
    if (user) {
        document.getElementById('profile-username').value = user.username;
        document.getElementById('profile-email').value = user.email;
        document.getElementById('profile-image-url').value = user.profileImageUrl || '';
    }

    // Listener "Diventa Ruolo"
    document.getElementById('btn-become-player')?.addEventListener('click', () => handleBecomeRole('player'));
    document.getElementById('btn-become-master')?.addEventListener('click', () => handleBecomeRole('master'));
    
    // Listener Form Modifica Profilo
    document.getElementById('profileEditForm')?.addEventListener('submit', handleProfileUpdate);
    
    // Listener Form Modifica Password
    document.getElementById('passwordChangeForm')?.addEventListener('submit', handlePasswordChange);

    // Listener Logout
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    // Nascondi i pulsanti "Diventa" se il ruolo è già stato acquisito
    if (isPlayer()) {
         document.getElementById('btn-become-player').style.display = 'none';
    }
     if (isMaster()) {
         document.getElementById('btn-become-master').style.display = 'none';
    }
});

// Handler "Diventa Ruolo" (invariato, restituisce token e reindirizza)
async function handleBecomeRole(role) {
     const endpoint = role === 'player' ? '/api/profile/become-player' : '/api/profile/become-master';
     const profileMessage = document.getElementById('profile-message');
     if(profileMessage) {
         profileMessage.textContent = 'Aggiornamento ruolo...';
         profileMessage.className = 'info-message show';
     }
     const data = await apiCall(endpoint, 'POST');
     if (data && data.token) {
        saveLoginData(data.token, data); 
        if (profileMessage) {
            profileMessage.textContent = "Ruolo assegnato! Reindirizzamento...";
            profileMessage.className = 'success-message show';
        }
        const targetPage = role === 'player' ? 'player.html' : 'master.html';
        window.location.replace(targetPage);
     } else if (profileMessage) {
          profileMessage.textContent = 'Operazione fallita. L\'errore è stato mostrato in un popup.';
          profileMessage.className = 'error-message show';
     }
}

// --- NUOVO HANDLER: Modifica Profilo ---
async function handleProfileUpdate(event) {
    event.preventDefault();
    const errorDiv = document.getElementById('profileEditError');
    const successDiv = document.getElementById('profileEditSuccess');
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    const newUsername = document.getElementById('profile-username').value;
    const newEmail = document.getElementById('profile-email').value;
    const newImageUrl = document.getElementById('profile-image-url').value;
    
    const data = await apiCall('/api/user/profile', 'PUT', { newUsername, newEmail, newImageUrl });

    if (data && data.token) {
        // SUCCESSO! Il backend ha restituito un nuovo token
        saveLoginData(data.token, data); // Salva i nuovi dati (username, email, img)
        updateGeneralUI(); // Aggiorna la navbar con i nuovi dati
        successDiv.textContent = "Profilo aggiornato con successo!";
        successDiv.style.display = 'block';
    } else {
        // apiCall gestirà l'alert, ma mostriamo anche l'errore nel div
        errorDiv.textContent = data?.message || "Errore sconosciuto";
        errorDiv.style.display = 'block';
    }
}

// --- NUOVO HANDLER: Modifica Password ---
async function handlePasswordChange(event) {
    event.preventDefault();
    const errorDiv = document.getElementById('passwordChangeError');
    const successDiv = document.getElementById('passwordChangeSuccess');
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    const oldPassword = document.getElementById('profile-old-password').value;
    const newPassword = document.getElementById('profile-new-password').value;

    const data = await apiCall('/api/user/password', 'PUT', { oldPassword, newPassword });

    if (data && data.message.includes('successo')) {
        successDiv.textContent = data.message;
        successDiv.style.display = 'block';
        event.target.reset(); // Svuota il form della password
    } else {
        errorDiv.textContent = data?.message || "Errore sconosciuto";
        errorDiv.style.display = 'block';
    }
}