package org.example.repository;

import org.example.model.Distributore;
import org.example.model.PacchettoProdotti;
import org.example.model.StatoProdotto;
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
