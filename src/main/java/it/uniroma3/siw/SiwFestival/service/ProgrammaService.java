package it.uniroma3.siw.SiwFestival.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.SiwFestival.dto.RigaProgrammaDTO;
import it.uniroma3.siw.SiwFestival.model.Festival;
import it.uniroma3.siw.SiwFestival.model.Proiezione;
import it.uniroma3.siw.SiwFestival.repository.FestivalRepository;
import it.uniroma3.siw.SiwFestival.repository.ProiezioneRepository;

/**
 * Costruisce il programma di un festival: tutte le proiezioni in ordine cronologico,
 * con orario di fine calcolato sulla durata del film e marcatura della prima
 * proiezione di ogni giornata (serve alla vista per raggruppare per data).
 *
 * Il caricamento usa un'unica query con fetch di film e sala: senza di essa la
 * costruzione delle righe scatenerebbe due SELECT aggiuntive per ogni proiezione
 * (problema N+1).
 */
@Service
public class ProgrammaService {

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private ProiezioneRepository proiezioneRepository;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<RigaProgrammaDTO> programmaPerFestival(Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId).orElse(null);
        if (festival == null) {
            return List.of();
        }

        List<Proiezione> proiezioni = proiezioneRepository.findByFestivalIdWithFilmESala(festivalId);

        List<RigaProgrammaDTO> programma = new ArrayList<>();
        for (Proiezione p : proiezioni) {
            programma.add(RigaProgrammaDTO.forProiezione(p));
        }

        Comparator<RigaProgrammaDTO> perData =
                Comparator.comparing(RigaProgrammaDTO::getData, Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<RigaProgrammaDTO> perOra =
                Comparator.comparing(RigaProgrammaDTO::getOra, Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<RigaProgrammaDTO> perSala =
                Comparator.comparing(RigaProgrammaDTO::getSalaNome, Comparator.nullsLast(Comparator.naturalOrder()));

        programma.sort(perData.thenComparing(perOra).thenComparing(perSala));

        for (int i = 0; i < programma.size(); i++) {
            RigaProgrammaDTO riga = programma.get(i);
            riga.setPosizione(i + 1);
            boolean prima = (i == 0)
                    || riga.getData() == null
                    || !riga.getData().equals(programma.get(i - 1).getData());
            riga.setPrimaDelGiorno(prima);
        }

        return programma;
    }
}
