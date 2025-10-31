import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, isMaster, saveLoginData, handleLogout, getCurrentUserFromStorage } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

document.addEventListener('DOMContentLoaded', () => {
    
    // --- GUARDIA ---
    if (!isAuthenticated()) {
        window.location.replace('landing.html');
        return; 
    }

    updateGeneralUI(); // Aggiorna la navbar

    // Popola i form con i dati utente attuali
    const user = getCurrentUserFromStorage();
    if (user) {
        document.getElementById('profile-username').value = user.username;
        document.getElementById('profile-email').value = user.email;
        document.getElementById('profile-image-url').value = user.profileImageUrl || '';
    }

    // --- GESTIONE DINAMICA BOTTONI RUOLO (MODIFICATA) ---
    const btnPlayer = document.getElementById('btn-become-player');
    const btnMaster = document.getElementById('btn-become-master');

    // --- NUOVA LOGICA: Se hai entrambi i ruoli, nascondi tutto ---
    if (isPlayer() && isMaster()) {
        const subtitle = document.getElementById('dashboard-subtitle');
        if (subtitle) subtitle.style.display = 'none';

        const profileActions = document.getElementById('profile-actions');
        if (profileActions) profileActions.style.display = 'none';
        
    } else {
        // --- VECCHIA LOGICA (semplificata) ---
        // (Se non hai entrambi i ruoli, mostra i bottoni normalmente)

        if (isPlayer()) {
            btnPlayer.textContent = "Già Player (Vai alla Dashboard)";
            // (Rimosse le righe classList.remove/add)
            btnPlayer.addEventListener('click', () => { // Cambia azione
                window.location.href = 'player.html';
            });
        } else {
            // Aggiunge listener solo se non è già player
            btnPlayer.addEventListener('click', () => handleBecomeRole('player'));
        }

        if (isMaster()) {
            btnMaster.textContent = "Già Master (Vai alla Dashboard)";
            // (Rimosse le righe classList.remove/add)
            btnMaster.addEventListener('click', () => { // Cambia azione
                window.location.href = 'master.html';
            });
        } else {
            // Aggiunge listener solo se non è già master
            btnMaster.addEventListener('click', () => handleBecomeRole('master'));
        }
    }
    // --- FINE MODIFICA ---
    
    // Listener Form Modifica Profilo
    document.getElementById('profileEditForm')?.addEventListener('submit', handleProfileUpdate);
    
    // Listener Form Modifica Password
    document.getElementById('passwordChangeForm')?.addEventListener('submit', handlePasswordChange);

    // Listener Logout
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });
});

// Handler "Diventa Ruolo" (invariato)
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
        window.location.replace(targetPage); // Ricarica la pagina
     } else if (profileMessage) {
          profileMessage.textContent = 'Operazione fallita. L\'errore è stato mostrato in un popup.';
          profileMessage.className = 'error-message show';
     }
}

// Handler Modifica Profilo (invariato)
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
        saveLoginData(data.token, data); 
        updateGeneralUI(); // Aggiorna la navbar con i nuovi dati
        successDiv.textContent = "Profilo aggiornato con successo!";
        successDiv.style.display = 'block';
    } else {
        errorDiv.textContent = data?.message || "Errore sconosciuto";
        errorDiv.style.display = 'block';
    }
}

// Handler Modifica Password (invariato)
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
        event.target.reset(); 
    } else {
        errorDiv.textContent = data?.message || "Errore sconosciuto";
        errorDiv.style.display = 'block';
    }
}