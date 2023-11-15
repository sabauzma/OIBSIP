package com.example.demo.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="user")
public class User {

	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	@Column(name = "userid", nullable = false)
	private int userId;
	
	@NotBlank(message =  "Name Should not be empty..!")
	@Size(min = 3, max = 25, message =  "User Name Must Be in Between 3 - 25 Characters..")
	@Column(name = "username", nullable = false)
	private String userName;
	
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")                                         
	@Column(name = "email", unique = true)
	private String email;
	

	@Column(name = "password")
	private String password;
	
	@Column(name = "userrole")
	private String userRole;
	
	@Column(name = "userenabled")
	private boolean userEnabled;
	
	@Column(name = "userimage")
	private String userImage;
	
	@Column(name = "userabout")
	private String userAbout;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
	private List<Contact> contacts = new ArrayList<>();


	public User() {
		super();
	}


	public User(int userId,
			@NotBlank(message = "Name Should not be empty..!") @Size(min = 3, max = 25, message = "User Name Must Be in Between 3 - 25 Characters..") String userName,
			@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$") String email, String password, String userRole,
			boolean userEnabled, String userImage, String userAbout, List<Contact> contacts) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.userRole = userRole;
		this.userEnabled = userEnabled;
		this.userImage = userImage;
		this.userAbout = userAbout;
		this.contacts = contacts;
	}


	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", email=" + email + ", password=" + password
				+ ", userRole=" + userRole + ", userEnabled=" + userEnabled + ", userImage=" + userImage
				+ ", userAbout=" + userAbout + ", contacts=" + contacts + "]";
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUserRole() {
		return userRole;
	}


	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}


	public boolean isUserEnabled() {
		return userEnabled;
	}


	public void setUserEnabled(boolean userEnabled) {
		this.userEnabled = userEnabled;
	}


	public String getUserImage() {
		return userImage;
	}


	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}


	public String getUserAbout() {
		return userAbout;
	}


	public void setUserAbout(String userAbout) {
		this.userAbout = userAbout;
	}


	public List<Contact> getContacts() {
		return contacts;
	}


	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
}
