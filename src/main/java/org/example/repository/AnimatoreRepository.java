package org.example.repository;

import org.example.model.Animatore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnimatoreRepository extends MongoRepository<Animatore, String> {
    Optional<Animatore> findByEmail(String email);
    boolean existsByEmail(String email);
}
