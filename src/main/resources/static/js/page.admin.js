import { apiCall } from './modules/api.js';
import { isAuthenticated, isAdmin, handleLogout } from './modules/auth.js';
import { Modal, updateGeneralUI } from './modules/ui.js';

// let loadedUsers = [];
const PAGE_SIZE = 50; // Dimensione fissa di 50
let currentPage = 0; // Pagina iniziale (indice 0)
let editUserModal; // Nuovo modal
let infoModal;
let confirmationModal; // <-- DICHIARAZIONE NUOVO MODALE
let currentEditingUserId = null; // ID dell'utente che stiamo modificando

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
    if (!isAuthenticated()) {
        console.warn("Utente non autenticato. Reindirizzamento a landing.html");
        window.location.replace('landing.html');
        return;
    }
    if (!isAdmin()) {
        console.warn("Accesso non admin a admin.html. Reindirizzamento...");
        alert("Accesso non autorizzato all'area admin.");
        window.location.replace('landing.html');
        return;
    }
    // ---------------------------------

    // Se la guardia passa, inizializza
    updateGeneralUI();

    const searchInput = document.getElementById('user-search-input');
    const searchButton = document.getElementById('user-search-button');

    if (searchInput) {
        searchInput.addEventListener('keyup', () => {
            // Quando l'utente digita, resetta la pagina a 0 e ricarica i dati
            currentPage = 0;
            const activeFilter = document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
            loadAdminData(activeFilter, currentPage); // Chiamiamo loadAdminData
        });
    }

    if (searchButton) {
        searchButton.disabled = true;
        searchButton.style.opacity = 0.7; // Opzionale: per mostrare che non è cliccabile
    }



    document.querySelectorAll('.btn-filter')?.forEach(btn => {
        btn.addEventListener('click', (e) => {
            currentPage = 0; // Resetta la pagina
            loadAdminData(e.target.dataset.filter, currentPage);
        });
    });

    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    document.getElementById('btn-prev-page')?.addEventListener('click', () => changePage(-1));
    document.getElementById('btn-next-page')?.addEventListener('click', () => changePage(1));
    editUserModal = new Modal('editUserModal'); // Inizializza il nuovo modal
    infoModal = new Modal('infoModal'); // <-- INIZIALIZZAZIONE NUOVO MODALE
    confirmationModal = new Modal('confirmationModal'); // <-- INIZIALIZZAZIONE NUOVO MODALE
    document.getElementById('editUserForm')?.addEventListener('submit', handleUpdateUser);

    loadAdminData();// Primo caricamento
});

// ... (funzioni handleBanUser e loadAdminData rimangono invariate) ...

// function filterUsersAndRender(filter) {
//     // ... (omissis) ...
//     // Se stai usando la paginazione, la logica di 'filter' qui è ridondante,
//     // perché i filtri 'players'/'masters' sono già gestiti dall'endpoint nel loadAdminData.
//     // Qui devi solo applicare la barra di ricerca.

//     const searchInput = document.getElementById('user-search-input')?.value.toLowerCase().trim();

//     // Applica solo la ricerca testuale, la paginazione e i filtri di ruolo vengono dal backend
//     let usersToRender = loadedUsers;

//     if (searchInput) {
//         usersToRender = loadedUsers.filter(user =>
//             user.username.toLowerCase().includes(searchInput) ||
//             user.email.toLowerCase().includes(searchInput)
//         );
//     }

//     const tableBody = document.querySelector('#users-table tbody');
//     renderUsersTable(usersToRender, tableBody, filter);
// }

// NUOVA FUNZIONE HELPER: Per mostrare il modal di Notifica (Successo/Errore)
function showInfoModal(title, text, isError = false) {
    const titleEl = document.getElementById('adminInfoModalTitle');
    const textEl = document.getElementById('adminInfoModalText');

    if (titleEl) {
        titleEl.textContent = title;
        titleEl.style.color = isError ? 'var(--error-color)' : 'var(--primary-purple-light)';
    }
    if (textEl) {
        textEl.textContent = text;
    }

    infoModal?.show();
}

