/**
 * Variabili globali per lo stato dell'applicazione
 */
let jwtToken = sessionStorage.getItem('jwtToken') || null;
let currentUser = sessionStorage.getItem('currentUser') ? JSON.parse(sessionStorage.getItem('currentUser')) : null;

/**
 * Funzione helper (DRY) per tutte le chiamate API
 * @param {string} endpoint Es. /api/auth/login
 * @param {string} method Es. 'GET', 'POST', 'PATCH', 'DELETE'
 * @param {object | null} body Oggetto JSON da inviare (opzionale)
 * @returns {Promise<object|null>} Dati JSON della risposta o null in caso di errore
 */
async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');

    // Aggiunge il token JWT se disponibile e se l'endpoint non è pubblico (/api/auth)
    if (jwtToken && !endpoint.startsWith('/api/auth/')) {
        headers.append('Authorization', 'Bearer ' + jwtToken);
    }

    const options = {
        method: method,
        headers: headers
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        console.log(`--- API Call: ${method} ${endpoint} ---`, body ? body : '');
        const response = await fetch(endpoint, options);

        // Se la risposta è vuota (es. 204 No Content), restituisce un oggetto vuoto
        if (response.status === 204) {
            console.log(`--- API Response ${response.status} (No Content) ---`);
            return {};
        }

        // Se la risposta non ha contenuto o non è JSON valido, gestisci l'errore
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            // Se non è JSON, leggi come testo per debug (potrebbe essere HTML di errore)
            const textResponse = await response.text();
             console.error(`--- API Error ${response.status} (Not JSON) ---`, textResponse);
             alert(`Errore ${response.status}: Risposta non valida dal server. Controlla la console.`);
             // Se lo status è 403, potrebbe essere un problema di sicurezza
             if (response.status === 403) {
                 clearLoginData(); // Forza il logout se c'è un 403
                 window.location.href = 'landing.html';
             }
            return null;
        }

        const data = await response.json();

        if (!response.ok) {
            console.error(`--- API Error ${response.status} ---`, data);
            // Mostra l'errore all'utente
            alert(`Errore ${response.status}: ${data.message || 'Errore sconosciuto dal server'}`);
             // Se lo status è 403 o 401 (non autorizzato), forza il logout
             if (response.status === 403 || response.status === 401) {
                 clearLoginData();
                 window.location.href = 'landing.html';
             }
            return null;
        }

        console.log(`--- API Response ${response.status} ---`, data);
        return data; // Ritorna i dati JSON

    } catch (error) {
        // Gestisce errori di rete o errori nel parsing JSON
        console.error(`--- Network or Parsing Error ---`, error);
        alert(`Errore di rete o risposta non valida: ${error.message}. Controlla la console.`);
        return null;
    }
}


/**
 * Gestione dello stato di autenticazione
 */
function saveLoginData(token, userDetails) {
    jwtToken = token;
    currentUser = {
        id: userDetails.id,
        username: userDetails.username,
        email: userDetails.email,
        roles: userDetails.roles || [] // Assicura che roles sia sempre un array
    };
    sessionStorage.setItem('jwtToken', jwtToken);
    sessionStorage.setItem('currentUser', JSON.stringify(currentUser));
    // Non chiamare updateUI qui, lo facciamo dopo il redirect
}

function clearLoginData() {
    jwtToken = null;
    currentUser = null;
    sessionStorage.removeItem('jwtToken');
    sessionStorage.removeItem('currentUser');
    // Non chiamare updateUI qui, viene chiamato dalla pagina di destinazione
}

function isAuthenticated() {
    // Controlla anche che il token non sia solo presente ma valido (potrebbe essere scaduto)
    // Per un controllo reale, dovremmo decodificare il token e verificare la scadenza `exp`
    // Per ora, ci fidiamo del sessionStorage
    return sessionStorage.getItem('jwtToken') !== null && sessionStorage.getItem('currentUser') !== null;
}

