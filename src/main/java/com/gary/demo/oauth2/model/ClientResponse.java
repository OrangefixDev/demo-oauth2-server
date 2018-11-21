package com.gary.demo.oauth2.model;

import org.springframework.http.HttpStatus;

public class ClientResponse extends Oauth2ServerResponse {

    private Client client;

    public ClientResponse(HttpStatus status, String responseMessage) {
        super(status, responseMessage);
    }

    public ClientResponse(HttpStatus status) {
        super(status);
    }

    public ClientResponse(HttpStatus status, String responseMessage, Client client) {
        super(status, responseMessage);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientResponse{");
        sb.append("client=").append(client);
        sb.append('}');
        return sb.toString();
    }
}