// FUNZIONE MODIFICATA: GESTIONE BAN (Usa il modal personalizzato)
async function handleBanUser(userId, username) {
    const title = "Conferma Ban Utente";
    const text = `Sei sicuro di voler bannare l'utente ${username} (ID: ${userId})? L'utente sarà sospeso per 1 anno e le sue campagne Master orfane.`;

    showConfirmation(title, text, async () => {
        const data = await apiCall(`/api/admin/users/${userId}/ban`, 'POST');
        if (data && data.status) { // Errore 4xx
            showInfoModal("Operazione Fallita", data.message, true);
        } else if (data && data.message) { // Successo
            showInfoModal("Utente Bannato", data.message, false);
            const activeFilter = document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
            loadAdminData(activeFilter, currentPage);
        }
    });
}
window.handleBanUser = handleBanUser;

// FUNZIONE MODIFICATA: GESTIONE SBLOCCA (Usa il modal personalizzato)
async function handleUnbanUser(userId, username) {
    const title = "Conferma Sblocco Utente";
    const text = `Sei sicuro di voler sbloccare l'utente ${username} (ID: ${userId})? La sospensione sarà revocata.`;

    showConfirmation(title, text, async () => {
        const data = await apiCall(`/api/admin/users/${userId}/unban`, 'PUT');
        if (data && data.status) { // Errore 4xx
            showInfoModal("Operazione Fallita", data.message, true);
        } else if (data && data.message) { // Successo
            showInfoModal("Utente Sbloccato", data.message, false);
            const activeFilter = document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
            loadAdminData(activeFilter, currentPage);
        }
    });
}
window.handleUnbanUser = handleUnbanUser;

// NUOVO: Prepara e mostra il modal di modifica
function showEditUserModal(user) {
    currentEditingUserId = user.id;
    document.getElementById('edit-username').value = user.username;
    document.getElementById('edit-email').value = user.email;
    document.getElementById('edit-image-url').value = user.profileImageUrl || '';
    document.getElementById('edit-is-admin').checked = user.admin;

    editUserModal.show();
}
window.showEditUserModal = showEditUserModal; // Rendi accessibile dall'HTML

// FUNZIONE HELPER: Mostra il Modale di Conferma e Gestisce l'Azione
function showConfirmation(title, text, callback) {
    // Imposta il testo nel modal di conferma
    document.getElementById('confirmationModalTitle').textContent = title;
    document.getElementById('confirmationModalText').textContent = text;

    const confirmBtn = document.getElementById('confirmationModalConfirmBtn');

    // TRUCCO per pulire vecchi listener: cloniamo il bottone
    const newConfirmBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

    // Aggiungiamo il nuovo listener che chiude il modal ed esegue la callback
    newConfirmBtn.onclick = () => {
        confirmationModal.hide();
        callback(); // Esegui la funzione di ban/promozione
    };

    // Mostra il modal di conferma
    confirmationModal.show();
}

// NUOVO: Gestisce l'invio del form di modifica
async function handleUpdateUser(event) {
    event.preventDefault();
    if (!currentEditingUserId) return;

    editUserModal.hideError();

    const dto = {
        username: document.getElementById('edit-username').value,
        email: document.getElementById('edit-email').value,
        profileImageUrl: document.getElementById('edit-image-url').value,
        isAdmin: document.getElementById('edit-is-admin').checked,
    };

    const data = await apiCall(`/api/admin/users/${currentEditingUserId}`, 'PUT', dto);

    if (data && data.status) { // Errore 4xx (Validazione, Email/Username già in uso)
        editUserModal.showError(data.message); // Mantiene l'errore nel modale di modifica
        return;
    }

    if (data && data.message) { // Successo
        editUserModal.hide();
        showInfoModal("Modifica Salvata", data.message, false); // <-- Mostra modal info
        // Ricarica la pagina corrente
        loadAdminData(document.querySelector('.btn-filter.active')?.dataset.filter || 'all', currentPage);
    }
}

