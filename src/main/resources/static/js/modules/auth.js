/**
 * Gestione dello stato di autenticazione
 */

export function saveLoginData(token, userDetails) {
    sessionStorage.setItem('jwtToken', token);
    sessionStorage.setItem('currentUser', JSON.stringify({
        id: userDetails.id,
        username: userDetails.username,
        email: userDetails.email,
        roles: userDetails.roles || [],
        profileImageUrl: userDetails.profileImageUrl
    }));
}

export function clearLoginData() {
    sessionStorage.removeItem('jwtToken');
    sessionStorage.removeItem('currentUser');
}

export function isAuthenticated() {
    return sessionStorage.getItem('jwtToken') !== null && sessionStorage.getItem('currentUser') !== null;
}

export function getCurrentUserFromStorage() {
    const user = sessionStorage.getItem('currentUser');
    try { 
        return user ? JSON.parse(user) : null; 
    } catch (e) { 
        console.error("Errore parsing currentUser", e); 
        clearLoginData(); 
        return null; 
    }
}

export function hasRole(role) {
    const user = getCurrentUserFromStorage();
    if (!user || !user.roles) return false;
    const roleName = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    return user.roles.includes(roleName);
}

export function isPlayer() { return hasRole('PLAYER'); }
export function isMaster() { return hasRole('MASTER'); }
export function isAdmin() { return hasRole('ADMIN'); }

// HandleLogout è qui perché è legato all'autenticazione
export function handleLogout(event) {
    if(event) event.preventDefault();
    clearLoginData(); 
    window.location.href = 'landing.html';
}