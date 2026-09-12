# SIW Festival

Sistema informativo su Web per la gestione di **festival cinematografici**
(Sistemi Informativi su Web, a.a. 2025/2026 - progetto assegnato dal docente,
appello di settembre 2026).

Stack: Spring Boot 4.1, JPA/Hibernate, PostgreSQL, Thymeleaf, React (Vite),
Spring Security.

---

## 1. Avvio rapido

```
createdb -U postgres siwfestival
mvnw.cmd spring-boot:run
```

L'applicazione risponde su **http://localhost:8082**.

Se la password del tuo utente `postgres` non e' `postgres`, passala come
variabile d'ambiente senza modificare i file:

```
set DB_PASSWORD=la_tua_password
mvnw.cmd spring-boot:run
```

Al primo avvio Hibernate crea lo schema (`ddl-auto=update`) e `data.sql`
inserisce i dati di esempio. Gli INSERT sono idempotenti, quindi lo script puo'
girare a ogni avvio senza duplicare nulla.

### Utenti precaricati

| username | password | ruolo   |
|----------|----------|---------|
| admin    | admin    | ADMIN   |
| mario    | mario    | DEFAULT |
| luca     | luca     | DEFAULT |

### Frontend React

Il widget di ricerca film e' un progetto Vite separato. Va compilato una volta:

```
cd frontend\react-film
npm install
npm run build
```

La build scrive il bundle in `src/main/resources/static/js/react-film/assets/index.js`,
che la pagina `/film/cerca` carica direttamente. Per lavorare sul widget in
hot-reload: `npm run dev` (Vite su :5173, con proxy di `/api` verso :8082).

---

## 2. Modello di dominio

Sei entita' significative piu' utenti e credenziali.

| Entita'     | Attributi principali                                     |
|-------------|----------------------------------------------------------|
| Festival    | nome, anno, citta, dataInizio, dataFine, descrizione      |
| Film        | titolo, anno, durata, genere, paeseProduzione             |
| Regista     | nome, cognome, dataNascita, nazionalita                   |
| Sala        | nome, indirizzo, capienza                                 |
| Proiezione  | data, ora, stato (SCHEDULED / COMPLETED / CANCELLED)      |
| Recensione  | testo, voto (1-10), data                                  |
| User        | name, surname, email                                      |
| Credentials | username, password (BCrypt), role                         |

### Associazioni

- `Festival` **molti-a-molti** `Film` (tabella `festival_film`) - un film puo'
  partecipare a piu' festival, un festival presenta piu' film.
- `Regista` **uno-a-molti** `Film` - ogni film ha un regista.
- `Festival` uno-a-molti `Proiezione`, `Film` uno-a-molti `Proiezione`,
  `Sala` uno-a-molti `Proiezione`.
- `Film` uno-a-molti `Recensione`, `User` autore delle proprie recensioni.
- Vincolo `uk_recensione_film_autore` su `(film_id, autore_id)`: un utente puo'
  scrivere al massimo una recensione per film.

---

## 3. Architettura a livelli

```
Controller  ->  Service  ->  Repository  ->  DB
(HTTP, validazione)  (casi d'uso, @Transactional)  (JPA)
```

Nessuna logica applicativa nei controller: i controller validano l'input,
chiamano un metodo del Service Layer e preparano il model per la vista.

### Gestione delle transazioni

Il caso d'uso transazionale principale e'
`ProiezioneService.programmaProiezione(...)`, che coinvolge tre entita' e tre
repository:

1. recupero del festival
2. recupero del film
3. recupero della sala
4. verifica che il film partecipi al festival
5. verifica che la data cada nel periodo del festival
6. verifica della disponibilita' della sala nella fascia oraria
7. creazione della proiezione

L'intero metodo e' annotato `@Transactional(isolation = READ_COMMITTED)`: se un
controllo fallisce viene sollevata `ProiezioneNonValidaException` e la
transazione va in rollback, senza lasciare stati parziali. Le letture pure sono
annotate `@Transactional(readOnly = true)`.

Il controllo di sovrapposizione confronta l'intervallo `[ora, ora + durata]`
della nuova proiezione con quelli delle proiezioni gia' presenti nella stessa
sala nello stesso giorno (escluse le annullate): due intervalli si sovrappongono
se `inizioA < fineB && inizioB < fineA`.

---

## 4. Accesso ai dati e strategie di fetch

`Film.regista` e' mappata **LAZY** di proposito. La pagina
**/admin/performance** esegue lo stesso caso d'uso - "carica tutti i film con il
relativo regista" - con tre strategie diverse sugli stessi dati e riporta per
ciascuna il numero di query SQL effettivamente eseguite (statistiche di
Hibernate), il tempo e il numero di oggetti caricati:

1. **Default (LAZY)** - `findAll()` piu' un accesso al regista di ogni film:
   1 query per l'elenco + N per i registi (problema **N+1**).
2. **JOIN FETCH esplicito** - `SELECT f FROM Film f LEFT JOIN FETCH f.regista`:
   una sola query.
