import { apiCall } from './modules/api.js';
import { isAuthenticated, isPlayer, handleLogout } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

let currentSheetId = null;

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
    if (!isAuthenticated()) {
        window.location.replace('landing.html');
        return;
    }
    if (!isPlayer()) {
        window.location.replace('profile.html');
        return;
    }
    
    updateGeneralUI(); // Aggiorna navbar

    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });
    
    document.getElementById('editSheetForm')?.addEventListener('submit', handleUpdateSheet);
    
    // NUOVO LISTENER PER DOWNLOAD PDF
    document.getElementById('btn-download-pdf')?.addEventListener('click', handleDownloadPdf);

    const params = new URLSearchParams(window.location.search);
    currentSheetId = params.get('id');

    if (currentSheetId) {
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
        window.location.href = 'player.html';
    }
}

/**
 * Popola tutti i campi del form con i dati dal DTO
 * (Mappa ai nuovi ID "cs-...")
 */
function populateForm(dto) {
    // Header
    document.getElementById('cs-name').value = dto.name || '';
    document.getElementById('cs-class-level').value = `${dto.primaryClass || ''} ${dto.primaryLevel || ''}`;
    document.getElementById('cs-background').value = dto.background || '';
    document.getElementById('cs-race').value = dto.race || '';
    document.getElementById('cs-alignment').value = dto.alignment || '';
    document.getElementById('cs-xp').value = dto.experiencePoints || 0;

    // Col 1 - Stats
    document.getElementById('cs-strength').value = dto.strength || 10;
    document.getElementById('cs-dexterity').value = dto.dexterity || 10;
    document.getElementById('cs-constitution').value = dto.constitution || 10;
    document.getElementById('cs-intelligence').value = dto.intelligence || 10;
    document.getElementById('cs-wisdom').value = dto.wisdom || 10;
    document.getElementById('cs-charisma').value = dto.charisma || 10;

    // Col 1 - Skills (Checkboxes)
    // (Usa "name" dal DTO per trovare l'ID nel form)
    document.getElementById('cs-skill-acrobatics').checked = dto.acrobaticsSkillProficiency;
    document.getElementById('cs-skill-animal').checked = dto.animalHandlingSkillProficiency;
    document.getElementById('cs-skill-arcana').checked = dto.arcanaSkillProficiency;
    document.getElementById('cs-skill-athletics').checked = dto.athleticsSkillProficiency;
    document.getElementById('cs-skill-deception').checked = dto.deceptionSkillProficiency;
    document.getElementById('cs-skill-history').checked = dto.historySkillProficiency;
    document.getElementById('cs-skill-insight').checked = dto.insightSkillProficiency;
    document.getElementById('cs-skill-intimidation').checked = dto.intimidationSkillProficiency;
    document.getElementById('cs-skill-investigation').checked = dto.investigationSkillProficiency;
    document.getElementById('cs-skill-medicine').checked = dto.medicineSkillProficiency;
    document.getElementById('cs-skill-nature').checked = dto.natureSkillProficiency;
    document.getElementById('cs-skill-perception').checked = dto.perceptionSkillProficiency;
    document.getElementById('cs-skill-performance').checked = dto.performanceSkillProficiency;
    document.getElementById('cs-skill-persuasion').checked = dto.persuasionSkillProficiency;
    document.getElementById('cs-skill-religion').checked = dto.religionSkillProficiency;
    document.getElementById('cs-skill-sleight').checked = dto.sleightOfHandSkillProficiency;
    document.getElementById('cs-skill-stealth').checked = dto.stealthSkillProficiency;
    document.getElementById('cs-skill-survival').checked = dto.survivalSkillProficiency;
    
    // Col 2 - Combat
    document.getElementById('cs-ac').value = dto.armorClass || 10;
    document.getElementById('cs-initiative').value = dto.initiative || 0;
    document.getElementById('cs-speed').value = dto.speed || 30;
    document.getElementById('cs-max-hp').value = dto.maxHitPoints || 10;
    document.getElementById('cs-current-hp').value = dto.currentHitPoints || 10;
    document.getElementById('cs-temp-hp').value = dto.temporaryHitPoints || 0;

    // Col 2 - Traits
    document.getElementById('cs-personality').value = dto.personalityTraits || '';
    document.getElementById('cs-ideals').value = dto.ideals || '';
    document.getElementById('cs-bonds').value = dto.bonds || '';
    document.getElementById('cs-flaws').value = dto.flaws || '';
    
    // Col 3
    // (Nota: hai mappato "equipment" due volte nel DTO,
    //  ma qui li colleghiamo a campi diversi come da PDF)
    document.getElementById('cs-attacks').value = dto.equipment || ''; // Temporaneo
    document.getElementById('cs-equipment').value = dto.equipment || '';
    document.getElementById('cs-features').value = dto.featuresAndTraits || '';
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
    const classAndLevel = document.getElementById('cs-class-level').value.split(' ');
    
    const dto = {
        id: currentSheetId,
        
        // Header
        name: document.getElementById('cs-name').value,
        primaryClass: classAndLevel[0] || 'Sconosciuto',
        primaryLevel: parseInt(classAndLevel[1] || '1', 10),
        background: document.getElementById('cs-background').value,
        race: document.getElementById('cs-race').value,
        alignment: document.getElementById('cs-alignment').value,
        experiencePoints: parseInt(document.getElementById('cs-xp').value, 10),

        // Col 1 - Stats
        strength: parseInt(document.getElementById('cs-strength').value, 10),
        dexterity: parseInt(document.getElementById('cs-dexterity').value, 10),
        constitution: parseInt(document.getElementById('cs-constitution').value, 10),
        intelligence: parseInt(document.getElementById('cs-intelligence').value, 10),
        wisdom: parseInt(document.getElementById('cs-wisdom').value, 10),
        charisma: parseInt(document.getElementById('cs-charisma').value, 10),
        
        // Col 1 - Skills (Checkboxes)
        acrobaticsSkillProficiency: document.getElementById('cs-skill-acrobatics').checked,
        animalHandlingSkillProficiency: document.getElementById('cs-skill-animal').checked,
        arcanaSkillProficiency: document.getElementById('cs-skill-arcana').checked,
        athleticsSkillProficiency: document.getElementById('cs-skill-athletics').checked,
        deceptionSkillProficiency: document.getElementById('cs-skill-deception').checked,
        historySkillProficiency: document.getElementById('cs-skill-history').checked,
        insightSkillProficiency: document.getElementById('cs-skill-insight').checked,
        intimidationSkillProficiency: document.getElementById('cs-skill-intimidation').checked,
        investigationSkillProficiency: document.getElementById('cs-skill-investigation').checked,
        medicineSkillProficiency: document.getElementById('cs-skill-medicine').checked,
        natureSkillProficiency: document.getElementById('cs-skill-nature').checked,
        perceptionSkillProficiency: document.getElementById('cs-skill-perception').checked,
        performanceSkillProficiency: document.getElementById('cs-skill-performance').checked,
        persuasionSkillProficiency: document.getElementById('cs-skill-persuasion').checked,
        religionSkillProficiency: document.getElementById('cs-skill-religion').checked,
        sleightOfHandSkillProficiency: document.getElementById('cs-skill-sleight').checked,
        stealthSkillProficiency: document.getElementById('cs-skill-stealth').checked,
        survivalSkillProficiency: document.getElementById('cs-skill-survival').checked,

        // Col 2 - Combat
        armorClass: parseInt(document.getElementById('cs-ac').value, 10),
        initiative: parseInt(document.getElementById('cs-initiative').value, 10),
        speed: parseInt(document.getElementById('cs-speed').value, 10),
        maxHitPoints: parseInt(document.getElementById('cs-max-hp').value, 10),
        currentHitPoints: parseInt(document.getElementById('cs-current-hp').value, 10),
        temporaryHitPoints: parseInt(document.getElementById('cs-temp-hp').value, 10),

        // Col 2 - Traits
        personalityTraits: document.getElementById('cs-personality').value,
        ideals: document.getElementById('cs-ideals').value,
        bonds: document.getElementById('cs-bonds').value,
        flaws: document.getElementById('cs-flaws').value,
        
        // Col 3
        equipment: document.getElementById('cs-equipment').value,
        featuresAndTraits: document.getElementById('cs-features').value,
        
        // (La logica per "Attacks" andrebbe gestita separatamente,
        // per ora non viene salvata in un campo separato)
    };

    const data = await apiCall(`/api/player/sheets/${currentSheetId}`, 'PUT', dto);
    
    if (data) {
        successDiv.textContent = "Scheda salvata con successo!";
        successDiv.style.display = 'block';
    } else {
        errorDiv.textContent = "Errore nel salvataggio.";
        errorDiv.style.display = 'block';
    }
}

/**
 * NUOVA FUNZIONE: Gestisce il download PDF
 */
function handleDownloadPdf() {
    console.log("Avvio generazione PDF...");
    const sheetElement = document.getElementById('editSheetForm');
    const sheetName = document.getElementById('cs-name').value || 'scheda';

    // Opzioni per html2pdf
    const options = {
        margin: [5, 5, 5, 5], // margini (top, left, bottom, right) in mm
        filename: `${sheetName.replace(' ', '_')}.pdf`,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, useCORS: true, logging: false },
        jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };

    // Nascondi i bottoni dalla stampa
    const actions = document.querySelector('.sheet-actions');
    if(actions) actions.style.display = 'none';

    html2pdf().from(sheetElement).set(options).save().then(() => {
        // Rimostra i bottoni dopo aver generato il PDF
        if(actions) actions.style.display = 'block';
    });
}