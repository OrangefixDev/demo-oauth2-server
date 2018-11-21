package com.gary.demo.oauth2.repository;

import com.gary.demo.oauth2.entity.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClientMongoDao extends MongoRepository<Client, Long> {
    List<Client> findAll();
    Optional<Client> findByClientId(String clientId);
    Integer countByClientId(String clientId);
    Client save(Client client);
    void deleteClientByClientId(String clientId);
}