3. **@EntityGraph** - `findAllBy()` con `@EntityGraph(attributePaths = "regista")`:
   stesso risultato, ma dichiarativo.

Altre scelte di fetch:

- `FestivalRepository.findByIdWithEntityGraph` carica i film del festival con la
  stessa query che carica il festival (pagina di dettaglio).
- `ProiezioneRepository.findByFestivalIdWithFilmESala` carica film e sala insieme
  alle proiezioni: senza di essa la costruzione del programma farebbe due query
  per ogni riga.
- `RecensioneRepository.findByFilmIdOrderByDataDesc` usa `@EntityGraph("autore")`
  per non fare una query per l'autore di ogni recensione.
- `FilmRepository.cercaPerTitoloConRegista` serve la ricerca React gia' con il
  regista caricato.

---

## 5. API REST

Consumate dal frontend React, restituiscono sempre DTO (mai entita' JPA).

| Metodo | Endpoint                          | Descrizione                    |
|--------|-----------------------------------|--------------------------------|
| GET    | `/api/festivals`                  | elenco festival                |
| GET    | `/api/festivals/{id}`             | dettaglio festival             |
| GET    | `/api/festivals/{id}/movies`      | film partecipanti              |
| GET    | `/api/festivals/{id}/screenings`  | proiezioni del festival        |
| GET    | `/api/movies`                     | elenco film (`?titolo=` filtra) |
| GET    | `/api/movies/{id}`                | dettaglio film                 |
| GET    | `/api/movies/{id}/reviews`        | recensioni del film            |

Le risorse inesistenti rispondono **404**.

---

## 6. Sicurezza

- Autenticazione form-based con `CustomUserDetailsService` e password BCrypt.
- Ruoli **USER** (`DEFAULT`) e **ADMIN**.
- Tutto cio' che sta sotto `/admin/**` richiede il ruolo ADMIN.
- Inserimento, modifica ed eliminazione di recensioni richiedono
  l'autenticazione; il `RecensioneService` verifica che l'utente sia l'autore
  della recensione (l'amministratore puo' cancellare qualsiasi recensione).
- Le API REST in sola lettura sono pubbliche; CORS abilitato solo su `/api/**`
  per il dev server di Vite.

---

## 7. Casi d'uso

**Pubblici:** elenco e dettaglio dei festival, film partecipanti a un festival,
programma delle proiezioni, dettaglio di un film (regista, festival, proiezioni,
recensioni, voto medio), dettaglio del regista, dettaglio della sala.

**Utente registrato:** inserimento, modifica ed eliminazione della propria
recensione (una sola per film).

**Amministratore:** creazione e modifica di festival, film, registi e sale;
associazione e rimozione di un film da un festival; programmazione,
riprogrammazione, cambio di stato ed eliminazione di una proiezione; pagina di
analisi delle prestazioni.

---

## 8. Convenzioni del foglio di stile

Le classi CSS seguono una regola sola: **il nome descrive il ruolo visivo, non
l'entita' del dominio**. Un componente riusato da piu' entita' prende un nome
funzionale; solo un componente legato a una sola entita' prende il nome del
dominio.

| Classe | Ruolo | Dove |
|---|---|---|
| `.card-grid` / `.card` | griglia di schede e singola scheda | elenco festival, elenco film, dashboard |
| `.card-meta`, `.card-links`, `.card-link`, `.card-link-primary`, `.card-icon`, `.card-arrow` | parti interne della scheda | come sopra |
| `.entity-list` / `.entity-item` / `.entity-name` / `.entity-meta` | lista di entita' con azione a destra | film di un festival, festival di un film |
| `.event-list` / `.event-item` / `.event-state` | righe compatte di eventi datati | proiezioni di un film, proiezioni di una sala |
| `.recensione-item` / `-header` / `-actions` / `-form` / `-edit-input` | blocco recensione | scheda del film |
| `.highlight-box` / `.highlight-value` | riquadro in evidenza | voto medio del film, data e ora della proiezione |
| `.status-badge` + `.status-scheduled` / `.status-completed` / `.status-cancelled` | etichetta di stato, un colore per valore dell'enum | ovunque compaia una `Proiezione` |
| `.pagination` / `.pagination-page` / `.pagination-step` / `.pagination-current` / `.pagination-disabled` / `.pagination-info` | barra di navigazione fra le pagine di un elenco | elenco dei film |

I nomi dei badge di stato sono allineati ai valori dell'enum `StatoProiezione`,
cosi' che la classe applicata sia leggibile direttamente dal valore:

```html
<span class="status-badge"
      th:classappend="${riga.stato == 'COMPLETED' ? 'status-completed'
                     : (riga.stato == 'CANCELLED' ? 'status-cancelled' : 'status-scheduled')}"
      th:text="${riga.stato}">Stato</span>
```

I colori non sono mai scritti a mano nei componenti: sono variabili CSS dichiarate
in `:root` all'inizio di `stile.css` (`--bg-primary`, `--gold-400`, `--radius-md`,
e cosi' via). Cambiare la palette del sito significa modificare quel blocco, non
cercare i valori riga per riga.
