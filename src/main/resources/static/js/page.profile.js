import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, isMaster, saveLoginData, handleLogout } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

// --- GUARDIA DI AUTENTICAZIONE (Eseguita subito) ---
if (!isAuthenticated()) {
    console.warn("Utente non autenticato. Reindirizzamento a landing.html");
    window.location.replace('landing.html'); // .replace() non salva nella cronologia
} else if (isPlayer() || isMaster()) {
    // Se ha già un ruolo, non deve stare qui. Manda alla dashboard prioritaria.
    console.warn("Utente con ruolo su profile.html. Reindirizzo...");
    window.location.replace(isPlayer() ? 'player.html' : 'master.html');
}
// --------------------------------------------------

document.addEventListener('DOMContentLoaded', () => {
    updateGeneralUI(); // Aggiorna la navbar

    // Listener
    document.getElementById('btn-become-player')?.addEventListener('click', () => handleBecomeRole('player'));
    document.getElementById('btn-become-master')?.addEventListener('click', () => handleBecomeRole('master'));
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });
});

// --- RISOLVE IL BUG DEL RE-LOGIN ---
async function handleBecomeRole(role) {
     const endpoint = role === 'player' ? '/api/profile/become-player' : '/api/profile/become-master';
     const profileMessage = document.getElementById('profile-message');
     if(profileMessage) {
         profileMessage.textContent = 'Aggiornamento ruolo...';
         profileMessage.className = 'info-message show';
     }

     const data = await apiCall(endpoint, 'POST');
     
     if (data && data.token) {
        // SUCCESSO! Il backend ha restituito un nuovo token
        
        // 1. Salva il NUOVO token (sovrascrive il vecchio)
        saveLoginData(data.token, data); 

        if (profileMessage) {
            profileMessage.textContent = "Ruolo assegnato! Reindirizzamento...";
            profileMessage.className = 'success-message show';
        }
        
        // 2. REINDIRIZZA alla pagina corretta
        // Usiamo window.location.replace() per ricaricare la pagina
        // e costringere la guardia della nuova pagina a rileggere i ruoli
        if (role === 'player') {
            window.location.replace('player.html');
        } else if (role === 'master') {
            window.location.replace('master.html');
        }

     } else if (profileMessage) {
          // apiCall ha fallito (es. "Sei già un Player")
          profileMessage.textContent = 'Operazione fallita. L\'errore è stato mostrato in un popup.';
          profileMessage.className = 'error-message show';
     }
}