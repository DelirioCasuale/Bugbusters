// ============================================================================
// DASHBOARD SCRIPT - Gestione funzionalità dashboard
// ============================================================================

document.addEventListener('DOMContentLoaded', function () {
  initializeSearchBars();
  initializeCardInteractions();
  initializeAddButtons();
});

// ============================================================================
// SEARCHBAR FUNCTIONALITY
// ============================================================================

function initializeSearchBars() {
  const searchInputs = document.querySelectorAll('.search-input');

  searchInputs.forEach((input) => {
    // Debounce per ottimizzare le performance
    let searchTimeout;

    input.addEventListener('input', function () {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(() => {
        performSearch(this);
      }, 300); // Aspetta 300ms prima di cercare
    });

    // Gestisci Enter key
    input.addEventListener('keypress', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        clearTimeout(searchTimeout);
        performSearch(this);
      }
    });

    // Gestisci focus/blur per UX
    input.addEventListener('focus', function () {
      this.closest('.search-container').classList.add('focused');
    });

    input.addEventListener('blur', function () {
      this.closest('.search-container').classList.remove('focused');
    });
  });
}

function performSearch(searchInput) {
  const searchTerm = searchInput.value.toLowerCase().trim();
  const searchName = searchInput.getAttribute('name');
  const section = searchInput.closest('.page');
  const cards = section.querySelectorAll('.card');

  console.log(`Searching for: "${searchTerm}" in section: ${searchName}`);

  // Se il termine di ricerca è vuoto, mostra tutte le card
  if (searchTerm === '') {
    showAllCards(cards);
    return;
  }

  // Filtra le card in base al termine di ricerca
  cards.forEach((card) => {
    const cardText = card.textContent.toLowerCase();
    const isMatch = cardText.includes(searchTerm);

    if (isMatch) {
      showCard(card);
      highlightSearchTerm(card, searchTerm);
    } else {
      hideCard(card);
    }
  });

  // Mostra messaggio se nessun risultato
  showNoResultsMessage(section, cards, searchTerm);
}

function showAllCards(cards) {
  cards.forEach((card) => {
    showCard(card);
    removeHighlight(card);
  });
  removeNoResultsMessage();
}

function showCard(card) {
  card.style.display = 'block';
  card.style.opacity = '1';
  card.style.transform = 'scale(1)';
}

function hideCard(card) {
  card.style.opacity = '0';
  card.style.transform = 'scale(0.95)';
  setTimeout(() => {
    if (card.style.opacity === '0') {
      card.style.display = 'none';
    }
  }, 200);
}

function highlightSearchTerm(card, searchTerm) {
  // Rimuovi highlight precedenti
  removeHighlight(card);

  // Aggiungi nuovo highlight (implementazione base)
  const textElements = card.querySelectorAll('h3, p');
  textElements.forEach((element) => {
    const text = element.textContent;
    const regex = new RegExp(`(${searchTerm})`, 'gi');
    if (regex.test(text)) {
      element.innerHTML = text.replace(regex, '<mark>$1</mark>');
    }
  });
}

function removeHighlight(card) {
  const highlighted = card.querySelectorAll('mark');
  highlighted.forEach((mark) => {
    mark.outerHTML = mark.innerHTML;
  });
}

function showNoResultsMessage(section, cards, searchTerm) {
  const visibleCards = Array.from(cards).filter(
    (card) => card.style.display !== 'none' && card.style.opacity !== '0'
  );

  if (visibleCards.length === 0) {
    let noResultsDiv = section.querySelector('.no-results-message');
    if (!noResultsDiv) {
      noResultsDiv = document.createElement('div');
      noResultsDiv.className = 'no-results-message';
      noResultsDiv.innerHTML = `
                <p>Nessun risultato trovato per "<strong>${searchTerm}</strong>"</p>
                <p class="no-results-hint">Prova con un termine diverso</p>
            `;
      section.querySelector('.card-container').appendChild(noResultsDiv);
    }
  } else {
    removeNoResultsMessage(section);
  }
}

function removeNoResultsMessage(section = null) {
  const noResultsMessages = section
    ? section.querySelectorAll('.no-results-message')
    : document.querySelectorAll('.no-results-message');

  noResultsMessages.forEach((message) => message.remove());
}

// ============================================================================
// CARD INTERACTIONS
// ============================================================================

function initializeCardInteractions() {
  const cards = document.querySelectorAll('.card');

  cards.forEach((card) => {
    // Hover effects già gestiti da CSS, ma possiamo aggiungere interazioni JS
    card.addEventListener('click', function () {
      handleCardClick(this);
    });

    // Aggiungi transizioni smooth per le animazioni di ricerca
    card.style.transition = 'opacity 0.2s ease, transform 0.2s ease';
  });
}

function handleCardClick(card) {
  const cardTitle = card.querySelector('h3').textContent;
  const section = card.closest('.page').querySelector('h2').textContent;

  console.log(`Card clicked: "${cardTitle}" in section: "${section}"`);

  // Qui puoi aggiungere la logica per aprire modal, navigare, etc.
  // Per ora mostriamo solo un feedback visivo
  card.style.transform = 'scale(0.98)';
  setTimeout(() => {
    card.style.transform = 'scale(1)';
  }, 150);
}

// ============================================================================
// ADD BUTTONS FUNCTIONALITY
// ============================================================================

function initializeAddButtons() {
  const addButtons = document.querySelectorAll('.add-card');

  addButtons.forEach((button) => {
    button.addEventListener('click', function () {
      handleAddButtonClick(this);
    });
  });
}

function handleAddButtonClick(button) {
  const section = button.closest('.page');
  const sectionTitle = section.querySelector('h2').textContent;
  const tooltip =
    button.closest('abbr')?.getAttribute('title') || 'Aggiungi elemento';

  console.log(
    `Add button clicked in section: "${sectionTitle}" - Action: "${tooltip}"`
  );

  // Feedback visivo per il bottone
  button.style.transform = 'scale(0.9)';
  setTimeout(() => {
    button.style.transform = 'scale(1)';
  }, 150);

  // Qui puoi aggiungere la logica per aprire modal di creazione, etc.
}

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

// Funzione per resettare tutte le ricerche
function clearAllSearches() {
  const searchInputs = document.querySelectorAll('.search-input');
  searchInputs.forEach((input) => {
    input.value = '';
    const section = input.closest('.page');
    const cards = section.querySelectorAll('.card');
    showAllCards(cards);
  });
  removeNoResultsMessage();
}

// Funzione per ottenere statistiche delle card visibili
function getVisibleCardsStats() {
  const sections = document.querySelectorAll('.page');
  const stats = {};

  sections.forEach((section) => {
    const sectionName = section.querySelector('h2').textContent;
    const allCards = section.querySelectorAll('.card');
    const visibleCards = Array.from(allCards).filter(
      (card) => card.style.display !== 'none' && card.style.opacity !== '0'
    );

    stats[sectionName] = {
      total: allCards.length,
      visible: visibleCards.length,
      hidden: allCards.length - visibleCards.length,
    };
  });

  return stats;
}

// Esporta funzioni per uso globale se necessario
window.DashboardUtils = {
  clearAllSearches,
  getVisibleCardsStats,
};
