import { apiCall } from './modules/api.js';
import { isAuthenticated, isAdmin, handleLogout } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

let loadedUsers = [];
const PAGE_SIZE = 50; // Dimensione fissa di 50
let currentPage = 0; // Pagina iniziale (indice 0)

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
            const activeFilter = document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
            filterUsersAndRender(activeFilter);
        });
    }

    if (searchButton) {
        searchButton.disabled = true;
        searchButton.style.opacity = 0.7; // Opzionale: per mostrare che non è cliccabile
    }

    

    document.querySelectorAll('.btn-filter')?.forEach(btn => {
        btn.addEventListener('click', (e) => loadAdminData(e.target.dataset.filter));
    });

    document.addEventListener('click', (e) => {
        if (e.target && e.target.id === 'logout-button') handleLogout(e);
    });

    document.getElementById('btn-prev-page')?.addEventListener('click', () => changePage(-1));
    document.getElementById('btn-next-page')?.addEventListener('click', () => changePage(1));
    
    loadAdminData();// Primo caricamento
});

// ... (funzioni handleBanUser e loadAdminData rimangono invariate) ...

function filterUsersAndRender(filter) {
    // ... (omissis) ...
    // Se stai usando la paginazione, la logica di 'filter' qui è ridondante,
    // perché i filtri 'players'/'masters' sono già gestiti dall'endpoint nel loadAdminData.
    // Qui devi solo applicare la barra di ricerca.
    
    const searchInput = document.getElementById('user-search-input')?.value.toLowerCase().trim();
    
    // Applica solo la ricerca testuale, la paginazione e i filtri di ruolo vengono dal backend
    let usersToRender = loadedUsers;
    
    if (searchInput) {
        usersToRender = loadedUsers.filter(user => 
            user.username.toLowerCase().includes(searchInput) || 
            user.email.toLowerCase().includes(searchInput)
        );
    }
    
    const tableBody = document.querySelector('#users-table tbody');
    renderUsersTable(usersToRender, tableBody, filter);
}

async function handleBanUser(userId, username) {
    if (confirm(`Sei sicuro di voler bannare l'utente ${username} (ID: ${userId})? L'utente sarà sospeso per 1 anno.`)) {
        const data = await apiCall(`/api/admin/users/${userId}/ban`, 'POST');
        if (data) {
            alert(data.message);
            const activeFilter = document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
            // Dopo il ban, ricarichiamo i dati originali e poi filtriamo
            loadAdminData(activeFilter);
        }
    }
}
window.handleBanUser = handleBanUser;

function changePage(delta) {
    // Incrementa o decrementa la pagina
    const newPage = currentPage + delta;
    
    // Non carichiamo se l'indice è negativo
    if (newPage < 0) return;
    
    // Aggiorniamo la pagina corrente
    currentPage = newPage;
    
    // Ricarica i dati dal backend con il nuovo indice di pagina
    loadAdminData(document.querySelector('.btn-filter.active')?.dataset.filter || 'all', currentPage);
}

async function loadAdminData(filter = 'all', pageIndex = 0) {
     console.log(`Caricamento dati Admin, filtro: ${filter}, pagina: ${pageIndex}`);
     if (!isAdmin()) return; 
     let endpoint = '/api/admin/users';
     
     if(filter === 'players') endpoint = '/api/admin/users/players';
     else if (filter === 'masters') endpoint = '/api/admin/users/masters';
     
     // Aggiunge i parametri di paginazione all'URL (il backend DEVE supportarli)
     const urlWithPagination = `${endpoint}?page=${pageIndex}&size=${PAGE_SIZE}`;
     
     const responseData = await apiCall(urlWithPagination); // responseData è ora l'oggetto di paginazione Spring (es. Page<AdminUserViewDTO>)
     
     // Assumendo che il backend restituisca un oggetto 'Page' di Spring Data
     const users = responseData.content || []; 
     const totalPages = responseData.totalPages || 1;
     
     // Salva l'elenco completo degli utenti per la ricerca lato client (solo la pagina corrente)
     loadedUsers = users; 
     
     const tableBody = document.querySelector('#users-table tbody');
     
     // 1. Applica filtro di ricerca lato client sulla pagina corrente
     filterUsersAndRender(filter); 
     
     // 2. Aggiorna i controlli di paginazione
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
                     ${!user.banned && !user.admin
                ? `<button class="action-button ban" onclick="handleBanUser(${user.id}, '${user.username}')">Banna</button>`
                : user.banned ? '<button class="action-button" disabled>Sblocca (WIP)</button>' : ''
            }
                      <button class="action-button" disabled>Modifica (WIP)</button>
                 </td>
             </tr>
         `).join('');
    } else {
        tableBody.innerHTML = `<tr><td colspan="6">Nessun utente trovato con il filtro '${filter}'.</td></tr>`;
    }
}