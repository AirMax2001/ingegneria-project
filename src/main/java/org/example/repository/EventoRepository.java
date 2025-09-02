package org.example.repository;

import org.example.model.Evento;
import org.example.model.UtenteLoggato;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends MongoRepository<Evento, String> {
    List<Evento> findByAnimatore(UtenteLoggato animatore);
    List<Evento> findByDataInizioAfter(LocalDateTime data);
    List<Evento> findByPartecipantiContaining(UtenteLoggato user);
    List<Evento> findByDataInizioAfterOrderByDataInizioAsc(LocalDateTime data);
}
