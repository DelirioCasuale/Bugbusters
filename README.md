<p align="center">
  <img src="https://images.unsplash.com/photo-1603570417039-03a2c0f4c9d2?auto=format&fit=crop&w=1600&q=80" alt="Tavern Portal Banner" width="100%" style="border-radius: 12px;">
</p>

<h1 align="center">🎲 Tavern Portal</h1>

<p align="center">
  <em>“Un portale digitale per avventurieri, dungeon master e admin del multiverso.”</em>
</p>

<p align="center">
  <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html"><img src="https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white" alt="Java 17+"></a>
  <a href="#"><img src="https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen?logo=springboot&logoColor=white" alt="Spring Boot 3"></a>
  <a href="#"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="MIT License"></a>
  <a href="#"><img src="https://img.shields.io/badge/Status-In%20Development-yellow" alt="Project Status"></a>
</p>

---

> **Tavern Portal** è un'applicazione web full-stack progettata per digitalizzare e semplificare la gestione di campagne di giochi di ruolo (**TTRPG**), con un focus su **Dungeons & Dragons 5e**.
> La piattaforma connette tre tipi di utenti (**Admin**, **Master** e **Player**) offrendo strumenti dedicati per ogni ruolo — dalla creazione della scheda personaggio alla gestione amministrativa delle campagne.

---

## 🚀 Caratteristiche Principali

Il portale è basato su un **sistema di ruoli dinamico**, che offre funzionalità specifiche per ciascun utente.

---

### 🧑‍💼 Admin (Amministratore)

* **Dashboard Utenti:** Visione completa di tutti gli utenti con paginazione, filtri (Player/Master) e ricerca in tempo reale.
* **Gestione Ban:** Possibilità di sospendere (1 anno dopo il quale eliminazione automatica) o sbloccare utenti.
* **Logica di Orfanezza:** Il ban di un Master avvia un timer di 30 giorni per le sue campagne, rendendole “orfane” e reclamabili.
* **Gestione Ruoli:** Modifica di username, email, immagine e promozione di utenti a `ROLE_ADMIN`.
* **Sicurezza:** Accesso esclusivo agli endpoint `/api/admin/**`.

---

### 🧙 Master (Dungeon Master)

* **Dashboard Master:** Creazione, modifica ed eliminazione di campagne.
* **Codici Invito:** Generazione di codici unici (`invitePlayersCode` e `inviteMastersCode`) per ogni campagna.
* **Gestione Giocatori:** Visualizzazione e rimozione dei giocatori dalla campagna.
* **Gestione Sessioni:** Proposta di nuove date di sessione (timer 48h) e visualizzazione dei voti.
* **Campagne Orfane:** Possibilità di reclamare una campagna orfana tramite `inviteMastersCode`.
* **Stato Campagna:** Opzione per segnare una campagna come *Finita*.

---

### 🛡️ Player (Giocatore)

* **Dashboard Giocatore:** Creazione, modifica ed eliminazione di schede personaggio.
* **Regole D&D 5e:** Applicazione automatica delle regole base (Punti Ferita, Tiri Salvezza, Competenze) per 13 classi.
* **Unione Campagne:** Unirsi a una campagna tramite `invitePlayersCode` e selezione della scheda.
* **Voto Sessioni:** Partecipazione alle votazioni per le sessioni proposte.
* **Gestione Orfana:** Visualizzazione delle campagne orfane e codice per invitare un nuovo Master.
* **Esportazione:** Download della scheda personaggio in **PDF**.

---

## 🔐 Autenticazione e Sistema

* **JWT Security:** Autenticazione stateless basata su **JSON Web Token (JWT)**.
* **Ruoli Dinamici:** Gli utenti iniziano come `ROLE_USER` e possono evolversi in `ROLE_PLAYER` o `ROLE_MASTER`.
* **Scheduler:** Task automatici (`@Scheduled`) per:
    * Pulizia utenti bannati e campagne orfane scadute.
    * Conferma automatica delle sessioni dopo il timer.

---

## 🛠️ Tecnologie Utilizzate

### 🧩 Backend

* **Java 17+**
* **Spring Boot 3**
    * `Spring Web`: Endpoint RESTful (`@RestController`)
    * `Spring Data JPA`: ORM con Hibernate
    * `Spring Security 6`: Autenticazione e autorizzazione
* **MySQL** come database relazionale
* **JWT** (libreria `io.jsonwebtoken.jjwt`)
* **Jakarta Validation** (`@Valid`, `@Size`, ecc.)
* **Lombok** per ridurre il boilerplate

### 🎨 Frontend

* **HTML5**, **CSS3**, **JavaScript (ES6 Modules)**
    * Layout moderni (**Flexbox**, **Grid**)
    * **CSS Variables** per theming coerente (Dark Mode)
    * **Responsive Design** tramite `@media queries`
    * **Fetch API** (`async/await`) per le chiamate REST
    * Gestione stato tramite `sessionStorage` (JWT e dettagli utente)
    * **Page Guards** per sicurezza lato client

### 🧰 Librerie Esterne

* **Font Awesome** → Icone UI
* **html2pdf.js** → Esportazione schede personaggio in PDF

---

## ⚙️ Come Iniziare

### 📋 Prerequisiti

* **JDK 17+**
* **Apache Maven**
* **MySQL Server** (o compatibile)

---

### 1️⃣ Configurazione del Database

1.  Assicurati che MySQL sia in esecuzione.
2.  Apri un client (es. *DBeaver* o *MySQL Workbench*).
3.  Esegui lo script:

    ```sql
    -- Crea il database e le tabelle necessarie
    source bugbusters_database_script.sql;

    -- (Opzionale) Popola il DB con dati di test:
    source populate_dnd_db.sql;
    -- Oppure:
    source populate_tavern_real_users.sql;
    
    ```

### 2️⃣ Avvio del Backend (Spring Boot)

```bash
# Clona il repository
git clone https://github.com/DelirioCasuale/Bugbusters.git

# Entra nella directory del progetto
cd Bugbusters

# Aggiorna application.properties con le tue credenziali MySQL (se necessario)

# Avvia l’applicazione
mvn spring-boot:run

# Il server sarà disponibile su 👉 http://localhost:8080
```

### 3️⃣ Accesso al Frontend

Il frontend è servito staticamente da Spring Boot (cartella `src/main/resources/static`).

Apri il browser e visita:
👉 [`http://localhost:8080/`](http://localhost:8080/)

### 🔑 Utenti di Prova

Se hai eseguito lo script di popolamento, troverai utenti demo.
Per test completi, puoi usare l’utente Admin:

| Ruolo | Username | Password | Note |
| :--- | :--- | :--- | :--- |
| Admin | Eladmin | prova1 | Include tutti i ruoli: ADMIN, USER, PLAYER, MASTER (in entrambi i populate) |
| Player | player1 | playerpass1 | Se si avvia il populate_dnd_db.sql |
| Master | master1 | password1 | Se si avvia il populate_dnd_db.sql |
| Player/Master | usernameGenerici | prova1 | Se si avvia il populate_tavern_real_users.sql controllare il nome degli username per accedere |

### 📄 Licenza

Questo progetto è distribuito sotto la Licenza MIT. Consulta il file LICENSE per i dettagli.

<p align="center"> <em>“May your rolls be ever natural 20s.”</em> 🐉 </p>
