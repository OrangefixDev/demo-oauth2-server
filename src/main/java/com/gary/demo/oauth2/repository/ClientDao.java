package com.gary.demo.oauth2.repository;

import com.gary.demo.oauth2.entity.Client;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;

public interface ClientDao extends Repository<Client, Long> {

    Collection<Client> findAll();
    Optional<Client> findByClientId(String clientId);
    Integer countByClientId(String clientId);
    Client save(Client client);
    void deleteClientByClientId(String clientId);



}