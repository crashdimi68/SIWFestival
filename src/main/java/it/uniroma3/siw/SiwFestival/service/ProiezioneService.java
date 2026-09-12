package it.uniroma3.siw.SiwFestival.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.model.Festival;
import it.uniroma3.siw.SiwFestival.model.Film;
import it.uniroma3.siw.SiwFestival.model.Proiezione;
import it.uniroma3.siw.SiwFestival.model.Sala;
import it.uniroma3.siw.SiwFestival.model.StatoProiezione;
import it.uniroma3.siw.SiwFestival.repository.FestivalRepository;
import it.uniroma3.siw.SiwFestival.repository.FilmRepository;
import it.uniroma3.siw.SiwFestival.repository.ProiezioneRepository;
import it.uniroma3.siw.SiwFestival.repository.SalaRepository;

@Service
public class ProiezioneService {

    private static final Logger logger = LoggerFactory.getLogger(ProiezioneService.class);

    @Autowired
    private ProiezioneRepository proiezioneRepository;
    @Autowired
    private FestivalRepository festivalRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public Optional<Proiezione> findById(Long id) {
        return proiezioneRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findAll() {
        return proiezioneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findByFestivalId(Long festivalId) {
        return proiezioneRepository.findByFestivalIdWithFilmESala(festivalId);
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findByFilmId(Long filmId) {
        return proiezioneRepository.findByFilmId(filmId);
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findBySalaId(Long salaId) {
        return proiezioneRepository.findBySalaId(salaId);
    }

    /**
     * CASO D'USO TRANSAZIONALE PRINCIPALE (Sezione 7 della specifica).
     *
     * La programmazione di una nuova proiezione coinvolge tre entita' e tre
     * repository diversi e deve essere atomica:
     *   1. recupero del festival
     *   2. recupero del film
     *   3. recupero della sala
     *   4. verifica che il film partecipi effettivamente al festival
     *   5. verifica che la data cada nell'intervallo del festival
     *   6. verifica della disponibilita' della sala nella fascia oraria richiesta
     *   7. creazione della proiezione
     *
     * Se uno qualsiasi dei controlli fallisce viene sollevata un'eccezione runtime:
     * la transazione va in rollback e nel database non resta nulla di parziale.
     *
     * Isolamento READ_COMMITTED: il controllo di sovrapposizione legge le proiezioni
     * gia' confermate, non quelle ancora non committate da altre transazioni.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Proiezione programmaProiezione(Long festivalId, Long filmId, Long salaId,
            LocalDate data, LocalTime ora) {

        Festival festival = festivalRepository.findByIdWithEntityGraph(festivalId)
                .orElseThrow(() -> new ProiezioneNonValidaException("Festival non trovato."));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ProiezioneNonValidaException("Film non trovato."));

        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new ProiezioneNonValidaException("Sala non trovata."));

        if (!festival.getFilm().contains(film)) {
            throw new ProiezioneNonValidaException(
                    "Il film \"" + film.getTitolo() + "\" non partecipa al festival "
                            + festival.getNome() + ": associalo prima al festival.");
        }

        if (data == null || ora == null) {
            throw new ProiezioneNonValidaException("Data e ora della proiezione sono obbligatorie.");
        }

        if (data.isBefore(festival.getDataInizio()) || data.isAfter(festival.getDataFine())) {
            throw new ProiezioneNonValidaException(
                    "La data indicata non rientra nel periodo del festival ("
                            + festival.getDataInizio() + " - " + festival.getDataFine() + ").");
        }

        verificaDisponibilitaSala(sala, data, ora, film.getDurata(), null);

        Proiezione proiezione = new Proiezione();
        proiezione.setFestival(festival);
        proiezione.setFilm(film);
        proiezione.setSala(sala);
        proiezione.setData(data);
        proiezione.setOra(ora);
        proiezione.setStato(StatoProiezione.SCHEDULED);

        Proiezione salvata = proiezioneRepository.save(proiezione);
        logger.info("Proiezione programmata: festival={}, film={}, sala={}, {} ore {}",
                festival.getNome(), film.getTitolo(), sala.getNome(), data, ora);
        return salvata;
    }

    /**
     * Verifica che nella sala, in quel giorno, non ci sia gia' una proiezione che si
     * sovrappone all'intervallo [ora, ora + durata). Due proiezioni si sovrappongono
     * se inizioA &lt; fineB e inizioB &lt; fineA.
     *
     * @param proiezioneDaIgnorare id della proiezione in corso di modifica (null in inserimento)
     */
    private void verificaDisponibilitaSala(Sala sala, LocalDate data, LocalTime ora,
            Integer durataMinuti, Long proiezioneDaIgnorare) {

        int durata = durataMinuti != null ? durataMinuti : 120;
        LocalTime fine = ora.plusMinutes(durata);

        List<Proiezione> giaInSala = proiezioneRepository
                .findBySalaIdAndDataAndStatoNot(sala.getId(), data, StatoProiezione.CANCELLED);

        for (Proiezione altra : giaInSala) {
            if (proiezioneDaIgnorare != null && proiezioneDaIgnorare.equals(altra.getId())) {
                continue;
            }
            LocalTime altraInizio = altra.getOra();
            if (altraInizio == null) {
                continue;
            }
            int altraDurata = (altra.getFilm() != null && altra.getFilm().getDurata() != null)
                    ? altra.getFilm().getDurata()
                    : 120;
            LocalTime altraFine = altraInizio.plusMinutes(altraDurata);

            boolean sovrapposte = ora.isBefore(altraFine) && altraInizio.isBefore(fine);
            if (sovrapposte) {
                logger.warn("Sala {} occupata il {} dalle {} alle {}: programmazione rifiutata",
                        sala.getNome(), data, altraInizio, altraFine);
                throw new ProiezioneNonValidaException(
                        "La sala " + sala.getNome() + " e' gia' occupata il " + data
                                + " dalle " + altraInizio + " alle " + altraFine + ".");
            }
        }
    }

    /** Modifica di data, ora e sala di una proiezione, con lo stesso controllo di consistenza. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Proiezione riprogramma(Long proiezioneId, Long salaId, LocalDate data, LocalTime ora) {
        Proiezione proiezione = proiezioneRepository.findById(proiezioneId)
                .orElseThrow(() -> new ProiezioneNonValidaException("Proiezione non trovata."));

        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new ProiezioneNonValidaException("Sala non trovata."));

        Festival festival = proiezione.getFestival();
        if (festival != null
                && (data.isBefore(festival.getDataInizio()) || data.isAfter(festival.getDataFine()))) {
            throw new ProiezioneNonValidaException(
                    "La data indicata non rientra nel periodo del festival ("
                            + festival.getDataInizio() + " - " + festival.getDataFine() + ").");
        }

        Integer durata = proiezione.getFilm() != null ? proiezione.getFilm().getDurata() : null;
        verificaDisponibilitaSala(sala, data, ora, durata, proiezioneId);

        proiezione.setSala(sala);
        proiezione.setData(data);
        proiezione.setOra(ora);
        return proiezioneRepository.save(proiezione);
    }

    @Transactional
    public void cambiaStato(Long proiezioneId, StatoProiezione nuovoStato) {
        Proiezione proiezione = proiezioneRepository.findById(proiezioneId)
                .orElseThrow(() -> new ProiezioneNonValidaException("Proiezione non trovata."));
        proiezione.setStato(nuovoStato);
        proiezioneRepository.save(proiezione);
    }

    @Transactional
    public void delete(Proiezione proiezione) {
        proiezioneRepository.delete(proiezione);
    }
}
