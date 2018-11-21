package com.gary.demo.oauth2.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Oauth2ServerResponse {

    @JsonProperty
    private final HttpStatus responseStatus;

    @JsonProperty
    private final String responseMessage;

    public Oauth2ServerResponse(HttpStatus status, String responseMessage) {
        this.responseStatus = status;
        this.responseMessage = responseMessage;
    }

    public Oauth2ServerResponse(HttpStatus status) {
        this(status, null);
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Oauth2ServerResponse{");
        sb.append("responseStatus=").append(responseStatus);
        sb.append(", responseMessage='").append(responseMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
