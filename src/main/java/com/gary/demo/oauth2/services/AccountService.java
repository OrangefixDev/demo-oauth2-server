package com.gary.demo.oauth2.services;

import com.gary.demo.oauth2.repository.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.security.auth.login.AccountException;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String defaultPassword = "P@ssw0rd";


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<com.gary.demo.oauth2.entity.Account> accountEntity = accountDao.findByUsername(userName);
        if (accountEntity.isPresent()) {
            return accountEntity.get();
        } else {
            throw new UsernameNotFoundException(String.format("Username[%s] not found", userName));
        }
    }

    public com.gary.demo.oauth2.model.Account findAccountByUsername(String username) throws UsernameNotFoundException {
        Optional<com.gary.demo.oauth2.entity.Account> accountEntity = accountDao.findByUsername(username);
        if (accountEntity.isPresent()) {
            return convertToAccountDto(accountEntity.get());
        } else {
            throw new UsernameNotFoundException(String.format("Username[%s] not found", username));
        }

    }

    public com.gary.demo.oauth2.model.Account registerUser(com.gary.demo.oauth2.model.Account accountDto) throws AccountException {
        if (accountDao.countByUsername(accountDto.getUsername() ) == 0) {
            com.gary.demo.oauth2.entity.Account accountEntity = convertToAccountEntity(accountDto);
            accountEntity.setPassword(passwordEncoder.encode(StringUtils.isEmpty(accountDto.getPassword())?defaultPassword:accountDto.getPassword()));
            //grant user access by default
            accountEntity.grantAuthority("ROLE_USER");
            return convertToAccountDto(accountDao.save(accountEntity));
        } else {
            throw new AccountException(String.format("Username[%s] already taken.", accountDto.getUsername()));
        }
    }

    public com.gary.demo.oauth2.model.Account registerAdmin(com.gary.demo.oauth2.model.Account accountDto) throws AccountException {
        if (accountDao.countByUsername(accountDto.getUsername() ) == 0) {
            com.gary.demo.oauth2.entity.Account accountEntity = convertToAccountEntity(accountDto);
            accountEntity.setPassword(passwordEncoder.encode(StringUtils.isEmpty(accountDto.getPassword())?defaultPassword:accountDto.getPassword()));
            //grant user access by default
            accountEntity.grantAuthority("ROLE_ADMIN");
            return convertToAccountDto(accountDao.save(accountEntity));
        } else {
            throw new AccountException(String.format("Username[%s] already taken.", accountDto.getUsername()));
        }
    }
    public com.gary.demo.oauth2.model.Account registerUser(com.gary.demo.oauth2.entity.Account accountEntity) throws AccountException {
        com.gary.demo.oauth2.model.Account accountDto = convertToAccountDto(accountEntity);
        accountDto.setPassword(accountEntity.getPassword());
        return registerUser(accountDto);
    }

    public com.gary.demo.oauth2.model.Account registerAdmin(com.gary.demo.oauth2.entity.Account accountEntity) throws AccountException {
        com.gary.demo.oauth2.model.Account accountDto = convertToAccountDto(accountEntity);
        accountDto.setPassword(accountEntity.getPassword());
        return registerAdmin(accountDto);
    }

    @Transactional
    public void removeAuthenticatedAccount() throws UsernameNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gary.demo.oauth2.model.Account accountDto = findAccountByUsername(username);
        accountDao.deleteAccountById(accountDto.getId());

    }


    private com.gary.demo.oauth2.model.Account convertToAccountDto(com.gary.demo.oauth2.entity.Account accountEntity){
        com.gary.demo.oauth2.model.Account accountDto = new com.gary.demo.oauth2.model.Account();
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setFirstName(accountEntity.getFirstName());
        accountDto.setLastName(accountEntity.getLastName());
        accountDto.setUsername(accountEntity.getUsername());
        accountDto.setId(accountEntity.getId());
        //not returning password
        return accountDto;
    }

    private com.gary.demo.oauth2.entity.Account convertToAccountEntity(com.gary.demo.oauth2.model.Account accountDto){
        com.gary.demo.oauth2.entity.Account accountEntity = new com.gary.demo.oauth2.entity.Account();
        accountEntity.setEmail(accountDto.getEmail());
        accountEntity.setFirstName(accountDto.getFirstName());
        accountEntity.setLastName(accountDto.getLastName());
        accountEntity.setUsername(accountDto.getUsername());
        //not setting the password
        //not setting the ID because it comes from the database
        return accountEntity;
    }
}
