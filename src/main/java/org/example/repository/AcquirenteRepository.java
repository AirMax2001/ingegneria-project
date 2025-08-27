package org.example.repository;

import org.example.model.Acquirente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcquirenteRepository extends MongoRepository<Acquirente, String> {
    Optional<Acquirente> findByEmail(String email);
    boolean existsByEmail(String email);
}
