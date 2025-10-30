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