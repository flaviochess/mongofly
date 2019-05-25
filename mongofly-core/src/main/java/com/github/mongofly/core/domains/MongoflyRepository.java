package com.github.mongofly.core.domains;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoflyRepository extends MongoRepository<Mongofly, String> {

    Optional<Mongofly> findByVersion(String version);
}
