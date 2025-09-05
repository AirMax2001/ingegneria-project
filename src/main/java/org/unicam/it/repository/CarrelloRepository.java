package org.unicam.it.repository;

import org.unicam.it.model.Carrello;
import org.unicam.it.model.UtenteLoggato;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrelloRepository extends MongoRepository<Carrello, String> {
    Optional<Carrello> findByProprietario(UtenteLoggato proprietario);
    void deleteByProprietario(UtenteLoggato proprietario);
}