function getCurrentUserFromStorage() {
    // Legge sempre da sessionStorage per coerenza tra pagine
    const user = sessionStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
}


function hasRole(role) {
    const user = getCurrentUserFromStorage();
    if (!user) return false;
    // Aggiunge 'ROLE_' se non presente per coerenza
    const roleName = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    return user.roles.includes(roleName);
}
// Helper specifici per i nostri ruoli dinamici (che arrivano dal backend)
function isPlayer() { return hasRole('PLAYER'); }
function isMaster() { return hasRole('MASTER'); }
function isAdmin() { return hasRole('ADMIN'); }


/**
 * Gestione Modali (Classe unica per tutti)
 */
class Modal {
    constructor(modalId) {
        this.modal = document.getElementById(modalId);
        if (!this.modal) {
            console.error(`Modal with id ${modalId} not found.`);
            return;
        }
        this.overlay = this.modal.querySelector('.modal-overlay');
        this.closeBtn = this.modal.querySelector('.modal-close');
        this.cancelBtns = this.modal.querySelectorAll('.modal-cancel');
        this.form = this.modal.querySelector('form');
        this.errorDiv = this.modal.querySelector('.error-message');
        this._initListeners();
    }

    _initListeners() {
        if (this.overlay) this.overlay.onclick = () => this.hide();
        if (this.closeBtn) this.closeBtn.onclick = () => this.hide();
        this.cancelBtns.forEach(btn => btn.onclick = () => this.hide());
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isVisible()) this.hide();
        });
    }

    show() { if (this.modal) { this.modal.classList.add('show'); document.body.style.overflow = 'hidden'; } }
    hide() { if (this.modal) { this.modal.classList.remove('show'); document.body.style.overflow = ''; this.clearForm(); } }
    isVisible() { return this.modal ? this.modal.classList.contains('show') : false; }
    showError(message) { if(this.errorDiv) { this.errorDiv.textContent = message; this.errorDiv.classList.add('show'); } }
    hideError() { if(this.errorDiv) { this.errorDiv.textContent = ''; this.errorDiv.classList.remove('show'); } }
    clearForm() { if(this.form) this.form.reset(); this.hideError(); }
}

// Istanziamo i modal globalmente DOPO che il DOM è caricato
let loginModal, createSheetModal, joinCampaignModal, createCampaignModal, claimCampaignModal;


/**
 * Gestori Eventi Specifici
 */
