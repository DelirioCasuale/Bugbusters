import { clearLoginData } from './auth.js';

/**
 * Funzione helper (DRY) per tutte le chiamate API
 * Esportata per essere usata in altri moduli.
 */
export async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');

    const currentToken = sessionStorage.getItem('jwtToken');
    if (currentToken && !endpoint.startsWith('/api/auth/')) {
        headers.append('Authorization', 'Bearer ' + currentToken);
    }

    const options = { method: method, headers: headers };
    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        console.log(`--- API Call: ${method} ${endpoint} ---`, body ? body : '');
        const response = await fetch(endpoint, options);

        if (response.status === 204) {
            console.log(`--- API Response ${response.status} (No Content) ---`);
            return {};
        }

        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const textResponse = await response.text();
            console.error(`--- API Error ${response.status} (Not JSON) ---`, textResponse || '<empty response>');
            alert(`Errore ${response.status}: Risposta non valida dal server. Controlla la console.`);
            if (response.status === 403 || response.status === 401) {
                clearLoginData();
                window.location.href = 'landing.html';
            }
            return null;
        }

        const data = await response.json();

        if (!response.ok) {
            console.error(`--- API Error ${response.status} ---`, data);

            // Caso 1: Errore di Autenticazione (401/403)
            if (response.status === 403 || response.status === 401) {

                // MODIFICA: Non fare alert/redirect se stiamo già tentando il login
                if (endpoint.startsWith('/api/auth/login')) {
                    // Restituisci l'errore al form di login per la gestione personalizzata
                    return { status: response.status, message: data.message || "Credenziali non valide." };
                }

                // Altrimenti (se l'errore 401/403 avviene altrove), fai il logout forzato
                alert(`Sessione scaduta o non autorizzata. Riprovare il login.`);
                clearLoginData();
                window.location.href = 'landing.html';
                return null;
            }

            let errorMessage = data.message || 'Errore sconosciuto dal server';

            // NUOVA LOGICA: Tenta di estrarre messaggi di validazione specifici (Status 400)
            if (response.status === 400 && data.errors && data.errors.length > 0) {
                // Estrae il primo messaggio di errore di validazione
                errorMessage = data.errors[0].defaultMessage || errorMessage;
            }

            // Caso 2: Errore Client 400/404/ecc. (Passa il messaggio di errore al chiamante)
            return { status: response.status, message: data.message || 'Errore sconosciuto dal server' };
        }

        console.log(`--- API Response ${response.status} ---`, data);
        return data;

    } catch (error) {
        console.error(`--- Network or Parsing Error ---`, error);
        alert(`Errore di rete o risposta non valida: ${error.message}. Controlla la console.`);
        return null;
    }
}