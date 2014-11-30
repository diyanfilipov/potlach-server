package com.diyanfilipov.potlach;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"giftId", "username"})) 
public class GiftTouches {
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long giftId;
	private String username;
	
	public GiftTouches(){}
	
	public GiftTouches(long giftId, String username){
		super();
		this.giftId = giftId;
		this.username = username;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getGiftId() {
		return giftId;
	}
	public void setGiftId(long giftId) {
		this.giftId = giftId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
