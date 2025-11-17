<p align="center">
  <img src="https://64.media.tumblr.com/39dd1d98385cd4252b73d858d1ad9402/4a3ef35e6c4ab562-da/s1280x1920/16c0e2ec346cb7660dbc2fd3307bc8a1e305792d.gif" alt="Tavern Portal Banner" width="100%" style="border-radius: 12px;">
</p>

<h1 align="center">🎲 Tavern Portal</h1>

<p align="center">
  <em>“Un portale digitale per avventurieri, dungeon master e admin del multiverso.”</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white" alt="Java 17+">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen?logo=springboot&logoColor=white" alt="Spring Boot 3">
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="MIT License">
  <img src="https://img.shields.io/badge/Status-In%20Development-yellow" alt="Project Status">
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
* **Ruoli Dinamici:** Gli utenti iniziano come `ROLE_USER` e possono evolversi in `ROLE_PLAYER`, `ROLE_MASTER` o `ROLE_ADMIN`.
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

### 1️⃣ Clona il Repository

```bash
git clone https://github.com/DelirioCasuale/Bugbusters.git
cd Bugbusters
```
---

### 2️⃣ Configura le Credenziali (JWT + DB)

Crea un file `.env` nella root del progetto ed inserisci:

```env
# File .env (NON COMMITTARE MAI)
DB_USERNAME=ilTuoUsername
DB_PASSWORD=laTuaPassword
JWT_SECRET=laTuaChiaveSegreta
```

**Sconsigliato**: In alternativa, se non puoi usare un file `.env` loader, puoi sostituire i placeholder direttamente in `src/main/resources/application.properties` con i tuoi valori, MA DEVI FARE ATTENZIONE A NON COMMITTARE TALE MODIFICA.

```properties
spring.datasource.username=ilTuoUsername
spring.datasource.password=laTuaPassword
jwt.secret=laTuaChiaveSegreta
```

---

### 3️⃣ Configura il Database MySQL

Importa gli script SQL presenti nel repository:
1.  Assicurati che MySQL sia in esecuzione.
2.  Apri un client (es. *DBeaver* o *MySQL Workbench*).
3.  Esegui lo script:

```sql
-- Crea database e tabelle
source bugbusters_database_script.sql;

-- (Opzionale) Popola con dati generici
source populate_tavern_real_users.sql;

-- (Opzionale) Popola con dati test base
source populate_dnd_db.sql;
```

---

### 4️⃣ Avvia l’Applicazione Backend

Avvia Spring Boot:

```bash
mvn spring-boot:run
```

---

### 5️⃣ Accesso al Frontend

Il frontend è servito automaticamente da Spring Boot (cartella `src/main/resources/static`).

---

Apri il browser e visita:
👉 [`http://localhost:8080/`](http://localhost:8080/)

---

### 🔑 Utenti di Prova

Se hai eseguito lo script di popolamento, troverai utenti demo.
Per test completi, puoi usare l’utente Admin:

| Ruolo | Username | Password | Note |
| :--- | :--- | :--- | :--- |
| Admin | Eladmin | prova1 | Ha tutti i ruoli (presente in entrambi i populate) |
| Player | player1 | playerpass1 | Da `populate_dnd_db.sql` |
| Master | master1 | password1 | Da `populate_dnd_db.sql` |
| Player/Master | usernameGenerici | prova1 | Da `populate_tavern_real_users.sql`, controllare il nome degli username per accedere |

### 📄 Licenza

Il progetto è rilasciato sotto licenza **MIT**. Per maggiori dettagli consulta il file `LICENSE`.

<p align="center"> <em>“May your rolls be ever natural 20s.”</em> 🐉 </p>
