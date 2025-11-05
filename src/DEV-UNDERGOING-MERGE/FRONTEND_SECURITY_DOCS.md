# Spring Security Frontend Validator

Questo sistema simula il comportamento di Spring Security lato frontend per i test durante lo sviluppo, finché il backend non sarà pronto.

## Utenti di Test Disponibili

1. **Admin**

   - Username: `admin`
   - Password: `admin`
   - Ruolo: `ADMIN`
   - Autorità: `['ROLE_ADMIN', 'ROLE_USER']`

2. **Guest 1**

   - Username: `guest1`
   - Password: `password1`
   - Ruolo: `GUEST`
   - Autorità: `['ROLE_GUEST']`

3. **Guest 2**
   - Username: `guest2`
   - Password: `password2`
   - Ruolo: `GUEST`
   - Autorità: `['ROLE_GUEST']`

## Funzionalità Implementate

### Autenticazione

- Login tramite modal Bootstrap
- Persistenza della sessione usando `sessionStorage`
- Logout con pulizia della sessione
- Validazione delle credenziali

### Autorizzazione

- Controllo dei ruoli (`hasRole()`)
- Controllo delle autorità (`hasAuthority()`)
- Visualizzazione condizionale degli elementi HTML

### Compatibilità Spring Security

- Simula gli attributi Thymeleaf `sec:authorize`
- Supporta `sec:authentication="username"`
- API JavaScript compatibile con Spring Security

## Attributi HTML Supportati

### `sec:authorize="isAuthenticated()"`

Mostra l'elemento solo se l'utente è autenticato.

### `sec:authorize="!isAuthenticated()"`

Mostra l'elemento solo se l'utente NON è autenticato.

### `sec:authorize="hasRole('ADMIN')"`

Mostra l'elemento solo se l'utente ha il ruolo ADMIN.

### `sec:authorize="hasRole('GUEST')"`

Mostra l'elemento solo se l'utente ha il ruolo GUEST.

### `sec:authentication="username"`

Elemento che contiene il nome utente (nascosto per compatibilità).

## API JavaScript Disponibili

```javascript
// Verifica se l'utente è autenticato
isAuthenticated();

// Verifica se l'utente ha un ruolo specifico
hasRole('ADMIN');
hasRole('GUEST');

// Verifica se l'utente ha un'autorità specifica
hasAuthority('ROLE_ADMIN');

// Ottiene l'utente corrente
getCurrentUser();

// Effettua il logout
logout();

// Effettua il login (per uso programmatico)
securityLogin(username, password, errorCallback);
```

## File Coinvolti

1. **frontend-development-script.js** - Contiene la logica principale del validator
2. **script.js** - Gestisce l'interfaccia utente e gli eventi
3. **landing.html** - Pagina di test con elementi condizionali
4. **style.css** - Stili per il sistema di autenticazione

## Come Testare

1. Apri la pagina `landing.html`
2. Clicca su "Accedi" per aprire il modal di login
3. Prova a loggarti con uno degli utenti di test
4. Osserva come cambiano gli elementi visibili in base al ruolo
5. Usa il pulsante "Logout" per disconnetterti

## Migrazione al Backend

Quando il backend Spring Security sarà pronto:

1. Rimuovi `frontend-development-script.js`
2. Modifica `script.js` per integrare con gli endpoint del backend
3. Sostituisci i controlli JavaScript con i tag Thymeleaf reali
4. Configura Spring Security per gestire autenticazione e autorizzazione

## Note Tecniche

- La sessione è mantenuta solo per la durata del browser (sessionStorage)
- I controlli di sicurezza sono solo lato frontend (per test)
- Il sistema è progettato per essere facilmente sostituibile con Spring Security reale
- Supporta Bootstrap 5 per l'interfaccia utente
