package com.diyanfilipov.potlach;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.diyanfilipov.potlach.client.PotlachSvcApi;

@Controller
public class UserController {

	@Autowired
	private ClientAndUserDetailsService userDetailService;
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public @ResponseBody boolean register(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			HttpServletResponse response) throws IOException{
		
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}
		try{
			userDetailService.createUser(username, password, "USER");
		}catch(IllegalArgumentException iae){ // caused by Assertion fail in  InMemoryUserDetailsManager.createUser
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username already exists.");
		}
		UserDetails user = userDetailService.loadUserByUsername(username);
		if(user == null){
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = PotlachSvcApi.POTLACH_SIGN_IN_PATH, method = RequestMethod.POST)
	public @ResponseBody boolean signIn(){
		return true;
	}
}
