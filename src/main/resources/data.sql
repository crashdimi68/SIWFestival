-- =====================================================================
-- Dati di esempio per SIW Festival.
--
-- Gli INSERT sono idempotenti (WHERE NOT EXISTS): lo script gira a ogni
-- avvio (spring.sql.init.mode=always) senza duplicare nulla.
-- Le password sono cifrate con BCrypt: admin/admin, mario/mario, luca/luca.
-- =====================================================================

-- ---------------------------------------------------------------------
-- UTENTI E CREDENZIALI
-- ---------------------------------------------------------------------
INSERT INTO users (id, name, surname, email)
SELECT nextval('users_seq'), 'Admin', 'Festival', 'admin@siw.it'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@siw.it');

INSERT INTO users (id, name, surname, email)
SELECT nextval('users_seq'), 'Mario', 'Rossi', 'mario@siw.it'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'mario@siw.it');

INSERT INTO users (id, name, surname, email)
SELECT nextval('users_seq'), 'Luca', 'Bianchi', 'luca@siw.it'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'luca@siw.it');

INSERT INTO credentials (id, username, password, role, user_id)
SELECT nextval('credentials_seq'), 'admin',
       '$2b$10$XcM5.tpht48Fwx3DkHPZe.3CohIc/tyB0Jz/ixjNwCL.dnBmK0YM.', 'ADMIN', id
FROM users WHERE email = 'admin@siw.it'
AND NOT EXISTS (SELECT 1 FROM credentials WHERE username = 'admin');

INSERT INTO credentials (id, username, password, role, user_id)
SELECT nextval('credentials_seq'), 'mario',
       '$2b$10$rz3Jm1W/XEG8mx9Wh3ATsO.aWlw1XRNSK1YedeKtTs9XI9Bp/TxH.', 'DEFAULT', id
FROM users WHERE email = 'mario@siw.it'
AND NOT EXISTS (SELECT 1 FROM credentials WHERE username = 'mario');

INSERT INTO credentials (id, username, password, role, user_id)
SELECT nextval('credentials_seq'), 'luca',
       '$2b$10$BdApzqbnw6Lf1f0TEIchrO5w2bzpm2apvav27QlXDM0MDZDoqDu0C', 'DEFAULT', id
FROM users WHERE email = 'luca@siw.it'
AND NOT EXISTS (SELECT 1 FROM credentials WHERE username = 'luca');

-- ---------------------------------------------------------------------
-- REGISTI
-- ---------------------------------------------------------------------
INSERT INTO regista (id, nome, cognome, data_nascita, nazionalita)
SELECT nextval('regista_seq'), 'Paolo', 'Sorrentino', DATE '1970-05-31', 'Italiana'
WHERE NOT EXISTS (SELECT 1 FROM regista WHERE nome = 'Paolo' AND cognome = 'Sorrentino');

INSERT INTO regista (id, nome, cognome, data_nascita, nazionalita)
SELECT nextval('regista_seq'), 'Alice', 'Rohrwacher', DATE '1982-01-01', 'Italiana'
WHERE NOT EXISTS (SELECT 1 FROM regista WHERE nome = 'Alice' AND cognome = 'Rohrwacher');

INSERT INTO regista (id, nome, cognome, data_nascita, nazionalita)
SELECT nextval('regista_seq'), 'Denis', 'Villeneuve', DATE '1967-10-03', 'Canadese'
WHERE NOT EXISTS (SELECT 1 FROM regista WHERE nome = 'Denis' AND cognome = 'Villeneuve');

INSERT INTO regista (id, nome, cognome, data_nascita, nazionalita)
SELECT nextval('regista_seq'), 'Greta', 'Gerwig', DATE '1983-08-04', 'Statunitense'
WHERE NOT EXISTS (SELECT 1 FROM regista WHERE nome = 'Greta' AND cognome = 'Gerwig');

INSERT INTO regista (id, nome, cognome, data_nascita, nazionalita)
SELECT nextval('regista_seq'), 'Hayao', 'Miyazaki', DATE '1941-01-05', 'Giapponese'
WHERE NOT EXISTS (SELECT 1 FROM regista WHERE nome = 'Hayao' AND cognome = 'Miyazaki');

-- ---------------------------------------------------------------------
-- SALE
-- ---------------------------------------------------------------------
INSERT INTO sala (id, nome, indirizzo, capienza)
SELECT nextval('sala_seq'), 'Sala Fellini', 'Via Tuscolana 1055, Roma', 320
WHERE NOT EXISTS (SELECT 1 FROM sala WHERE nome = 'Sala Fellini');

