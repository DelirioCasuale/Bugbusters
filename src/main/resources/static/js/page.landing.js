import { apiCall } from './modules/api.js';
import { saveLoginData, isAdmin, isPlayer, isMaster, handleLogout } from './modules/auth.js';
import { Modal, updateGeneralUI } from './modules/ui.js';

let loginModal;

document.addEventListener('DOMContentLoaded', () => {
    // 1. Inizializza UI (Navbar e Modal)
    updateGeneralUI();
    loginModal = new Modal('loginModal');

    // 2. Aggiungi Listener (Form, bottoni, ecc.)
    document.querySelectorAll('.login-trigger').forEach(el => el.onclick = (e) => {
        e.preventDefault();
        loginModal?.show();
    });
    document.getElementById('loginForm')?.addEventListener('submit', handleLogin);
    document.getElementById('contactForm')?.addEventListener('submit', (e) => {
        e.preventDefault();
        alert('Supporto (WIP): Messaggio inviato! (non ancora implementato)');
        document.getElementById('contactForm').reset();
    });
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    // --- NUOVA AGGIUNTA: Intersection Observer per animazioni on-scroll ---
    
    // 1. Opzioni per l'observer
    const observerOptions = {
        root: null, // usa il viewport come area di intersezione
        threshold: 0.1 // attiva l'animazione quando il 10% dell'elemento è visibile
    };

    // 2. La funzione da eseguire quando un elemento entra nel viewport
    const observerCallback = (entries, observer) => {
        entries.forEach(entry => {
            // Se l'elemento è visibile...
            if (entry.isIntersecting) {
                // ...aggiungi la classe che attiva la transizione CSS
                entry.target.classList.add('is-visible');
                // ...e smetti di osservarlo per non ripetere l'animazione
                observer.unobserve(entry.target);
            }
        });
    };

    // 3. Crea l'observer
    const scrollObserver = new IntersectionObserver(observerCallback, observerOptions);

    // 4. Trova tutti gli elementi da animare (.tile e #contact) e avvia l'osservazione
    const elementsToObserve = document.querySelectorAll('.tile, #contact');
    elementsToObserve.forEach(el => scrollObserver.observe(el));
    
    // --- FINE NUOVA AGGIUNTA ---
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