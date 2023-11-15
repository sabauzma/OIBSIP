package com.example.demo.Service;

import org.springframework.stereotype.Service;

import com.example.demo.Entity.Contact;


@Service
public interface ContactService {
	void addContact(Contact contact);


}
