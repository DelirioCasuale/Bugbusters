// ============================================================================
// JOIN CAMPAIGN FUNCTIONALITY - Script dedicato per gestire l'unione alle campagne
// ============================================================================

document.addEventListener('DOMContentLoaded', function () {
  initializeJoinCampaignButton();
});

// ============================================================================
// JOIN CAMPAIGN INITIALIZATION
// ============================================================================

function initializeJoinCampaignButton() {
  const joinCampaignButton = document.getElementById('join-campaign');

  if (joinCampaignButton) {
    joinCampaignButton.addEventListener('click', function () {
      scrollToJoinCampaignCard();
    });
  }
}

// ============================================================================
// SCROLL & HIGHLIGHT FUNCTIONALITY
// ============================================================================

function scrollToJoinCampaignCard() {
  // Trova la card per unirsi alla campagna
  const joinCard = document.querySelector('.add-campaign-card');

  if (joinCard) {
    // Scroll smooth alla card (senza centrare)
    joinCard.scrollIntoView({
      behavior: 'smooth',
      block: 'nearest',
      inline: 'nearest',
    });

    // Aggiungi un effetto di highlight temporaneo
    highlightJoinCard(joinCard);

    // Focus sul primo input del form
    setTimeout(() => {
      const firstInput = joinCard.querySelector('.invite-input');
      if (firstInput) {
        firstInput.focus();
      }
    }, 500); // Aspetta che lo scroll sia completato
  }
}

function highlightJoinCard(card) {
  // Salva lo stile originale
  const originalTransform = card.style.transform;
  const originalBoxShadow = card.style.boxShadow;

  // Applica l'effetto highlight
  card.style.transform = 'scale(1.02)';
  card.style.boxShadow = '0 0 20px var(--primary-purple-light)';
  card.style.transition = 'all 0.3s ease';

  // Rimuovi l'effetto dopo 2 secondi
  setTimeout(() => {
    card.style.transform = originalTransform;
    card.style.boxShadow = originalBoxShadow;
  }, 2000);
}

// ============================================================================
// EXPORT FOR GLOBAL ACCESS (if needed)
// ============================================================================

window.JoinCampaignUtils = {
  scrollToJoinCampaignCard,
  highlightJoinCard,
};