async function handleLogin(event) {
    event.preventDefault();
    if (!loginModal) return; // Sicurezza
    loginModal.hideError();
    const usernameInput = document.getElementById('login-username');
    const passwordInput = document.getElementById('login-password');
    const username = usernameInput?.value;
    const password = passwordInput?.value;


    if (!username || !password) {
        loginModal.showError('Inserisci username e password');
        return;
    }

    const data = await apiCall('/api/auth/login', 'POST', { username, password });
    if (data && data.token) {
        saveLoginData(data.token, data); // Salva token e user in sessionStorage
        loginModal.hide();
        // Reindirizza alla pagina corretta
        if (isAdmin()) {
            window.location.href = 'admin.html';
        } else {
            window.location.href = 'dashboard.html';
        }
    } else if (!data) {
        // Se apiCall ritorna null (errore di rete o parsing)
        loginModal.showError('Errore durante il login. Controlla la console.');
    } else {
        // Se apiCall ritorna dati ma senza token (errore logico dal backend)
        loginModal.showError(data.message || 'Credenziali non valide.');
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const errorDiv = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');
    if (errorDiv) errorDiv.style.display = 'none';
    if (successDiv) successDiv.style.display = 'none';

    const usernameInput = document.getElementById('reg-username');
    const emailInput = document.getElementById('reg-email');
    const passwordInput = document.getElementById('reg-password');
    const username = usernameInput?.value;
    const email = emailInput?.value;
    const password = passwordInput?.value;
    // const confirmPassword = document.getElementById('confirm-password').value; // Aggiungere controllo se necessario

    if (!username || !email || !password) {
         if (errorDiv) { errorDiv.textContent = 'Tutti i campi sono obbligatori.'; errorDiv.style.display = 'block'; }
         return;
    }
    // Aggiungere validazione frontend (es. password match, formato email) se vuoi

    const data = await apiCall('/api/auth/register', 'POST', { username, email, password });
    if (data && data.message) {
        if (data.message.toLowerCase().includes('successo')) {
             if (successDiv) {
                 successDiv.textContent = data.message + " Ora puoi accedere.";
                 successDiv.style.display = 'block';
             }
            document.getElementById('registerForm')?.reset(); // Pulisce il form
        } else {
            if (errorDiv) { // Mostra errori (es. username già in uso)
                errorDiv.textContent = data.message;
                errorDiv.style.display = 'block';
            }
        }
    } else if (!data) {
         if (errorDiv) { errorDiv.textContent = 'Errore durante la registrazione. Controlla la console.'; errorDiv.style.display = 'block'; }
    }
}

function handleLogout(event) {
    event.preventDefault();
    clearLoginData();
    window.location.href = 'landing.html'; // Reindirizza sempre alla home dopo il logout
}

// Handler per i pulsanti "Diventa..."
async function handleBecomeRole(role) {
     const endpoint = role === 'player' ? '/api/profile/become-player' : '/api/profile/become-master';
     const profileMessage = document.getElementById('profile-message');
     if(profileMessage) {
         profileMessage.textContent = 'Attendere...'; // Messaggio di attesa
         profileMessage.className = 'info-message show'; // Stile informativo
     }

     const data = await apiCall(endpoint, 'POST');
     if (data && data.message) {
         if (profileMessage) {
             profileMessage.textContent = data.message + " Fai logout e login per vedere i cambiamenti.";
             // Mantiene lo stile info se successo, altrimenti apiCall mostrerà alert
         }
         // Aggiorna UI per nascondere il pulsante (anche se serve re-login per il ruolo)
         if (role === 'player') document.getElementById('btn-become-player').style.display = 'none';
         if (role === 'master') document.getElementById('btn-become-master').style.display = 'none';

     } else if (profileMessage) {
          profileMessage.textContent = 'Operazione fallita.'; // Messaggio generico se apiCall fallisce
          profileMessage.className = 'error-message show';
     }
}

// --- Handler Azioni Player ---
async function handleCreateSheet(event) {
    event.preventDefault();
    if (!createSheetModal) return;
    createSheetModal.hideError();
    const nameInput = document.getElementById('sheet-name');
    const classInput = document.getElementById('sheet-class');
    const raceSelect = document.getElementById('sheet-race');
    const name = nameInput?.value;
    const primaryClass = classInput?.value;
    const race = raceSelect?.value;

    if (!name || !primaryClass || !race) {
        createSheetModal.showError("Tutti i campi sono obbligatori.");
        return;
    }
    const data = await apiCall('/api/player/sheets', 'POST', { name, primaryClass, race });
    if (data) {
        createSheetModal.hide();
        loadPlayerData(); // Ricarica i dati del player
    }
}

async function handleJoinCampaign(event) {
    event.preventDefault();
    if (!joinCampaignModal) return;
     joinCampaignModal.hideError();
     const codeInput = document.getElementById('join-code'); // Assicurati che l'ID sia corretto nell'HTML
     const sheetSelect = document.getElementById('join-sheet-id'); // Assicurati che l'ID sia corretto
     const inviteCode = codeInput?.value;
     const characterSheetId = sheetSelect?.value;

     if (!inviteCode || !characterSheetId) {
         joinCampaignModal.showError("Codice invito e scheda sono obbligatori.");
         return;
     }

     const data = await apiCall('/api/player/campaigns/join', 'POST', { inviteCode, characterSheetId: Number(characterSheetId) });
     if(data) {
         joinCampaignModal.hide();
         loadPlayerData(); // Ricarica le liste campagne player
     }
}

async function handleVoteProposal(proposalId) {
     console.log("Voto per proposta:", proposalId);
     const data = await apiCall(`/api/player/proposals/${proposalId}/vote`, 'POST');
     if (data) {
         loadPlayerData(); // Ricarica le proposte per aggiornare lo stato "votato"
     }
}

// --- Handler Azioni Master ---
async function handleCreateCampaign(event) {
    event.preventDefault();
    if (!createCampaignModal) return;
    createCampaignModal.hideError();
    const titleInput = document.getElementById('camp-title'); // Assicurati ID corretto
    const descriptionInput = document.getElementById('camp-desc'); // Assicurati ID corretto
    const title = titleInput?.value;
    const description = descriptionInput?.value;

     if (!title || !description) {
         createCampaignModal.showError("Titolo e descrizione sono obbligatori.");
         return;
     }

     const data = await apiCall('/api/master/campaigns', 'POST', { title, description });
     if(data) {
         createCampaignModal.hide();
         loadMasterData(); // Ricarica lista campagne master
     }
}

async function handleClaimCampaign(event) {
    event.preventDefault();
    if (!claimCampaignModal) return;
    claimCampaignModal.hideError();
    const codeInput = document.getElementById('claim-code'); // Assicurati ID corretto
    const inviteMastersCode = codeInput?.value;

      if (!inviteMastersCode) {
          claimCampaignModal.showError("Il codice invito master è obbligatorio.");
          return;
      }

      const data = await apiCall('/api/master/campaigns/claim', 'POST', { inviteMastersCode });
      if(data) {
          claimCampaignModal.hide();
          loadMasterData(); // Ricarica lista campagne master (la campagna reclamata apparirà)
      }
}

// --- Handler Azioni Admin ---
async function handleBanUser(userId, username) {
     if (confirm(`Sei sicuro di voler bannare l'utente ${username} (ID: ${userId})? L'utente sarà sospeso per 1 anno.`)) {
         const data = await apiCall(`/api/admin/users/${userId}/ban`, 'POST');
         if (data) {
             // Ricarica la lista utenti usando il filtro attualmente attivo
             const activeFilter = document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
             loadAdminData(activeFilter);
         }
     }
}

/**
 * Funzioni per caricare e mostrare i dati nelle Dashboard
 */
async function loadPlayerData() {
    console.log("Caricamento dati Player...");
    const user = getCurrentUserFromStorage();
    if (!user || !isPlayer()) return; // Non caricare se non è player

    // Carica Schede
    const sheets = await apiCall('/api/player/sheets');
    const sheetsList = document.getElementById('player-sheets-list');
    if (sheets && sheetsList) {
        if(sheets.length > 0) {
            sheetsList.innerHTML = sheets.map(sheet => `
                <div class="card">
                    <h3>${sheet.name || 'Senza nome'}</h3>
                    <p>ID: ${sheet.id} · ${sheet.primaryClass || '?'} Lvl ${sheet.primaryLevel || '?'} · ${sheet.race || '?'}</p>
                    <button class="btn-secondary" disabled>Modifica (WIP)</button>
                </div>
            `).join('');
        } else {
            sheetsList.innerHTML = '<p>Non hai ancora creato nessuna scheda.</p>';
        }
         // Popola la select nel modal join campaign
        const joinSheetSelect = document.getElementById('join-sheet-id');
        if (joinSheetSelect) {
            joinSheetSelect.innerHTML = '<option value="" disabled selected>Seleziona una scheda...</option>'; // Opzione default
            sheets.forEach(sheet => {
                joinSheetSelect.innerHTML += `<option value="${sheet.id}">${sheet.name} (Lvl ${sheet.primaryLevel})</option>`;
            });
        }
    } else if (sheetsList) {
         sheetsList.innerHTML = '<p>Errore nel caricamento delle schede.</p>';
    }


    // Carica Campagne Unite
    const joinedCampaigns = await apiCall('/api/player/campaigns/joined');
    const campaignsList = document.getElementById('player-campaigns-list');
    if (joinedCampaigns && campaignsList) {
        if(joinedCampaigns.length > 0) {
            campaignsList.innerHTML = joinedCampaigns.map(jc => `
                <div class="card">
                    <h3>${jc.campaignTitle || 'Campagna sconosciuta'}</h3>
                    <p>ID Camp: ${jc.campaignId}</p>
                    <p>Usando: ${jc.characterUsed?.name || '?'} (ID: ${jc.characterUsed?.id || '?'})</p>
                    <button class="btn-primary" disabled>Entra (WIP)</button>
                </div>
            `).join('');
        } else {
             campaignsList.innerHTML = '<p>Non fai ancora parte di nessuna campagna.</p>';
        }
    } else if (campaignsList) {
         campaignsList.innerHTML = '<p>Errore nel caricamento delle campagne.</p>';
    }


    // Carica Campagne Orfane
     const orphanedCampaigns = await apiCall('/api/player/campaigns/orphaned');
     const orphanedList = document.getElementById('player-orphaned-campaigns-list');
     const orphanedSection = orphanedList?.closest('.dashboard-section');
     if (orphanedCampaigns && orphanedList && orphanedSection) {
         if(orphanedCampaigns.length > 0) {
             orphanedList.innerHTML = orphanedCampaigns.map(oc => `
                 <div class="card">
                     <h3>${oc.campaignTitle || 'Campagna sconosciuta'} (Orfana!)</h3>
                     <p>ID Camp: ${oc.campaignId}</p>
                     <p>Invita un nuovo Master con:</p>
                     <span class="code">${oc.inviteMastersCode}</span>
                     <small>Scadenza: ${new Date(oc.deletionDeadline).toLocaleString()}</small>
                 </div>
             `).join('');
             orphanedSection.style.display = 'block';
         } else {
             orphanedList.innerHTML = ''; // Nessuna campagna orfana
             orphanedSection.style.display = 'none'; // Nasconde la sezione
         }
     } else if (orphanedSection) {
          orphanedSection.style.display = 'none'; // Nasconde se errore o non trovato
     }


    // Carica Proposte Attive
    const proposals = await apiCall('/api/player/proposals');
    const proposalsList = document.getElementById('player-proposals-list');
    const proposalsSection = proposalsList?.closest('.dashboard-section');
    if (proposals && proposalsList && proposalsSection) {
        if(proposals.length > 0) {
            proposalsList.innerHTML = proposals.map(p => `
                <div class="card proposal-card">
                    <h3>${p.campaignTitle || '?'}</h3>
                    <p>Data Proposta: ${new Date(p.proposedDate).toLocaleString()}</p>
                    <p>Scadenza Voto: ${new Date(p.expiresOn).toLocaleString()}</p>
                    ${p.hasVoted
                        ? '<span class="voted">Votato</span>'
                        : `<button class="btn-primary" onclick="handleVoteProposal(${p.proposalId})">Vota Sì</button>`
                     }
                </div>
            `).join('');
            proposalsSection.style.display = 'block';
        } else {
             proposalsList.innerHTML = '<p>Nessuna proposta di sessione attiva al momento.</p>';
             // Lasciamo la sezione visibile ma con il messaggio
             proposalsSection.style.display = 'block';
        }
    } else if (proposalsSection) {
         proposalsSection.style.display = 'none'; // Nasconde se errore
    }
}

async function loadMasterData() {
    console.log("Caricamento dati Master...");
    const user = getCurrentUserFromStorage();
    if (!user || !isMaster()) return; // Non caricare se non è master

    // Carica Campagne del Master
    const campaigns = await apiCall('/api/master/campaigns');
    const campaignsList = document.getElementById('master-campaigns-list');
    if (campaigns && campaignsList) {
         if(campaigns.length > 0) {
            campaignsList.innerHTML = campaigns.map(c => `
                <div class="card">
                    <h3>${c.title || 'Senza Titolo'}</h3>
                    <p>ID: ${c.id}</p>
                    <p>Codice Player: <span class="code">${c.invitePlayersCode}</span></p>
                    <p>Codice Master: <span class="code">${c.inviteMastersCode}</span></p>
                     <button class="btn-primary" onclick="viewCampaignDetails(${c.id})">Gestisci (WIP)</button>
                </div>
            `).join('');
         } else {
              campaignsList.innerHTML = '<p>Non hai ancora creato nessuna campagna.</p>';
         }
    } else if (campaignsList) {
         campaignsList.innerHTML = '<p>Errore nel caricamento delle campagne.</p>';
    }
}

async function loadAdminData(filter = 'all') {
     console.log("Caricamento dati Admin, filtro:", filter);
     const user = getCurrentUserFromStorage();
     if (!user || !isAdmin()) return; // Sicurezza extra

     let endpoint = '/api/admin/users';
     if(filter === 'players') endpoint = '/api/admin/users/players';
     else if (filter === 'masters') endpoint = '/api/admin/users/masters';

     const users = await apiCall(endpoint);
     const tableBody = document.querySelector('#users-table tbody');

     if (users && tableBody) {
          if (users.length > 0) {
             tableBody.innerHTML = users.map(user => `
                 <tr>
                     <td>${user.id}</td>
                     <td>${user.username}</td>
                     <td>${user.email}</td>
                     <td>
                         ${user.admin ? '<span class="role-badge admin">ADMIN</span>' : ''}
                         ${user.player ? '<span class="role-badge player">PLAYER</span>' : ''}
                         ${user.master ? '<span class="role-badge master">MASTER</span>' : ''}
                         ${!user.admin && !user.player && !user.master ? 'Utente Base' : ''}
                     </td>
                     <td>
                         <span class="${user.banned ? 'status-banned' : 'status-active'}">
                             ${user.banned ? 'Bannato' : 'Attivo'}
                         </span>
                     </td>
                     <td>
                         ${!user.banned && !user.admin // Non permettere di bannare altri admin
                             ? `<button class="action-button ban" onclick="handleBanUser(${user.id}, '${user.username}')">Banna</button>`
                             : user.banned ? '<button class="action-button" disabled>Sblocca (WIP)</button>' : '' // Mostra sblocca se bannato, niente se admin
                         }
                          <button class="action-button" disabled>Modifica (WIP)</button>
                     </td>
                 </tr>
             `).join('');
          } else {
              tableBody.innerHTML = `<tr><td colspan="6">Nessun utente trovato con il filtro '${filter}'.</td></tr>`;
          }
     } else if (tableBody) {
         tableBody.innerHTML = '<tr><td colspan="6">Errore nel caricamento degli utenti.</td></tr>';
     }

     // Aggiorna stato attivo filtri
     document.querySelectorAll('.btn-filter').forEach(btn => {
         btn.classList.toggle('active', btn.dataset.filter === filter);
     });
}

// Funzione placeholder per dettagli campagna (Master)
function viewCampaignDetails(campaignId) {
     // In futuro, potremmo reindirizzare a una pagina campaign_details.html?id=...
     // o aprire un modal più grande con i dettagli caricati da /api/master/campaigns/{id}
     alert(`Funzionalità "Gestisci Campagna ${campaignId}" non ancora implementata.`);
}


/**
 * Inizializzazione all'avvio (DOM Ready)
 */
document.addEventListener('DOMContentLoaded', () => {
    const currentPage = window.location.pathname.split('/').pop(); // Prende solo il nome del file

    console.log("Pagina corrente:", currentPage);
    console.log("Autenticato?", isAuthenticated());

    // --- GUARDIA DI AUTENTICAZIONE ---
    if (currentPage === 'dashboard.html' || currentPage === 'admin.html') {
        if (!isAuthenticated()) {
            console.warn("Utente non autenticato in sessionStorage. Reindirizzamento a landing.html");
            window.location.href = 'landing.html';
            return;
        }
        if (currentPage === 'admin.html' && !isAdmin()) {
             console.warn("Utente non ADMIN sta provando ad accedere a admin.html. Reindirizzamento...");
             alert("Accesso non autorizzato all'area admin.");
             window.location.href = 'landing.html';
             return;
        }
        // Se arriviamo qui, l'utente è autenticato (e autorizzato per admin.html se necessario)
        initializeProtectedPage(); // Inizializza le pagine protette
    } else {
        // Setup per pagine pubbliche (landing, register)
        initializePublicPage();
    }

    // Aggiorna l'UI generale (principalmente la navbar) su TUTTE le pagine
    updateGeneralUI();
});

/** Funzione per inizializzare le pagine pubbliche */
function initializePublicPage() {
    console.log("Inizializzazione pagina pubblica...");
    // Istanzia il modal di login se presente
    if(document.getElementById('loginModal')) loginModal = new Modal('loginModal');

    // Listener per triggerare il modal di login
    document.querySelectorAll('.login-trigger').forEach(el => el.onclick = (e) => {
        e.preventDefault();
        loginModal?.show();
    });

    // Listener per i form di login e registrazione (se presenti)
    document.getElementById('loginForm')?.addEventListener('submit', handleLogin);
    document.getElementById('registerForm')?.addEventListener('submit', handleRegister);
}

/** Funzione per inizializzare le pagine protette (Dashboard, Admin) */
function initializeProtectedPage() {
     console.log("Inizializzazione pagina protetta...");
     // Istanzia i modali specifici
    if(document.getElementById('createSheetModal')) createSheetModal = new Modal('createSheetModal');
    // TODO: Aggiungere HTML e istanziare gli altri modal
    // if(document.getElementById('joinCampaignModal')) joinCampaignModal = new Modal('joinCampaignModal');
    // if(document.getElementById('createCampaignModal')) createCampaignModal = new Modal('createCampaignModal');
    // if(document.getElementById('claimCampaignModal')) claimCampaignModal = new Modal('claimCampaignModal');

    // Listener per aprire i modali
    document.getElementById('btn-show-create-sheet-modal')?.addEventListener('click', () => createSheetModal?.show());
    // TODO: Aggiungere listener per aprire gli altri modal
    // document.getElementById('btn-show-join-campaign-modal')?.addEventListener('click', () => joinCampaignModal?.show());
    // document.getElementById('btn-show-create-campaign-modal')?.addEventListener('click', () => createCampaignModal?.show());
    // document.getElementById('btn-show-claim-campaign-modal')?.addEventListener('click', () => claimCampaignModal?.show());

    // Listener per i submit dei form nei modali
    document.getElementById('createSheetForm')?.addEventListener('submit', handleCreateSheet);
    // TODO: Aggiungere listener submit per gli altri form modali
    // document.getElementById('joinCampaignForm')?.addEventListener('submit', handleJoinCampaign);
    // document.getElementById('createCampaignForm')?.addEventListener('submit', handleCreateCampaign);
    // document.getElementById('claimCampaignForm')?.addEventListener('submit', handleClaimCampaign);


    // Listener per i pulsanti "Diventa..." (solo in dashboard.html)
     document.getElementById('btn-become-player')?.addEventListener('click', () => handleBecomeRole('player'));
     document.getElementById('btn-become-master')?.addEventListener('click', () => handleBecomeRole('master'));

     // Listener per i filtri Admin (solo in admin.html)
     document.querySelectorAll('.btn-filter')?.forEach(btn => {
         btn.addEventListener('click', (e) => loadAdminData(e.target.dataset.filter));
     });

    // Aggiorna l'UI specifica e carica i dati
    updateDashboardAdminUI();
}


/** Funzione per aggiornare UI specifica dashboard/admin */
function updateDashboardAdminUI() {
    const user = getCurrentUserFromStorage(); // Legge sempre da storage
    if (!user) return; // Sicurezza extra

    if (document.body.classList.contains('dashboard-body') && !isAdmin()) {
        const playerSection = document.getElementById('player-section');
        const masterSection = document.getElementById('master-section');
        const profileActions = document.getElementById('profile-actions');
        const btnBecomePlayer = document.getElementById('btn-become-player');
        const btnBecomeMaster = document.getElementById('btn-become-master');
        const dashboardTitle = document.getElementById('dashboard-title');
        const dashboardSubtitle = document.getElementById('dashboard-subtitle');

        let title = `Bentornato, ${user.username}!`;
        let subtitle = "Gestisci qui le tue avventure.";
        if (isPlayer() && isMaster()) title = "Dashboard Giocatore & Master";
        else if (isPlayer()) title = "Dashboard Giocatore";
        else if (isMaster()) title = "Dashboard Master";
        else subtitle = "Scegli il tuo ruolo per iniziare.";

        if(dashboardTitle) dashboardTitle.textContent = title;
        if(dashboardSubtitle) dashboardSubtitle.textContent = subtitle;

        if (playerSection) playerSection.style.display = isPlayer() ? 'block' : 'none';
        if (masterSection) masterSection.style.display = isMaster() ? 'block' : 'none';

        if (profileActions) {
             const showActions = !isPlayer() || !isMaster(); // Mostra se manca almeno un ruolo
             profileActions.style.display = showActions ? 'flex' : 'none';
             if (btnBecomePlayer) btnBecomePlayer.style.display = !isPlayer() ? 'inline-block' : 'none';
             if (btnBecomeMaster) btnBecomeMaster.style.display = !isMaster() ? 'inline-block' : 'none';
        }

        // Carica i dati specifici
        if (isPlayer()) loadPlayerData();
        if (isMaster()) loadMasterData();

    } else if (document.body.classList.contains('admin-body') && isAdmin()) {
        loadAdminData(); // Carica i dati admin (filtro 'all' di default)
    }
}

/** Funzione per aggiornare UI generale (navbar) */
function updateGeneralUI() {
    const isAuth = isAuthenticated();
    const user = getCurrentUserFromStorage();
    const nav = document.querySelector('header nav');

    if (!nav) return; // Esce se non c'è la navbar

    if (isAuth && user) {
        // Navbar utente loggato
        let navHTML = `<span id="welcome-user">Benvenuto, ${user.username}</span>`;
        const currentPage = window.location.pathname.split('/').pop();

        if (currentPage === 'dashboard.html') {
             if (isPlayer()) navHTML += `<a href="#player-section">Vista Player</a>`;
             if (isMaster()) navHTML += `<a href="#master-section">Vista Master</a>`;
        } else if (currentPage === 'admin.html' && isAdmin()) {
             navHTML = `<span id="welcome-user">Admin: ${user.username}</span>`;
             // Aggiungere link specifici admin se necessario
        } else if (currentPage !== 'dashboard.html' && currentPage !== 'admin.html') {
             // Navbar su pagine pubbliche ma loggato
              if (isAdmin()) navHTML += `<a href="admin.html">Admin Dashboard</a>`;
              else navHTML += `<a href="dashboard.html">Dashboard</a>`; // Link alla dashboard
        }
         navHTML += `<a href="#" id="logout-button">Logout</a>`;
         nav.innerHTML = navHTML;

    } else {
        // Navbar utente NON loggato
        nav.innerHTML = `
            <a href="#" class="login-trigger">Accedi</a>
            <a href="register.html">Registrati</a>
        `;
         // Riattacca listener per trigger login
         document.querySelectorAll('.login-trigger').forEach(el => el.onclick = (e) => {
             e.preventDefault();
             // Assicurati che il modal sia istanziato SE siamo su una pagina pubblica
             if(loginModal) loginModal.show();
             else console.error("Login modal non trovato su questa pagina.");
         });
    }
}