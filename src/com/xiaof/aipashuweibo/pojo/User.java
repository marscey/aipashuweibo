package com.xiaof.aipashuweibo.pojo;

import android.graphics.drawable.Drawable;

public class User {
	private Long id;
	private String userId;
	private String userName;
	private Drawable userHead;
	private String token;
	private String tokenSecret;
	private String description;
	
	public User(){
		
	}
	
	public User(String userId, String userName, String token,
			String tokenSecret, String description){
		this.userId = userId;
		this.userName = userName;
		this.token = token;
		this.tokenSecret = tokenSecret;
		this.description = description;
	}
	
	public User(String userId,String userName, String token,
			String tokenSecret, String description, Drawable userHead){
		this(userId, userName, token, tokenSecret, description);
		this.userHead = userHead;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", userId=" + userId + ", userName="
				+ userName + ", userHead=" + userHead + ", token=" + token
				+ ", tokenSecret=" + tokenSecret + ", description="
				+ description + "]";
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Drawable getUserHead() {
		return userHead;
	}
	public void setUserHead(Drawable userHead) {
		this.userHead = userHead;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenSecret() {
		return tokenSecret;
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
