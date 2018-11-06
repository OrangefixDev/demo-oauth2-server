package com.gary.demo.oauth2;

import com.gary.demo.oauth2.entity.Account;
import com.gary.demo.oauth2.entity.Client;
import com.gary.demo.oauth2.services.AccountService;
import com.gary.demo.oauth2.services.ClientService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.CredentialException;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class DemoOauth2Application {

	public static void main(String[] args) {
		SpringApplication.run(DemoOauth2Application.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean @Qualifier("mainDataSource")
	public DataSource dataSource(){
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder
				.setType(EmbeddedDatabaseType.H2)
				.build();
		return db;
	}

	@Bean
	CommandLineRunner init(AccountService accountService, ClientService clientService) {
		return (evt) -> {
			Arrays.asList(
					"user,admin".split(",")).forEach(
					username -> {
						Account acct = new Account();
						acct.setUsername(username);
						if (username.equals("user")) acct.setPassword("UserP@ssw0rd");
						else acct.setPassword("AdminP@ssw0rd");
						acct.setFirstName(username);
						acct.setLastName("LastName");

						if (username.equals("admin")) {
							try {
								accountService.registerAdmin(acct);
							} catch (AccountException e) {
								e.printStackTrace();
							}
						}
						else{
							try {
								accountService.registerUser(acct);
							} catch (AccountException e) {
								e.printStackTrace();
							}
						}
					}
			);
			Client client = new Client();
			client.setClientId("democrud");
			client.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList("client_credentials", "password", "refresh_token")));
			client.setAuthorities("ROLE_TRUSTED_CLIENT");
			client.setScope(new HashSet<>(Arrays.asList("read", "write")));
			client.setResourceIds(new HashSet<>(Arrays.asList("democrud")));
			client.setAccessTokenValiditySeconds(new Integer("7200"));
			client.setRefreshTokenValiditySeconds(new Integer("7200"));
			client.setClientSecret("123password");
			try {
				clientService.register(client);
			} catch (CredentialException e) {
				e.printStackTrace();
			}
		};
	}
}
