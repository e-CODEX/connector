package eu.domibus.connector.ui.dto;

import java.util.Date;

import eu.domibus.connector.ui.enums.UserRole;

public class WebUser {
	
	private String username;
	private UserRole role;
	private String password;
	private boolean locked;
	private Date created;
	

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public UserRole getRole() {
		return role;
	}


	public void setRole(UserRole role) {
		this.role = role;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public boolean isLocked() {
		return locked;
	}


	public void setLocked(boolean locked) {
		this.locked = locked;
	}


	public Date getCreated() {
		return created;
	}
	
	
	public String getCreatedString() {
		return created!=null?created.toString():null;
	}


	public void setCreated(Date created) {
		this.created = created;
	}


	public WebUser() {
		// TODO Auto-generated constructor stub
	}

}
