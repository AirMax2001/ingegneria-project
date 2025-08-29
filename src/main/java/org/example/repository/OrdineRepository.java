package org.example.repository;

import org.example.model.Ordine;
import org.example.model.StatoOrdine;
import org.example.model.UtenteLoggato;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineRepository extends MongoRepository<Ordine, String> {
    List<Ordine> findByAcquirente(UtenteLoggato acquirente);
    List<Ordine> findByStato(StatoOrdine stato);
    List<Ordine> findByAcquirenteOrderByDataCreazioneDesc(UtenteLoggato acquirente);
}
