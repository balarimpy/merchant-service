package io.movmint.msp.merchant.data.repository;

import io.movmint.msp.merchant.data.entity.Business;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends MongoRepository<Business, String> {
    boolean existsByName(String name);
}
