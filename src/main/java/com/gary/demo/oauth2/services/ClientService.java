package com.gary.demo.oauth2.services;

import com.gary.demo.oauth2.repository.ClientMongoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.security.auth.login.CredentialException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
@Service
public class ClientService implements ClientDetailsService {

    @Autowired
    private ClientMongoDao clientDao;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<com.gary.demo.oauth2.entity.Client> client = clientDao.findByClientId(clientId);
        if (client.isPresent()) {
            return client.get();
        } else {
                throw new OAuth2Exception(String.format("ClientId[%s] not found", clientId));
        }
    }

    public com.gary.demo.oauth2.model.Client register(com.gary.demo.oauth2.model.Client clientDto) throws CredentialException {
        if (clientDao.countByClientId(clientDto.getClientId()) == 0) {
            return convertToClientDto(clientDao.save(convertoToClientEntity(clientDto)));
        } else {
            throw new CredentialException(String.format("Client ID[%s] already taken.", clientDto.getClientId()));
        }
    }

    public com.gary.demo.oauth2.model.Client register(com.gary.demo.oauth2.entity.Client clientEntity) throws CredentialException {
        com.gary.demo.oauth2.model.Client clientDto = convertToClientDto(clientEntity);
        clientDto.setClientSecret(clientEntity.getClientSecret());
        return register(clientDto);
    }

    public com.gary.demo.oauth2.model.Client findClientByclientId(String clientId) throws CredentialException {
        Optional<com.gary.demo.oauth2.entity.Client> clientEntity = clientDao.findByClientId(clientId);
        if (clientEntity.isPresent()) {
            return convertToClientDto(clientEntity.get());
        } else {
            throw new CredentialException(String.format("Username[%s] not found", clientId));
        }

    }


    private com.gary.demo.oauth2.model.Client convertToClientDto(com.gary.demo.oauth2.entity.Client clientEntity){
        com.gary.demo.oauth2.model.Client clientDto = new com.gary.demo.oauth2.model.Client();
        clientDto.setAccessTokenValiditySeconds(clientEntity.getAccessTokenValiditySeconds());
        clientDto.setRefreshTokenValiditySeconds(clientEntity.getRefreshTokenValiditySeconds());
        clientDto.setAuthorities(StringUtils.collectionToCommaDelimitedString(clientEntity.getAuthorities()));
        clientDto.setAuthorizedGrantTypes(StringUtils.collectionToCommaDelimitedString(clientEntity.getAuthorizedGrantTypes()));
        clientDto.setClientId(clientEntity.getClientId());
        //not returning client secret
        clientDto.setRegisteredRedirectUri(StringUtils.collectionToCommaDelimitedString(clientEntity.getRegisteredRedirectUri()));
        clientDto.setResourceIds(StringUtils.collectionToCommaDelimitedString(clientEntity.getResourceIds()));
        clientDto.setScope(StringUtils.collectionToCommaDelimitedString(clientEntity.getScope()));

        return clientDto;
    }

    private com.gary.demo.oauth2.entity.Client convertoToClientEntity(com.gary.demo.oauth2.model.Client clientDto){
        com.gary.demo.oauth2.entity.Client clientEntity = new com.gary.demo.oauth2.entity.Client();
        clientEntity.setAccessTokenValiditySeconds(clientDto.getAccessTokenValiditySeconds());
        clientEntity.setRefreshTokenValiditySeconds(clientDto.getRefreshTokenValiditySeconds());
        clientEntity.setAuthorities(clientDto.getAuthorities());
        clientEntity.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(clientDto.getAuthorizedGrantTypes().split(","))));
        clientEntity.setClientId(clientDto.getClientId());
        clientEntity.setRegisteredRedirectUri(StringUtils.isEmpty(clientDto.getRegisteredRedirectUri())?null:(new HashSet<>(Arrays.asList(clientDto.getRegisteredRedirectUri().split(",")))));
        clientEntity.setResourceIds(StringUtils.isEmpty(clientDto.getResourceIds())?null:(new HashSet<>(Arrays.asList(clientDto.getResourceIds().split(",")))));
        clientEntity.setScope(StringUtils.isEmpty(clientDto.getScope())?null:(new HashSet<>(Arrays.asList(clientDto.getScope().split(",")))));
        clientEntity.setClientSecret(clientDto.getClientSecret());

        return clientEntity;
    }
}