INSERT INTO sala (id, nome, indirizzo, capienza)
SELECT nextval('sala_seq'), 'Sala Visconti', 'Via Ostiense 106, Roma', 180
WHERE NOT EXISTS (SELECT 1 FROM sala WHERE nome = 'Sala Visconti');

INSERT INTO sala (id, nome, indirizzo, capienza)
SELECT nextval('sala_seq'), 'Sala Antonioni', 'Piazza Verdi 8, Bologna', 140
WHERE NOT EXISTS (SELECT 1 FROM sala WHERE nome = 'Sala Antonioni');

INSERT INTO sala (id, nome, indirizzo, capienza)
SELECT nextval('sala_seq'), 'Arena Estiva', 'Parco della Musica, Torino', 500
WHERE NOT EXISTS (SELECT 1 FROM sala WHERE nome = 'Arena Estiva');

-- ---------------------------------------------------------------------
-- FILM
-- ---------------------------------------------------------------------
INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'La Grande Bellezza', 2013, 141, 'DRAMMATICO', 'Italia', r.id
FROM regista r WHERE r.nome = 'Paolo' AND r.cognome = 'Sorrentino'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'La Grande Bellezza' AND anno = 2013);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'E stata la mano di Dio', 2021, 130, 'DRAMMATICO', 'Italia', r.id
FROM regista r WHERE r.nome = 'Paolo' AND r.cognome = 'Sorrentino'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'E stata la mano di Dio' AND anno = 2021);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'Le Meraviglie', 2014, 111, 'DRAMMATICO', 'Italia', r.id
FROM regista r WHERE r.nome = 'Alice' AND r.cognome = 'Rohrwacher'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'Le Meraviglie' AND anno = 2014);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'La Chimera', 2023, 130, 'DRAMMATICO', 'Italia', r.id
FROM regista r WHERE r.nome = 'Alice' AND r.cognome = 'Rohrwacher'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'La Chimera' AND anno = 2023);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'Arrival', 2016, 116, 'FANTASCIENZA', 'Stati Uniti', r.id
FROM regista r WHERE r.nome = 'Denis' AND r.cognome = 'Villeneuve'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'Arrival' AND anno = 2016);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'Dune', 2021, 155, 'FANTASCIENZA', 'Stati Uniti', r.id
FROM regista r WHERE r.nome = 'Denis' AND r.cognome = 'Villeneuve'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'Dune' AND anno = 2021);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'Lady Bird', 2017, 94, 'COMMEDIA', 'Stati Uniti', r.id
FROM regista r WHERE r.nome = 'Greta' AND r.cognome = 'Gerwig'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'Lady Bird' AND anno = 2017);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'Piccole Donne', 2019, 135, 'DRAMMATICO', 'Stati Uniti', r.id
FROM regista r WHERE r.nome = 'Greta' AND r.cognome = 'Gerwig'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'Piccole Donne' AND anno = 2019);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'La Citta Incantata', 2001, 125, 'ANIMAZIONE', 'Giappone', r.id
FROM regista r WHERE r.nome = 'Hayao' AND r.cognome = 'Miyazaki'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'La Citta Incantata' AND anno = 2001);

INSERT INTO film (id, titolo, anno, durata, genere, paese_produzione, regista_id)
SELECT nextval('film_seq'), 'Il Ragazzo e lAirone', 2023, 124, 'ANIMAZIONE', 'Giappone', r.id
FROM regista r WHERE r.nome = 'Hayao' AND r.cognome = 'Miyazaki'
AND NOT EXISTS (SELECT 1 FROM film WHERE titolo = 'Il Ragazzo e lAirone' AND anno = 2023);

-- ---------------------------------------------------------------------
-- FESTIVAL
-- ---------------------------------------------------------------------
INSERT INTO festival (id, nome, anno, citta, data_inizio, data_fine, descrizione)
SELECT nextval('festival_seq'), 'Roma Film Fest', 2026, 'Roma',
       DATE '2026-10-15', DATE '2026-10-25',
       'Rassegna internazionale dedicata al cinema d autore, con proiezioni in tre sale storiche della citta.'
WHERE NOT EXISTS (SELECT 1 FROM festival WHERE nome = 'Roma Film Fest' AND anno = 2026);

INSERT INTO festival (id, nome, anno, citta, data_inizio, data_fine, descrizione)
SELECT nextval('festival_seq'), 'Bologna Cinema Ritrovato', 2026, 'Bologna',
       DATE '2026-06-20', DATE '2026-06-28',
       'Festival dedicato ai restauri e alle riscoperte, con una sezione competitiva per il cinema di animazione.'
