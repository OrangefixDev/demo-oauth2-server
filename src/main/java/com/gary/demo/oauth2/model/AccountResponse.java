package com.gary.demo.oauth2.model;

import org.springframework.http.HttpStatus;

public class AccountResponse extends Oauth2ServerResponse {

    private Account account;

    public AccountResponse(HttpStatus status, String responseMessage) {
        super(status, responseMessage);
    }

    public AccountResponse(HttpStatus status) {
        super(status);
    }

    public AccountResponse(HttpStatus status, String responseMessage, Account account) {
        super(status, responseMessage);
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccountResponse{");
        sb.append("account=").append(account);
        sb.append('}');
        return sb.toString();
    }
}
