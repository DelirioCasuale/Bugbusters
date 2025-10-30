// static/js/page.profile.js
import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, isMaster, saveLoginData, handleLogout } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

document.addEventListener('DOMContentLoaded', () => {

    // --- GUARDIA DI AUTENTICAZIONE ---
    if (!isAuthenticated()) {
        console.warn("Utente non autenticato. Reindirizzamento a landing.html");
        window.location.replace('landing.html');
        return; // Ferma l'esecuzione
    }

    // --- RIMUOVIAMO QUESTA GUARDIA ---
    // Se l'utente ha già un ruolo, i link nella navbar (aggiornata da updateGeneralUI)
    // lo manderanno comunque alle pagine giuste. Lasciamo che questa pagina
    // carichi, così può cliccare "Diventa Master" se è solo Player.
    /*
    if (isPlayer() || isMaster()) {
        console.warn("Utente con ruolo su profile.html. Reindirizzo...");
        window.location.replace(isPlayer() ? 'player.html' : 'master.html');
        return;
    }
    */
    // ---------------------------------

    updateGeneralUI(); // Aggiorna la navbar

    // Listener
    document.getElementById('btn-become-player')?.addEventListener('click', () => handleBecomeRole('player'));
    document.getElementById('btn-become-master')?.addEventListener('click', () => handleBecomeRole('master'));
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    // Nascondi i pulsanti se il ruolo è già stato acquisito
    if (isPlayer()) {
         document.getElementById('btn-become-player').style.display = 'none';
    }
     if (isMaster()) {
         document.getElementById('btn-become-master').style.display = 'none';
    }

});

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