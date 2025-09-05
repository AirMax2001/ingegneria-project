package org.unicam.it.repository;

import org.unicam.it.model.Curatore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuratoreRepository extends MongoRepository<Curatore, String> {
    Optional<Curatore> findByEmail(String email);
    boolean existsByEmail(String email);
}
