import { apiCall } from './modules/api.js';
import { isAuthenticated, isAdmin, handleLogout } from './modules/auth.js';
import { updateGeneralUI } from './modules/ui.js';

let loadedUsers = [];

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

    loadAdminData(); // Primo caricamento
});

// ... (funzioni handleBanUser e loadAdminData rimangono invariate) ...

function filterUsersAndRender(filter) {
    const tableBody = document.querySelector('#users-table tbody');
    const searchInput = document.getElementById('user-search-input')?.value.toLowerCase().trim();

    // 1. Filtra l'array completo degli utenti caricati
    let filteredUsers = loadedUsers;

    if (searchInput) {
        filteredUsers = loadedUsers.filter(user =>
            user.username.toLowerCase().includes(searchInput) ||
            user.email.toLowerCase().includes(searchInput)
        );
    }

    // 2. Renderizza il set filtrato
    renderUsersTable(filteredUsers, tableBody, filter);
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

async function loadAdminData(filter = 'all') {
    console.log("Caricamento dati Admin, filtro:", filter);
    if (!isAdmin()) return;
    let endpoint = '/api/admin/users';
    if (filter === 'players') endpoint = '/api/admin/users/players';
    else if (filter === 'masters') endpoint = '/api/admin/users/masters';

    const users = await apiCall(endpoint);

    loadedUsers = users || [];

    const tableBody = document.querySelector('#users-table tbody');

    if (users && tableBody) {
        renderUsersTable(users, tableBody, filter);
    } else if (tableBody) {
        tableBody.innerHTML = '<tr><td colspan="6">Errore nel caricamento degli utenti.</td></tr>';
    }
    document.querySelectorAll('.btn-filter').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.filter === filter);
    });
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