WHERE NOT EXISTS (SELECT 1 FROM festival WHERE nome = 'Bologna Cinema Ritrovato' AND anno = 2026);

INSERT INTO festival (id, nome, anno, citta, data_inizio, data_fine, descrizione)
SELECT nextval('festival_seq'), 'Torino Sci-Fi Days', 2026, 'Torino',
       DATE '2026-07-03', DATE '2026-07-08',
       'Cinque giorni di fantascienza sotto le stelle, con proiezioni serali all Arena Estiva.'
WHERE NOT EXISTS (SELECT 1 FROM festival WHERE nome = 'Torino Sci-Fi Days' AND anno = 2026);

-- ---------------------------------------------------------------------
-- ASSOCIAZIONE FILM - FESTIVAL (molti-a-molti)
-- ---------------------------------------------------------------------
INSERT INTO festival_film (festival_id, film_id)
SELECT f.id, m.id
FROM festival f, film m
WHERE f.nome = 'Roma Film Fest' AND f.anno = 2026
  AND m.titolo IN ('La Grande Bellezza', 'E stata la mano di Dio', 'La Chimera', 'Piccole Donne')
  AND NOT EXISTS (SELECT 1 FROM festival_film ff WHERE ff.festival_id = f.id AND ff.film_id = m.id);

INSERT INTO festival_film (festival_id, film_id)
SELECT f.id, m.id
FROM festival f, film m
WHERE f.nome = 'Bologna Cinema Ritrovato' AND f.anno = 2026
  AND m.titolo IN ('La Citta Incantata', 'Il Ragazzo e lAirone', 'Le Meraviglie', 'Lady Bird')
  AND NOT EXISTS (SELECT 1 FROM festival_film ff WHERE ff.festival_id = f.id AND ff.film_id = m.id);

INSERT INTO festival_film (festival_id, film_id)
SELECT f.id, m.id
FROM festival f, film m
WHERE f.nome = 'Torino Sci-Fi Days' AND f.anno = 2026
  AND m.titolo IN ('Arrival', 'Dune', 'Il Ragazzo e lAirone')
  AND NOT EXISTS (SELECT 1 FROM festival_film ff WHERE ff.festival_id = f.id AND ff.film_id = m.id);

-- ---------------------------------------------------------------------
-- PROIEZIONI
-- Nessuna sovrapposizione nella stessa sala: gli orari tengono conto della
-- durata dei film, esattamente come fa il controllo in ProiezioneService.
-- ---------------------------------------------------------------------

-- Roma Film Fest 2026
INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-10-16', TIME '18:00', 'SCHEDULED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Roma Film Fest' AND m.titolo = 'La Grande Bellezza' AND s.nome = 'Sala Fellini'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-10-16' AND p.ora = TIME '18:00');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-10-16', TIME '21:00', 'SCHEDULED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Roma Film Fest' AND m.titolo = 'E stata la mano di Dio' AND s.nome = 'Sala Fellini'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-10-16' AND p.ora = TIME '21:00');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-10-17', TIME '17:30', 'SCHEDULED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Roma Film Fest' AND m.titolo = 'La Chimera' AND s.nome = 'Sala Visconti'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-10-17' AND p.ora = TIME '17:30');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-10-17', TIME '20:30', 'SCHEDULED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Roma Film Fest' AND m.titolo = 'Piccole Donne' AND s.nome = 'Sala Visconti'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-10-17' AND p.ora = TIME '20:30');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-10-18', TIME '19:00', 'CANCELLED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Roma Film Fest' AND m.titolo = 'La Chimera' AND s.nome = 'Sala Fellini'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-10-18' AND p.ora = TIME '19:00');

-- Bologna Cinema Ritrovato 2026
INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-06-21', TIME '16:00', 'COMPLETED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Bologna Cinema Ritrovato' AND m.titolo = 'La Citta Incantata' AND s.nome = 'Sala Antonioni'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-06-21' AND p.ora = TIME '16:00');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-06-21', TIME '19:00', 'COMPLETED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Bologna Cinema Ritrovato' AND m.titolo = 'Il Ragazzo e lAirone' AND s.nome = 'Sala Antonioni'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-06-21' AND p.ora = TIME '19:00');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-06-22', TIME '17:00', 'COMPLETED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Bologna Cinema Ritrovato' AND m.titolo = 'Le Meraviglie' AND s.nome = 'Sala Antonioni'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-06-22' AND p.ora = TIME '17:00');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-06-22', TIME '20:00', 'COMPLETED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Bologna Cinema Ritrovato' AND m.titolo = 'Lady Bird' AND s.nome = 'Sala Antonioni'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-06-22' AND p.ora = TIME '20:00');

