package org.example.repository;

import org.example.model.Carrello;
import org.example.model.UtenteLoggato;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrelloRepository extends MongoRepository<Carrello, String> {
    Optional<Carrello> findByProprietario(UtenteLoggato proprietario);
    void deleteByProprietario(UtenteLoggato proprietario);
}
