package com.github.mongofly.core.usecases;

import com.github.mongofly.core.domains.Mongofly;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoflyRepository extends MongoRepository<Mongofly, String> {

    Optional<Mongofly> findByVersion(String version);
}
