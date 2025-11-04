import { apiCall } from './modules/api.js';
import {
  isAuthenticated,
  isAdmin,
  handleLogout,
  getCurrentUserFromStorage,
} from './modules/auth.js';
import { updateGeneralUI, initLogoNavigation } from './modules/ui.js';

// --- GUARDIA DI AUTENTICAZIONE ---
document.addEventListener('DOMContentLoaded', () => {
  console.log('Admin page: Checking authentication...');
  console.log('isAuthenticated():', isAuthenticated());
  console.log('isAdmin():', isAdmin());

  // For admin page: if not admin (whether authenticated or not), show 403
  if (!isAdmin()) {
    console.warn(
      'Accesso non admin a admin.html. Reindirizzamento a error403...'
    );
    window.location.replace('error403.html');
    return;
  }

  console.log('Admin access granted - initializing page...');
  // ---------------------------------

  // Se la guardia passa, inizializza
  updateGeneralUI();
  initLogoNavigation();

  document.querySelectorAll('.btn-filter')?.forEach((btn) => {
    btn.addEventListener('click', (e) =>
      loadAdminData(e.target.dataset.filter)
    );
  });

  document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'logout-button') handleLogout(e);
  });

  loadAdminData();
});

// ... (funzioni handleBanUser e loadAdminData rimangono invariate) ...

async function handleBanUser(userId, username) {
  if (
    confirm(
      `Sei sicuro di voler bannare l'utente ${username} (ID: ${userId})? L'utente sarà sospeso per 1 anno.`
    )
  ) {
    const data = await apiCall(`/api/admin/users/${userId}/ban`, 'POST');
    if (data) {
      const activeFilter =
        document.querySelector('.btn-filter.active')?.dataset.filter || 'all';
      loadAdminData(activeFilter);
    }
  }
}
window.handleBanUser = handleBanUser;

async function loadAdminData(filter = 'all') {
  console.log('Caricamento dati Admin, filtro:', filter);
  if (!isAdmin()) return;
  let endpoint = '/api/admin/users';
  if (filter === 'players') endpoint = '/api/admin/users/players';
  else if (filter === 'masters') endpoint = '/api/admin/users/masters';
  const users = await apiCall(endpoint);
  const tableBody = document.querySelector('#users-table tbody');
  if (users && tableBody) {
    if (users.length > 0) {
      tableBody.innerHTML = users
        .map(
          (user) => `
                 <tr>
                     <td>${user.id}</td>
                     <td>${user.username}</td>
                     <td>${user.email}</td>
                     <td>
                         ${
                           user.admin
                             ? '<span class="role-badge admin">ADMIN</span>'
                             : ''
                         }
                         ${
                           user.player
                             ? '<span class="role-badge player">PLAYER</span>'
                             : ''
                         }
                         ${
                           user.master
                             ? '<span class="role-badge master">MASTER</span>'
                             : ''
                         }
                         ${
                           !user.admin && !user.player && !user.master
                             ? 'Utente Base'
                             : ''
                         }
                     </td>
                     <td>
                         <span class="${
                           user.banned ? 'status-banned' : 'status-active'
                         }">
                             ${user.banned ? 'Bannato' : 'Attivo'}
                         </span>
                     </td>
                     <td>
                         ${
                           !user.banned && !user.admin
                             ? `<button class="action-button ban" onclick="handleBanUser(${user.id}, '${user.username}')">Banna</button>`
                             : user.banned
                             ? '<button class="action-button" disabled>Sblocca (WIP)</button>'
                             : ''
                         }
                          <button class="action-button" disabled>Modifica (WIP)</button>
                     </td>
                 </tr>
             `
        )
        .join('');
    } else {
      tableBody.innerHTML = `<tr><td colspan="6">Nessun utente trovato con il filtro '${filter}'.</td></tr>`;
    }
  } else if (tableBody) {
    tableBody.innerHTML =
      '<tr><td colspan="6">Errore nel caricamento degli utenti.</td></tr>';
  }
  document.querySelectorAll('.btn-filter').forEach((btn) => {
    btn.classList.toggle('active', btn.dataset.filter === filter);
  });
}