-- Torino Sci-Fi Days 2026
INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-07-04', TIME '21:00', 'COMPLETED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Torino Sci-Fi Days' AND m.titolo = 'Arrival' AND s.nome = 'Arena Estiva'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-07-04' AND p.ora = TIME '21:00');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-07-05', TIME '21:00', 'COMPLETED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Torino Sci-Fi Days' AND m.titolo = 'Dune' AND s.nome = 'Arena Estiva'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-07-05' AND p.ora = TIME '21:00');

INSERT INTO proiezione (id, data, ora, stato, festival_id, film_id, sala_id)
SELECT nextval('proiezione_seq'), DATE '2026-07-06', TIME '21:00', 'COMPLETED', f.id, m.id, s.id
FROM festival f, film m, sala s
WHERE f.nome = 'Torino Sci-Fi Days' AND m.titolo = 'Il Ragazzo e lAirone' AND s.nome = 'Arena Estiva'
AND NOT EXISTS (SELECT 1 FROM proiezione p WHERE p.festival_id = f.id AND p.film_id = m.id
                AND p.data = DATE '2026-07-06' AND p.ora = TIME '21:00');

-- ---------------------------------------------------------------------
-- RECENSIONI
-- Al massimo una recensione per (utente, film): il vincolo di unicita'
-- uk_recensione_film_autore lo garantisce anche a livello di database.
-- ---------------------------------------------------------------------
INSERT INTO recensione (id, testo, voto, data, autore_id, film_id)
SELECT nextval('recensione_seq'),
       'Un affresco malinconico e barocco su Roma. La fotografia da sola vale il biglietto.',
       9, TIMESTAMP '2026-07-01 10:15:00', u.id, m.id
FROM users u, film m
WHERE u.email = 'mario@siw.it' AND m.titolo = 'La Grande Bellezza'
AND NOT EXISTS (SELECT 1 FROM recensione r WHERE r.autore_id = u.id AND r.film_id = m.id);

INSERT INTO recensione (id, testo, voto, data, autore_id, film_id)
SELECT nextval('recensione_seq'),
       'Bellissimo da guardare ma un po troppo lungo nella parte centrale.',
       7, TIMESTAMP '2026-07-02 18:40:00', u.id, m.id
FROM users u, film m
WHERE u.email = 'luca@siw.it' AND m.titolo = 'La Grande Bellezza'
AND NOT EXISTS (SELECT 1 FROM recensione r WHERE r.autore_id = u.id AND r.film_id = m.id);

INSERT INTO recensione (id, testo, voto, data, autore_id, film_id)
SELECT nextval('recensione_seq'),
       'Fantascienza che sceglie il linguaggio invece delle esplosioni. Finale memorabile.',
       10, TIMESTAMP '2026-07-05 23:30:00', u.id, m.id
FROM users u, film m
WHERE u.email = 'mario@siw.it' AND m.titolo = 'Arrival'
AND NOT EXISTS (SELECT 1 FROM recensione r WHERE r.autore_id = u.id AND r.film_id = m.id);

INSERT INTO recensione (id, testo, voto, data, autore_id, film_id)
SELECT nextval('recensione_seq'),
       'Visivamente imponente, ma e chiaramente meta di una storia.',
       8, TIMESTAMP '2026-07-06 09:05:00', u.id, m.id
FROM users u, film m
WHERE u.email = 'luca@siw.it' AND m.titolo = 'Dune'
AND NOT EXISTS (SELECT 1 FROM recensione r WHERE r.autore_id = u.id AND r.film_id = m.id);

INSERT INTO recensione (id, testo, voto, data, autore_id, film_id)
SELECT nextval('recensione_seq'),
       'Un classico che regge benissimo alla riproiezione in sala. Da vedere almeno una volta al cinema.',
       10, TIMESTAMP '2026-06-22 08:00:00', u.id, m.id
FROM users u, film m
WHERE u.email = 'mario@siw.it' AND m.titolo = 'La Citta Incantata'
AND NOT EXISTS (SELECT 1 FROM recensione r WHERE r.autore_id = u.id AND r.film_id = m.id);

INSERT INTO recensione (id, testo, voto, data, autore_id, film_id)
SELECT nextval('recensione_seq'),
       'Commedia asciutta e sincera, con una protagonista scritta molto bene.',
       8, TIMESTAMP '2026-06-23 21:10:00', u.id, m.id
FROM users u, film m
WHERE u.email = 'luca@siw.it' AND m.titolo = 'Lady Bird'
AND NOT EXISTS (SELECT 1 FROM recensione r WHERE r.autore_id = u.id AND r.film_id = m.id);
