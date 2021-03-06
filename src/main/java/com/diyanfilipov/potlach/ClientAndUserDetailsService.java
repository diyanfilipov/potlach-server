/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package com.diyanfilipov.potlach;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * A class that combines a UserDetailsService and ClientDetailsService
 * into a single object.
 * 
 * @author jules
 *
 */
public class ClientAndUserDetailsService implements UserDetailsService,
		ClientDetailsService {

	private final ClientDetailsService clients_;

	private final InMemoryUserDetailsManager users_;
	
	private final ClientDetailsUserDetailsService clientDetailsWrapper_;

	public ClientAndUserDetailsService(ClientDetailsService clients,
			InMemoryUserDetailsManager users) {
		super();
		clients_ = clients;
		users_ = users;
		clientDetailsWrapper_ = new ClientDetailsUserDetailsService(clients_);
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId)
			throws ClientRegistrationException {
		return clients_.loadClientByClientId(clientId);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UserDetails user = null;
		try{
			user = users_.loadUserByUsername(username);
		}catch(UsernameNotFoundException e){
			user = clientDetailsWrapper_.loadUserByUsername(username);
		}
		return user;
	}
	
	public void createUser(String username, String password, String... authorities){
		users_.createUser(User.create(username, password, authorities));
	}

}
