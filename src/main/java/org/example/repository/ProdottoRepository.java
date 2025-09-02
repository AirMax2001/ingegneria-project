package org.example.repository;

import org.example.model.Prodotto;
import org.example.model.StatoProdotto;
import org.example.model.UtenteLoggato;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottoRepository extends MongoRepository<Prodotto, String> {
    List<Prodotto> findByStato(StatoProdotto stato);
    List<Prodotto> findByVenditore(UtenteLoggato venditore);
    List<Prodotto> findByStatoAndQuantitaDisponibileGreaterThan(StatoProdotto stato, int quantita);

    @Query("{'nome': {$regex: ?0, $options: 'i'}}")
    List<Prodotto> findByNomeContainingIgnoreCase(String nome);

    @Query("{'categoria': ?0, 'stato': ?1}")
    List<Prodotto> findByCategoriaAndStato(String categoria, StatoProdotto stato);

    List<Prodotto> findByStatoOrderByDataCreazioneDesc(StatoProdotto stato);
}
