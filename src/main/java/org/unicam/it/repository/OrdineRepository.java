package org.unicam.it.repository;

import org.unicam.it.model.Ordine;
import org.unicam.it.model.StatoOrdine;
import org.unicam.it.model.UtenteLoggato;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineRepository extends MongoRepository<Ordine, String> {
    List<Ordine> findByAcquirente(UtenteLoggato acquirente);
    List<Ordine> findByStato(StatoOrdine stato);
    List<Ordine> findByAcquirenteOrderByDataCreazioneDesc(UtenteLoggato acquirente);
}