// FUNZIONE MODIFICATA: GESTIONE PROMUOVI (Usa il modal personalizzato)
async function handlePromoteUser(userId, username) {
    const title = "Conferma Promozione";
    const text = `Sei sicuro di voler promuovere ${username} ad ADMIN? L'utente otterrà pieni privilegi amministrativi.`;

    showConfirmation(title, text, async () => {
        const data = await apiCall(`/api/admin/users/${userId}/promote`, 'POST');
        if (data && data.status) { // Errore 4xx
            showInfoModal("Promozione Fallita", data.message, true);
        } else if (data && data.message) { // Successo
            showInfoModal("Promozione Riuscita", data.message, false);
            const activeFilter = document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
            loadAdminData(activeFilter, currentPage);
        }
    });
}
window.handlePromoteUser = handlePromoteUser;

function changePage(delta) {
    const newPage = currentPage + delta;
    if (newPage < 0) return;
    currentPage = newPage;
    // Ricarica i dati inviando anche il termine di ricerca
    loadAdminData(document.querySelector('.btn-filter.active')?.dataset.filter || 'all', currentPage);
}

async function loadAdminData(filter = 'all', pageIndex = 0) {
    console.log(`Caricamento dati Admin, filtro: ${filter}, pagina: ${pageIndex}`);
    if (!isAdmin()) return;

    // 1. Prendi il termine di ricerca dall'input
    const searchInput = document.getElementById('user-search-input')?.value.trim();

    let endpoint = '/api/admin/users';
    if (filter === 'players') endpoint = '/api/admin/users/players';
    else if (filter === 'masters') endpoint = '/api/admin/users/masters';

    // 2. Costruisci i parametri URL (Paginazione + Ricerca)
    const urlParams = new URLSearchParams({
        page: pageIndex,
        size: PAGE_SIZE
    });

    if (searchInput) {
        urlParams.append('search', searchInput); // Aggiungi la ricerca se presente
    }

    const urlWithParams = `${endpoint}?${urlParams.toString()}`;

    const responseData = await apiCall(urlWithParams);

    const users = responseData.content || [];
    const totalPages = responseData.totalPages || 1;

    // 3. Rimuovi il filtro client-side (non più necessario)
    // loadedUsers = users; // <-- RIMUOVI

    const tableBody = document.querySelector('#users-table tbody');

    // 4. Renderizza direttamente i risultati (già filtrati dal backend)
    renderUsersTable(users, tableBody, filter);

    // 5. Aggiorna i controlli di paginazione
    updatePaginationControls(totalPages);
}

function updatePaginationControls(totalPages) {
    const prevBtn = document.getElementById('btn-prev-page');
    const nextBtn = document.getElementById('btn-next-page');
    const pageInfo = document.getElementById('page-info');

    // Aggiorna il testo informativo
    pageInfo.textContent = `Pagina ${currentPage + 1} di ${totalPages}`;

    // Abilita/Disabilita bottoni
    if (prevBtn) prevBtn.disabled = currentPage === 0;
    if (nextBtn) nextBtn.disabled = currentPage >= (totalPages - 1);
}

function renderUsersTable(usersToRender, tableBody, filter) {
    if (usersToRender.length > 0) {
        tableBody.innerHTML = usersToRender.map(user => `
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
                     ${!user.admin ?
                `<button class="action-button" style="background: #28a745;" 
                              onclick="handlePromoteUser(${user.id}, '${user.username}')">Promuovi</button>` : ''
            }
                     
                     ${user.banned && !user.admin
                ? `<button class="action-button unban" onclick="handleUnbanUser(${user.id}, '${user.username}')">Sblocca</button>`
                : !user.banned && !user.admin
                    ? `<button class="action-button ban" onclick="handleBanUser(${user.id}, '${user.username}')">Banna</button>`
                    : ''
            }

                     <button class="action-button edit" 
                          onclick="showEditUserModal({
                              id: ${user.id}, 
                              username: '${user.username.replace(/'/g, "\\'")}', 
                              email: '${user.email.replace(/'/g, "\\'")}', 
                              profileImageUrl: '${user.profileImageUrl || ''}', 
                              admin: ${user.admin}
                          })">Modifica</button>
                 </td>
             </tr>
         `).join('');
    } else {
        tableBody.innerHTML = `<tr><td colspan="6">Nessun utente trovato con il filtro '${filter}'.</td></tr>`;
    }
}