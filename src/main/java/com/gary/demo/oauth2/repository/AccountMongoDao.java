package com.gary.demo.oauth2.repository;

import com.gary.demo.oauth2.entity.Account;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountMongoDao extends MongoRepository<Account, String> {

    @Override
    List<Account> findAll(Sort sort);
    Optional<Account> findByUsername(String username);
    Integer countByUsername(String username);
    Account save(Account account);
    void deleteAccountByUsername(String username);
}
