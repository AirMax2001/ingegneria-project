package org.unicam.it.service;

import org.unicam.it.model.Evento;
import org.unicam.it.model.UtenteLoggato;
import org.unicam.it.model.UserRole;
import org.unicam.it.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UserService userService;

    // Use Case: Crea Evento
    public Evento creaEvento(String animatoreId, String nome, String descrizione,
                           LocalDateTime dataInizio, LocalDateTime dataFine, String luogo, int maxPartecipanti) {
        UtenteLoggato animatore = userService.findById(animatoreId);
        if (animatore.getRuolo() != UserRole.ANIMATORE) {
            throw new IllegalArgumentException("L'utente non è un animatore");
        }

        Evento evento = new Evento(nome, descrizione, dataInizio, dataFine, animatore);
        evento.setLuogo(luogo);
        evento.setMaxPartecipanti(maxPartecipanti);

        return eventoRepository.save(evento);
    }

    // Use Case: Modifica Evento
    public Evento modificaEvento(String eventoId, String animatoreId, String nome,
                               String descrizione, LocalDateTime dataInizio,
                               LocalDateTime dataFine, String luogo, int maxPartecipanti) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

        if (!evento.getAnimatore().getId().equals(animatoreId)) {
            throw new IllegalArgumentException("Non autorizzato a modificare questo evento");
        }

        evento.setNome(nome);
        evento.setDescrizione(descrizione);
        evento.setDataInizio(dataInizio);
        evento.setDataFine(dataFine);
        evento.setLuogo(luogo);
        evento.setMaxPartecipanti(maxPartecipanti);

        return eventoRepository.save(evento);
    }

    // Use Case: Elimina Evento
    public void eliminaEvento(String eventoId, String animatoreId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

        if (!evento.getAnimatore().getId().equals(animatoreId)) {
            throw new IllegalArgumentException("Non autorizzato a eliminare questo evento");
        }

        eventoRepository.delete(evento);
    }

    // Use Case: Prenota Evento
    public Evento prenotaEvento(String eventoId, String userId) {
        UtenteLoggato user = userService.findById(userId);
        if (user.getRuolo() != UserRole.ACQUIRENTE) {
            throw new IllegalArgumentException("L'utente non può prenotare eventi");
        }

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

        evento.aggiungiPartecipante(user);

        return eventoRepository.save(evento);
    }

    // Business methods
    public List<Evento> getEventiDisponibili() {
        return eventoRepository.findByDataInizioAfterOrderByDataInizioAsc(LocalDateTime.now());
    }

    public List<Evento> getEventiByAnimatore(String animatoreId) {
        UtenteLoggato animatore = userService.findById(animatoreId);
        return eventoRepository.findByAnimatore(animatore);
    }

    public List<Evento> getEventiPrenotatiByUser(String userId) {
        UtenteLoggato user = userService.findById(userId);
        return eventoRepository.findByPartecipantiContaining(user);
    }

    public Evento findById(String eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));
    }
}
