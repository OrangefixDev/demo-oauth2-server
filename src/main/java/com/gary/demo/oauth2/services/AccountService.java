package com.gary.demo.oauth2.services;

import com.gary.demo.oauth2.model.AccountResponse;
import com.gary.demo.oauth2.repository.AccountMongoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountMongoDao accountDao;

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

    public AccountResponse findAccountByUsername(String username) {
        Optional<com.gary.demo.oauth2.entity.Account> accountEntity = accountDao.findByUsername(username);
        if (accountEntity.isPresent()) {
            return new AccountResponse(HttpStatus.OK, "Successful", convertToAccountDto(accountEntity.get()));
        } else {
            return new AccountResponse(HttpStatus.NOT_FOUND, "User name not found");
        }

    }

    public AccountResponse registerUser(com.gary.demo.oauth2.model.Account accountDto){
        if (accountDao.countByUsername(accountDto.getUsername() ) == 0) {
            com.gary.demo.oauth2.entity.Account accountEntity = convertToAccountEntity(accountDto);
            accountEntity.setPassword(passwordEncoder.encode(StringUtils.isEmpty(accountDto.getPassword())?defaultPassword:accountDto.getPassword()));
            //grant user access by default
            accountEntity.grantAuthority("ROLE_USER");
            try{
                return new AccountResponse(HttpStatus.CREATED, "Created", convertToAccountDto(accountDao.save(accountEntity)));
            }
            catch(Exception e){
                return  new AccountResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create account in database", accountDto);
            }
        } else {
            return  new AccountResponse(HttpStatus.BAD_REQUEST, "Username already taken.", accountDto);
        }
    }

    public AccountResponse registerAdmin(com.gary.demo.oauth2.model.Account accountDto){
        if (accountDao.countByUsername(accountDto.getUsername() ) == 0) {
            com.gary.demo.oauth2.entity.Account accountEntity = convertToAccountEntity(accountDto);
            accountEntity.setPassword(passwordEncoder.encode(StringUtils.isEmpty(accountDto.getPassword())?defaultPassword:accountDto.getPassword()));
            //grant user access by default
            accountEntity.grantAuthority("ROLE_ADMIN");
            try{
                return new AccountResponse(HttpStatus.CREATED, "Created", convertToAccountDto(accountDao.save(accountEntity)));
            }
            catch(Exception e){
                return  new AccountResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create account in database", accountDto);
            }
        } else {
            return  new AccountResponse(HttpStatus.BAD_REQUEST, "Username already taken.", accountDto);
        }
    }

    @Transactional
    public AccountResponse updateAccount(com.gary.demo.oauth2.model.Account accountDto){

        Optional<com.gary.demo.oauth2.entity.Account> accountEntityOptional = accountDao.findByUsername(accountDto.getUsername());
        com.gary.demo.oauth2.entity.Account accountEntity = accountEntityOptional.get();
        if (accountEntity != null) {
            boolean hasChange = false;
            if(accountDto.getRoles() != null && accountDto.getRoles().size() >0) {
                accountEntity.setRoles(accountDto.getRoles());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(accountDto.getEmail())){
                accountEntity.setEmail(accountDto.getEmail());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(accountDto.getFirstName())){
                accountEntity.setFirstName(accountDto.getFirstName());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(accountDto.getLastName())){
                accountEntity.setLastName(accountDto.getLastName());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(accountDto.getPassword())){
                accountEntity.setPassword(passwordEncoder.encode(accountDto.getPassword()));
                hasChange = true;
            }
            try{
                if(hasChange) {
                    return new AccountResponse(HttpStatus.OK, "Updated", convertToAccountDto(accountDao.save(accountEntity)));
                }
                else{
                    return new AccountResponse(HttpStatus.BAD_REQUEST, "No information is provided for the update");
                }
            }
            catch(Exception e){
                return  new AccountResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update account in database", accountDto);
            }
        } else {
            return  new AccountResponse(HttpStatus.BAD_REQUEST, "Username does not exist", accountDto);
        }

    }

    /**
     * The method is used internally in the application
     * @param accountEntity
     * @return
     * @throws AccountException
     */
    public com.gary.demo.oauth2.model.Account registerUser(com.gary.demo.oauth2.entity.Account accountEntity) throws AccountException {
        com.gary.demo.oauth2.model.Account accountDto = convertToAccountDto(accountEntity);
        accountDto.setPassword(accountEntity.getPassword());
        AccountResponse accountResponse = registerUser(accountDto);
        if(accountResponse.getResponseStatus().equals(HttpStatus.CREATED)){
            return accountResponse.getAccount();
        }
        else{
            return null;
        }
    }

    /**
     * The method is used internally in the applicaiton
     * @param accountEntity
     * @return
     * @throws AccountException
     */
    public com.gary.demo.oauth2.model.Account registerAdmin(com.gary.demo.oauth2.entity.Account accountEntity) throws AccountException {
        com.gary.demo.oauth2.model.Account accountDto = convertToAccountDto(accountEntity);
        accountDto.setPassword(accountEntity.getPassword());
        AccountResponse accountResponse = registerAdmin(accountDto);
        if(accountResponse.getResponseStatus().equals(HttpStatus.CREATED)){
            return accountResponse.getAccount();
        }
        else{
            return null;
        }
    }

    @Transactional
    public AccountResponse removeAuthenticatedAccount() throws UsernameNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gary.demo.oauth2.model.Account accountDto = findAccountByUsername(username).getAccount();
        accountDao.deleteAccountByUsername(accountDto.getUsername());
        return new AccountResponse(HttpStatus.OK, "User Deleted");
    }


    private com.gary.demo.oauth2.model.Account convertToAccountDto(com.gary.demo.oauth2.entity.Account accountEntity){
        com.gary.demo.oauth2.model.Account accountDto = new com.gary.demo.oauth2.model.Account();
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setFirstName(accountEntity.getFirstName());
        accountDto.setLastName(accountEntity.getLastName());
        accountDto.setUsername(accountEntity.getUsername());
        accountDto.setRoles(accountEntity.getRoles());
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
