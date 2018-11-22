package com.gary.demo.oauth2.controllers;

import com.gary.demo.oauth2.exceptions.RestError;
import com.gary.demo.oauth2.entity.RestResponse;
import com.gary.demo.oauth2.model.Account;
import com.gary.demo.oauth2.model.AccountResponse;
import com.gary.demo.oauth2.model.Client;
import com.gary.demo.oauth2.model.ClientResponse;
import com.gary.demo.oauth2.services.AccountService;
import com.gary.demo.oauth2.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletResponse;

@RestController
public class Oauth2Controller {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    private static final String ENDPOINT_V1_USER = "/v1/auth/user";
    private static final String ENDPOINT_V1_ADMIN = "/v1/auth/admin";
    private static final String ENDPOINT_V1_CLIENT = "/v1/auth/client";

    /**
     * Get User Details
     * @return Account object
     */
    @RequestMapping(value = ENDPOINT_V1_USER, method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse user(HttpServletResponse response) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AccountResponse accountResponse = accountService.findAccountByUsername(username);
        response.setStatus(accountResponse.getResponseStatus().value());
        return accountResponse;
    }


    /**
     * register a user.  This endpoint is open for anyone to access
     * @param account
     * @return Account Object
     */
    @RequestMapping(value = ENDPOINT_V1_USER + "/register", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse register(@RequestBody Account account, HttpServletResponse response) {
        AccountResponse accountResponse = accountService.registerUser(account);
        response.setStatus(accountResponse.getResponseStatus().value());
        return accountResponse;
    }

    /**
     * update user role.  This endpoint is only accessible by an admin
     * @return 200 if successful
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ENDPOINT_V1_USER + "/updateUser", method= RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse updateUser(@RequestBody Account account, HttpServletResponse response) {
        AccountResponse accountResponse =  accountService.updateAccount(account);
        response.setStatus(accountResponse.getResponseStatus().value());
        return accountResponse;
    }


    /**
     * Remove a user from registry.  This endpoint is only accessible by an admin
     * @return 200 if successful
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = ENDPOINT_V1_USER + "/remove", method= RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse removeUser() {
        return accountService.removeAuthenticatedAccount();
    }


    /**
     * Get client detail
     * @return client Object
     */
    @RequestMapping(value = ENDPOINT_V1_CLIENT, method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse client(HttpServletResponse response) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String clientId = ((OAuth2Authentication) a).getOAuth2Request().getClientId();
        ClientResponse clientResponse = clientService.findClientByclientId(clientId);
        response.setStatus(clientResponse.getResponseStatus().value());
        return clientResponse;
    }


    /**
     * register a client.  Only admin can access this endpoint
     * @param client
     * @return client object
     */
    @RequestMapping(value = ENDPOINT_V1_CLIENT + "/register", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse register(@RequestBody Client client, HttpServletResponse response) {
        ClientResponse clientResponse = clientService.register(client);
        response.setStatus(clientResponse.getResponseStatus().value());
        return clientResponse;
    }


    /**
     * update a client.  Only admin can access this endpoint
     * @param client
     * @return client object
     */
    @RequestMapping(value = ENDPOINT_V1_CLIENT + "/updateClient", method= RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse updateClient(@RequestBody Client client, HttpServletResponse response) {
        ClientResponse clientResponse = clientService.updateClient(client);
        response.setStatus(clientResponse.getResponseStatus().value());
        return clientResponse;
    }

}
