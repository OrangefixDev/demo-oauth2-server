package com.gary.demo.oauth2.services;

import com.gary.demo.oauth2.model.ClientResponse;
import com.gary.demo.oauth2.repository.ClientMongoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public ClientResponse register(com.gary.demo.oauth2.model.Client clientDto) {
        if (clientDao.countByClientId(clientDto.getClientId()) == 0) {
            try{
                return new ClientResponse(HttpStatus.CREATED, "Created", convertToClientDto(clientDao.save(convertoToClientEntity(clientDto))));
            }
            catch(Exception e){
                return new ClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to regiester client to Database", clientDto);
            }
        } else {
            return new ClientResponse(HttpStatus.BAD_REQUEST, "Client ID already taken.", clientDto);
        }
    }


    public ClientResponse updateClient(com.gary.demo.oauth2.model.Client clientDto) {
        Optional<com.gary.demo.oauth2.entity.Client> clientEntityOptional = clientDao.findByClientId(clientDto.getClientId());
        com.gary.demo.oauth2.entity.Client clientEntity = clientEntityOptional.get();
        if (clientEntity != null) {
            boolean hasChange = false;
            if(clientDto.getAccessTokenValiditySeconds() != null){
                clientEntity.setAccessTokenValiditySeconds(clientDto.getAccessTokenValiditySeconds());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(clientDto.getAuthorities())){
                clientEntity.setAuthorities(clientDto.getAuthorities());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(clientDto.getAuthorizedGrantTypes())){
                clientEntity.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(clientDto.getAuthorities().split(","))));
                hasChange = true;
            }
            if(clientDto.getRefreshTokenValiditySeconds() != null){
                clientEntity.setRefreshTokenValiditySeconds(clientDto.getRefreshTokenValiditySeconds());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(clientDto.getClientSecret())){
                clientEntity.setClientSecret(clientDto.getClientSecret());
                hasChange = true;
            }
            if(!StringUtils.isEmpty(clientDto.getRegisteredRedirectUri())){
                clientEntity.setRegisteredRedirectUri(new HashSet<>(Arrays.asList(clientDto.getRegisteredRedirectUri().split(","))));
                hasChange = true;
            }
            if(!StringUtils.isEmpty(clientDto.getResourceIds())){
                clientEntity.setResourceIds(new HashSet<>(Arrays.asList((clientDto.getResourceIds().split(",")))));
                hasChange = true;
            }
            if(!StringUtils.isEmpty(clientDto.getScope())){
                clientEntity.setScope(new HashSet<>(Arrays.asList(clientDto.getScope().split(","))));
                hasChange = true;
            }
            try{
                if(hasChange){
                    return new ClientResponse(HttpStatus.CREATED, "Updated", convertToClientDto(clientDao.save(convertoToClientEntity(clientDto))));
                }
                else{
                    return new ClientResponse(HttpStatus.BAD_REQUEST, "No information is provided for the update");
                }

            }
            catch(Exception e){
                return new ClientResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update client to Database", clientDto);
            }
        } else {
            return new ClientResponse(HttpStatus.BAD_REQUEST, "Client ID does not exist", clientDto);
        }
    }
    /**
     * The method is used internally in the application
     * @param clientEntity
     * @return
     */
    public com.gary.demo.oauth2.model.Client register(com.gary.demo.oauth2.entity.Client clientEntity) {
        com.gary.demo.oauth2.model.Client clientDto = convertToClientDto(clientEntity);
        clientDto.setClientSecret(clientEntity.getClientSecret());
        ClientResponse clientResponse = register(clientDto);
        if(clientResponse.getResponseStatus().equals(HttpStatus.CREATED)){
           return clientResponse.getClient();
        }
        else {
            //return nothing since it is not created
            return null;
        }
    }

    public ClientResponse findClientByclientId(String clientId) {
        Optional<com.gary.demo.oauth2.entity.Client> clientEntity = clientDao.findByClientId(clientId);
        if (clientEntity.isPresent()) {
            return new ClientResponse(HttpStatus.OK, "Successful", convertToClientDto(clientEntity.get()));
        } else {
            return new ClientResponse(HttpStatus.NOT_FOUND, "Client Id not found");
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
