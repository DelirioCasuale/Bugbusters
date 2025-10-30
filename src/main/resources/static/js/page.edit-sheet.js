import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, handleLogout } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

let currentSheetId = null;

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
    if (!isAuthenticated()) {
        console.warn("Utente non autenticato. Reindirizzamento a landing.html");
        window.location.replace('landing.html');
        return;
    }
    if (!isPlayer()) {
        console.warn("Utente non player su pagina modifica. Reindirizzamento...");
        window.location.replace('profile.html');
        return;
    }
    // ---------------------------------

    updateGeneralUI(); // Aggiorna navbar

    // Listener Logout
    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    // Listener Form
    document.getElementById('editSheetForm')?.addEventListener('submit', handleUpdateSheet);

    // 1. Ottieni l'ID della scheda dall'URL
    const params = new URLSearchParams(window.location.search);
    currentSheetId = params.get('id');

    if (currentSheetId) {
        // 2. Carica i dati della scheda
        loadSheetData(currentSheetId);
    } else {
        alert("Errore: ID scheda non trovato nell'URL.");
        window.location.href = 'player.html';
    }
});

/**
 * Carica i dati della scheda e popola il form
 */
async function loadSheetData(sheetId) {
    const data = await apiCall(`/api/player/sheets/${sheetId}`);
    if (data) {
        populateForm(data);
    } else {
        // Errore (es. 403 non autorizzato, 404 non trovato)
        // L'alert di apiCall ha già avvisato, reindirizziamo
        window.location.href = 'player.html';
    }
}

/**
 * Popola tutti i campi del form con i dati dal DTO
 */
function populateForm(dto) {
    // Titolo pagina
    document.getElementById('sheet-name-title').textContent = `Modifica: ${dto.name}`;
    
    // Assegna valore a ogni input. Usiamo nomi corrispondenti
    // Questo è un elenco parziale (KISS). Aggiungi gli altri campi se necessario.
    
    // Info Base
    document.getElementById('name').value = dto.name || '';
    document.getElementById('primaryClass').value = `${dto.primaryClass || ''} ${dto.primaryLevel || ''}`; // Combiniamo
    document.getElementById('background').value = dto.background || '';
    document.getElementById('race').value = dto.race || '';
    document.getElementById('alignment').value = dto.alignment || '';
    document.getElementById('experiencePoints').value = dto.experiencePoints || 0;

    // Statistiche
    document.getElementById('strength').value = dto.strength || 10;
    document.getElementById('dexterity').value = dto.dexterity || 10;
    document.getElementById('constitution').value = dto.constitution || 10;
    document.getElementById('intelligence').value = dto.intelligence || 10;
    document.getElementById('wisdom').value = dto.wisdom || 10;
    document.getElementById('charisma').value = dto.charisma || 10;

    // Combat
    document.getElementById('armorClass').value = dto.armorClass || 10;
    document.getElementById('initiative').value = dto.initiative || 0;
    document.getElementById('speed').value = dto.speed || 30;
    document.getElementById('currentHitPoints').value = dto.currentHitPoints || 10;
    document.getElementById('maxHitPoints').value = dto.maxHitPoints || 10;
    
    // Testo
    document.getElementById('personalityTraits').value = dto.personalityTraits || '';
    document.getElementById('ideals').value = dto.ideals || '';
    document.getElementById('bonds').value = dto.bonds || '';
    document.getElementById('flaws').value = dto.flaws || '';
    document.getElementById('equipment').value = dto.equipment || '';
    document.getElementById('featuresAndTraits').value = dto.featuresAndTraits || '';

    // TODO: Mappare le checkbox delle competenze (più complesso)
    // Per ora, omettiamo la mappatura delle 18 checkbox di competenza per semplicità
}

/**
 * Legge i dati dal form e li invia al backend
 */
async function handleUpdateSheet(event) {
    event.preventDefault();
    if (!currentSheetId) return;

    const errorDiv = document.getElementById('editSheetError');
    const successDiv = document.getElementById('editSheetSuccess');
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';
    
    // Costruisci il DTO leggendo dal form
    // N.B.: Dobbiamo separare di nuovo Classe e Livello
    const classAndLevel = document.getElementById('primaryClass').value.split(' ');
    
    const dto = {
        id: currentSheetId,
        name: document.getElementById('name').value,
        primaryClass: classAndLevel[0] || 'Sconosciuto', // Prende la prima parola
        primaryLevel: parseInt(classAndLevel[1] || '1', 10), // Prende il numero
        background: document.getElementById('background').value,
        race: document.getElementById('race').value,
        alignment: document.getElementById('alignment').value,
        experiencePoints: parseInt(document.getElementById('experiencePoints').value, 10),
        
        strength: parseInt(document.getElementById('strength').value, 10),
        dexterity: parseInt(document.getElementById('dexterity').value, 10),
        constitution: parseInt(document.getElementById('constitution').value, 10),
        intelligence: parseInt(document.getElementById('intelligence').value, 10),
        wisdom: parseInt(document.getElementById('wisdom').value, 10),
        charisma: parseInt(document.getElementById('charisma').value, 10),
        
        armorClass: parseInt(document.getElementById('armorClass').value, 10),
        initiative: parseInt(document.getElementById('initiative').value, 10),
        speed: parseInt(document.getElementById('speed').value, 10),
        currentHitPoints: parseInt(document.getElementById('currentHitPoints').value, 10),
        maxHitPoints: parseInt(document.getElementById('maxHitPoints').value, 10),
        
        personalityTraits: document.getElementById('personalityTraits').value,
        ideals: document.getElementById('ideals').value,
        bonds: document.getElementById('bonds').value,
        flaws: document.getElementById('flaws').value,
        equipment: document.getElementById('equipment').value,
        featuresAndTraits: document.getElementById('featuresAndTraits').value,
        
        // TODO: Mappare le checkbox (omesse per ora)
        // ... (tutti i campi booleani delle 18 skill) ...
    };

    const data = await apiCall(`/api/player/sheets/${currentSheetId}`, 'PUT', dto);
    
    if (data) {
        successDiv.textContent = "Scheda salvata con successo!";
        successDiv.style.display = 'block';
        // Aggiorna il titolo della pagina
        document.getElementById('sheet-name-title').textContent = `Modifica: ${data.name}`;
    } else {
        errorDiv.textContent = "Errore nel salvataggio.";
        errorDiv.style.display = 'block';
    }
}