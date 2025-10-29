// Variabile globale per salvare il nostro "biglietto"
let jwtToken = null;

// Helper per scrivere nel log
function log(message) {
    const logEl = document.getElementById('log');
    // Converte l'oggetto JSON in una stringa formattata
    const formattedMessage = (typeof message === 'object') ? JSON.stringify(message, null, 2) : message;
    logEl.textContent = formattedMessage + '\n\n' + logEl.textContent;
    console.log(message);
}

/**
 * Funzione helper (DRY) per tutte le chiamate API
 * @param {string} endpoint Es. /api/auth/login
 * @param {string} method Es. 'GET', 'POST'
 * @param {object | null} body Oggetto JSON da inviare (opzionale)
 */
async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    
    // FONDAMENTALE: Aggiunge il token JWT a tutte le richieste, tranne login/register
    if (jwtToken && !endpoint.includes('/api/auth/')) {
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
        log(`--- Richiesta ${method} a ${endpoint} ---`);
        const response = await fetch(endpoint, options);
        
        // Cerca di leggere la risposta come JSON
        const data = await response.json();

        if (!response.ok) {
            // Se il server risponde con un errore (es. 403, 404)
            log(`ERRORE ${response.status}: ${data.message || 'Errore sconosciuto'}`);
            console.error(data);
            return null;
        }

        // Se tutto va bene
        log(data);
        return data; // Ritorna i dati per gestirli (es. salvare il token)

    } catch (error) {
        log(`ERRORE DI RETE: ${error.message}`);
        console.error(error);
        return null;
    }
}

// Collega tutti i pulsanti agli eventi
document.addEventListener('DOMContentLoaded', () => {
    
    // --- 1. REGISTRAZIONE ---
    document.getElementById('btn-register').onclick = () => {
        const user = document.getElementById('reg-user').value;
        const email = document.getElementById('reg-email').value;
        const pass = document.getElementById('reg-pass').value;
        apiCall('/api/auth/register', 'POST', {
            username: user,
            email: email,
            password: pass
        });
    };

    // --- 2. LOGIN ---
    document.getElementById('btn-login').onclick = async () => {
        const user = document.getElementById('login-user').value;
        const pass = document.getElementById('login-pass').value;
        
        const data = await apiCall('/api/auth/login', 'POST', {
            username: user,
            password: pass
        });

        // SALVA IL TOKEN
        if (data && data.token) {
            jwtToken = data.token;
            document.getElementById('jwt-token').value = jwtToken;
            log("--- TOKEN SALVATO! Ora puoi usare le API protette. ---");
        }
    };

    // --- 3. PROFILO ---
    document.getElementById('btn-become-player').onclick = () => {
        apiCall('/api/profile/become-player', 'POST');
    };
    document.getElementById('btn-become-master').onclick = () => {
        apiCall('/api/profile/become-master', 'POST');
    };

    // --- 4. MASTER ---
    document.getElementById('btn-create-campaign').onclick = () => {
        const title = document.getElementById('camp-title').value;
        const desc = document.getElementById('camp-desc').value;
        apiCall('/api/master/campaigns', 'POST', {
            title: title,
            description: desc
        });
    };
    document.getElementById('btn-get-master-campaigns').onclick = () => {
        apiCall('/api/master/campaigns', 'GET');
    };

    // --- 5. PLAYER ---
    document.getElementById('btn-create-sheet').onclick = () => {
        const name = document.getElementById('sheet-name').value;
        const pClass = document.getElementById('sheet-class').value;
        const race = document.getElementById('sheet-race').value; // Es. 'HALF_ORC'
        apiCall('/api/player/sheets', 'POST', {
            name: name,
            primaryClass: pClass,
            race: race
        });
    };
    document.getElementById('btn-get-player-sheets').onclick = () => {
        apiCall('/api/player/sheets', 'GET');
    };
    document.getElementById('btn-join-campaign').onclick = () => {
        const code = document.getElementById('join-code').value;
        const sheetId = document.getElementById('join-sheet-id').value;
        apiCall('/api/player/campaigns/join', 'POST', {
            inviteCode: code,
            characterSheetId: Number(sheetId) // Assicura che sia un numero
        });
    };
    document.getElementById('btn-get-joined-campaigns').onclick = () => {
        apiCall('/api/player/campaigns/joined', 'GET');
    };
});