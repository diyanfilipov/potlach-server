package com.diyanfilipov.integration.test;

import retrofit.client.ApacheClient;

import com.diyanfilipov.potlach.client.PotlachSvcApi;
import com.diyanfilipov.potlach.client.SecuredRestBuilder;

public final class TestUtils {
	public static final String TEST_URL = "https://localhost:8443";

	public static final String ADMIN = "admin";
	public static final String PASSWORD = "pass";
	public static final String CLIENT_ID = "mobile";
	
	public static final String DESCRIPTION = "Lorem ipsum dolor sit amet, "
			+ "consectetur adipisicing elit, sed do eiusmod tempor "
			+ "incididunt ut labore et dolore magna aliqua. "
			+ "Ut enim ad minim veniam, quis nostrud exercitation.";
	
	public static PotlachSvcApi createPotlachSvcApi(String username, String password){
		return new SecuredRestBuilder()
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL)
		.setLoginEndpoint(TEST_URL + PotlachSvcApi.TOKEN_PATH)
		// .setLogLevel(LogLevel.FULL)
		.setUsername(username).setPassword(password).setClientId(CLIENT_ID)
		.build().create(PotlachSvcApi.class);
	}

}
