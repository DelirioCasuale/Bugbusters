// ===== MODALE MODIFICA SCHEDA =====
var modifySchedaModal = document.getElementById("modifyScheda");
var openModifySchedaBtns = document.querySelectorAll(".open-modify-scheda-btn");

// Usa la classe per il pulsante X
var closeCharacterModalBtn = document.querySelector(".close-character-modal");

// Gestore per l'apertura
if (openModifySchedaBtns.length > 0 && modifySchedaModal) {
    openModifySchedaBtns.forEach(function(button) {
        button.addEventListener('click', function() {
            modifySchedaModal.style.display = "flex"; 
            modifySchedaModal.style.justifyContent = "center";
            modifySchedaModal.style.alignItems = "center";
        });
    });
}

// Gestore per la chiusura (pulsante X)
if (closeCharacterModalBtn && modifySchedaModal) {
    closeCharacterModalBtn.addEventListener('click', function() {
        modifySchedaModal.style.display = "none";
    });
}

// Gestore per la chiusura (click fuori)
window.addEventListener('click', function(event) {
    if (event.target == modifySchedaModal) {
        modifySchedaModal.style.display = "none";
    }
});


// ===== MODALE DETTAGLI CAMPAGNA =====
var campaignDetailsModal = document.getElementById("campaignDetailsModal");
var openCampaignDetailsBtns = document.querySelectorAll(".open-campaign-details-btn"); 
var closeCampaignDetailsBtn = document.querySelector(".close-campaign-details"); 

// MOCK-UP DATI: In un'applicazione reale, questi dati verrebbero caricati dal server
var campaignData = {
    1: { 
        title: "La Cittadella Perduta", 
        dm: "Gabibbo", 
        players: 4, 
        lastSession: "15/02/2025", 
        objectives: "Esplora le rovine del vecchio Canale 5, recupera l'artefatto perduto e sconfiggi Gerry Scotti prima che diventi un drago." 
    },
    2: { 
        title: "Il Fantabosco", 
        dm: "Lupo Lucio", 
        players: 7, 
        lastSession: "01/03/2025", 
        objectives: "Proteggi la principessa Odessa, riporta la pace nel Fantabosco e impara a fare la Scivolizia." 
    }
};

// Gestore per l'apertura (collega tutti i pulsanti)
if (openCampaignDetailsBtns.length > 0 && campaignDetailsModal) {
    openCampaignDetailsBtns.forEach(function(button) {
        button.addEventListener('click', function() {
            var campaignId = this.getAttribute('data-campaign-id');
            var data = campaignData[campaignId];

            if (data) {
                // Popola il modale con i dati del mock-up
                document.getElementById('campaignTitle').textContent = data.title;
                document.getElementById('campaignDM').textContent = data.dm;
                document.getElementById('campaignPlayers').textContent = data.players;
                document.getElementById('campaignLastSession').textContent = data.lastSession;
                document.getElementById('campaignObjectives').textContent = data.objectives;
            }
            
            // Apri il modale
            campaignDetailsModal.style.display = "flex"; 
            campaignDetailsModal.style.justifyContent = "center";
            campaignDetailsModal.style.alignItems = "center";
        });
    });
}

// Gestore per la chiusura (pulsante X)
if (closeCampaignDetailsBtn && campaignDetailsModal) {
    closeCampaignDetailsBtn.addEventListener('click', function() {
        campaignDetailsModal.style.display = "none";
    });
}

// Gestore per la chiusura (click fuori)
window.addEventListener('click', function(event) {
    if (event.target == campaignDetailsModal) {
        campaignDetailsModal.style.display = "none";
    }
});

// ===== MODALE CAMPAGNE ORFANE =====
// 1. Definisci le variabili degli elementi
var modal = document.getElementById("dateModal");
var dateInput = document.getElementById("session-date");
var closeButton = document.querySelector(".close-button");
var dateDisplay = document.getElementById("selectedDate");

// 2. Funzione per aprire il modale
function openModal(date) {
    // Formatta la data selezionata (es. YYYY-MM-DD -> DD/MM/YYYY)
    var parts = date.split('-');
    var formattedDate = parts[2] + '/' + parts[1] + '/' + parts[0];
    
    // Inserisci la data nel modale
    dateDisplay.textContent = formattedDate;
    
    // Mostra il modale (rendendolo flessibile per centrarlo)
    modal.style.display = "flex";
    modal.style.justifyContent = "center";
    modal.style.alignItems = "center";
}

// 3. Funzione per chiudere il modale
function closeModal() {
    modal.style.display = "none";
}

// 4. Gestore di eventi sul calendario
dateInput.addEventListener('change', function() {
    if (this.value) {
        openModal(this.value);
    }
});

// 5. Gestore di eventi sui pulsanti di chiusura
closeButton.addEventListener('click', closeModal);

// Chiudi il modale cliccando fuori da esso
window.addEventListener('click', function(event) {
    if (event.target == modal) {
        closeModal();
    }
});

// Chiudi il modale premendo il tasto ESC
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape' && modal.style.display === 'flex') {
        closeModal();
    }
});