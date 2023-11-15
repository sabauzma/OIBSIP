package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "contact")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "contactid", nullable = false)
	private int cid;
	
	private String cname;
	
	private String csecondName;
	
	private String cwork;
	
	private String cemail;
	
	private String cphone;
	
	@Size(min = 0,max = 255, message = "size must be between 0 and 255")
	private  String cimage;
	
	@Column(length = 8000)
	private String cdescription;
	
	@ManyToOne
	@JsonIgnore
	private User user;

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Contact(int cid, String cname, String csecondName, String cwork, String cemail, String cphone, String cimage,
			String cdescription, User user) {
		super();
		this.cid = cid;
		this.cname = cname;
		this.csecondName = csecondName;
		this.cwork = cwork;
		this.cemail = cemail;
		this.cphone = cphone;
		this.cimage = cimage;
		this.cdescription = cdescription;
		this.user = user;
	}

//	@Override
//	public String toString() {
//		return "Contact [cid=" + cid + ", cname=" + cname + ", csecondName=" + csecondName + ", cwork=" + cwork
//				+ ", cemail=" + cemail + ", cphone=" + cphone + ", cimage=" + cimage + ", cdescription=" + cdescription
//				+ ", user=" + user + "]";
//	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getCsecondName() {
		return csecondName;
	}

	public void setCsecondName(String csecondName) {
		this.csecondName = csecondName;
	}

	public String getCwork() {
		return cwork;
	}

	public void setCwork(String cwork) {
		this.cwork = cwork;
	}

	public String getCemail() {
		return cemail;
	}

	public void setCemail(String cemail) {
		this.cemail = cemail;
	}

	public String getCphone() {
		return cphone;
	}

	public void setCphone(String cphone) {
		this.cphone = cphone;
	}

	public String getCimage() {
		return cimage;
	}

	public void setCimage(String cimage) {
		this.cimage = cimage;
	}

	public String getCdescription() {
		return cdescription;
	}

	public void setCdescription(String cdescription) {
		this.cdescription = cdescription;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
