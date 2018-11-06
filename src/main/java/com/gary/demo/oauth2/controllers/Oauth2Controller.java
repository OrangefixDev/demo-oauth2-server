package com.gary.demo.oauth2.controllers;

import com.gary.demo.oauth2.exceptions.RestError;
import com.gary.demo.oauth2.entity.RestResponse;
import com.gary.demo.oauth2.model.Account;
import com.gary.demo.oauth2.model.Client;
import com.gary.demo.oauth2.services.AccountService;
import com.gary.demo.oauth2.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.CredentialException;

@RestController
public class Oauth2Controller {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    private static final String ENDPOINT_V1_USER = "/v1/auth/user";
    private static final String ENDPOINT_V1_CLIENT = "/v1/auth/client";

    @GetMapping(path = ENDPOINT_V1_USER, produces = "application/json" )
    public Account user() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountService.findAccountByUsername(username);
    }


    @PostMapping(path = ENDPOINT_V1_USER + "/register", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody Account account) {
        try {

            return new ResponseEntity<Object>(
                    accountService.register(account), HttpStatus.OK);
        } catch (AccountException e) {
            e.printStackTrace();
            return new ResponseEntity<RestError>(new RestError(e.getMessage()),HttpStatus.BAD_REQUEST );
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = ENDPOINT_V1_USER + "/remove", produces = "application/json")
    public ResponseEntity<?> removeUser() {
        try {
            accountService.removeAuthenticatedAccount();
            return new ResponseEntity<Object>(new RestResponse("User removed."), HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(new RestError(e.getMessage()), HttpStatus.OK);
        }
    }


    @GetMapping(path = ENDPOINT_V1_CLIENT, produces = "application/json" )
    public ResponseEntity<?> client() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String clientId = ((OAuth2Authentication) a).getOAuth2Request().getClientId();
        try {
            return new ResponseEntity<Object>(clientService.findClientByclientId(clientId), HttpStatus.OK);
        } catch (CredentialException e) {
            e.printStackTrace();
            return new ResponseEntity<RestError>(new RestError(e.getMessage()),HttpStatus.BAD_REQUEST );
        }
    }

    @PostMapping(path = ENDPOINT_V1_CLIENT + "/register", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody Client client) {
        try {

            return new ResponseEntity<Object>(
                    clientService.register(client), HttpStatus.OK);
        } catch (CredentialException e) {
            e.printStackTrace();
            return new ResponseEntity<RestError>(new RestError(e.getMessage()),HttpStatus.BAD_REQUEST );
        }
    }


}
