package com.app.projectory.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Users implements Serializable{

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String username;
	private String password;
	private String loginIndicator;
	
	@JsonIgnore
	@OneToMany(mappedBy = "userWithConnection")
	List<Connections> connection;
	
	
	//for indicating projects owned(created) by the users
	@JsonIgnore
	@OneToMany(mappedBy = "projectOwner")
	private List<Project> ownedProjects;
	
	//for indicating projects the user has joined(is a member of)
	@JsonIgnore
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
	@JoinTable(name = "project_members", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
	private List<Project> joinedProjects;
	
	public Users() {
			
	}
	public Users(String username, String password, String firstName, String lastName, String emailAddress) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	
	
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}	
	
	public List<Project> getOwnedProjects() {
		return ownedProjects;
	}
	public void setOwnedProjects(List<Project> ownedProjects) {
		this.ownedProjects = ownedProjects;
	}
	public List<Project> getJoinedProjects() {
		return joinedProjects;
	}
	public void setJoinedProjects(List<Project> joinedProjects) {
		this.joinedProjects = joinedProjects;
	}
	
	public String getLoginIndicator() {
		return loginIndicator;
	}
	public void setLoginIndicator(String loginIndicator) {
		this.loginIndicator = loginIndicator;
	}
	public List<Connections> getConnection() {
		return connection;
	}
	public void setConnection(List<Connections> connection) {
		this.connection = connection;
	}
	//convinience method
	public void addUser() {
		
	}
	
	@Override
	public String toString() {
		return getFirstName() +" "+getLastName();
	}
	
		
}
