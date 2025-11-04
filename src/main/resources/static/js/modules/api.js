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

            // Caso 1: Errore di Autenticazione (Logout forzato)
            if (response.status === 403 || response.status === 401) {
                // ... (logica 401/403 invariata) ...
                if (endpoint.startsWith('/api/auth/login')) {
                    return { status: response.status, message: "Credenziali non valide." };
                }
                alert(`Sessione scaduta o non autorizzata. Riprovare il login.`);
                clearLoginData();
                window.location.href = 'landing.html';
                return null;
            }

            // Caso 2: Altri Errori (400, 404, ecc.)

            let errorMessage = data.message || 'Errore sconosciuto dal server';

            // --- NUOVA LOGICA: Tenta di estrarre messaggi di validazione (Status 400) ---
            // Spring Boot ha diverse strutture di errore di validazione
            try {
                if (response.status === 400) {
                    if (data.errors && Array.isArray(data.errors) && data.errors.length > 0) {
                        // Struttura 1 (ProblemDetail standard con @Valid)
                        errorMessage = data.errors[0].defaultMessage;
                    } else if (data.fieldErrors && Array.isArray(data.fieldErrors) && data.fieldErrors.length > 0) {
                        // Struttura 2 (Vecchia struttura Spring)
                        errorMessage = data.fieldErrors[0].defaultMessage;
                    }
                    // Se il messaggio è ancora quello generico, usa il 'detail' se esiste (comune con spring-boot-starter-validation)
                    if (errorMessage.startsWith("Validation failed") && data.detail) {
                        errorMessage = data.detail;
                    }
                }
            } catch (e) {
                // Se il parsing dell'errore fallisce, resta l'errorMessage di default
                console.warn("Impossibile analizzare la struttura dell'errore di validazione", e);
            }
            // --- FINE NUOVA LOGICA ---

            return { status: response.status, message: errorMessage };
        }

        console.log(`--- API Response ${response.status} ---`, data);
        return data;

    } catch (error) {
        console.error(`--- Network or Parsing Error ---`, error);
        alert(`Errore di rete o risposta non valida: ${error.message}. Controlla la console.`);
        return null;
    }
}