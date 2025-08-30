package org.example.repository;

import org.example.model.Distributore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistributoreRepository extends MongoRepository<Distributore, String> {
    Optional<Distributore> findByEmail(String email);
    boolean existsByEmail(String email);
}
