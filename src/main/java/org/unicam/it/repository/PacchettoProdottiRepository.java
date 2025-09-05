package org.unicam.it.repository;

import org.unicam.it.model.Distributore;
import org.unicam.it.model.PacchettoProdotti;
import org.unicam.it.model.StatoProdotto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacchettoProdottiRepository extends MongoRepository<PacchettoProdotti, String> {

    List<PacchettoProdotti> findByDistributore(Distributore distributore);

    List<PacchettoProdotti> findByStato(StatoProdotto stato);

    List<PacchettoProdotti> findByStatoAndAttivoTrue(StatoProdotto stato);

    List<PacchettoProdotti> findByDistributoreAndStato(Distributore distributore, StatoProdotto stato);

    List<PacchettoProdotti> findByAttivoTrueOrderByDataCreazioneDesc();

    List<PacchettoProdotti> findByNomeContainingIgnoreCase(String nome);

    List<PacchettoProdotti> findByStatoAndAttivoTrueAndQuantitaDisponibileGreaterThan(
        StatoProdotto stato, int quantita);
}
