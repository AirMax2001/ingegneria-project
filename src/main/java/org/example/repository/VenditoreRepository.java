package org.example.repository;

import org.example.model.Venditore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VenditoreRepository extends MongoRepository<Venditore, String> {
    Optional<Venditore> findByEmail(String email);
    boolean existsByEmail(String email);
}
