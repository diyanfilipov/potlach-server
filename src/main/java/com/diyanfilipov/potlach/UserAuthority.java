package com.diyanfilipov.potlach;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class UserAuthority implements GrantedAuthority{
	private static final long serialVersionUID = -7516193374704733588L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String authority;
	
	public UserAuthority(){}
	
	public UserAuthority(String role){
		authority = role;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

}
