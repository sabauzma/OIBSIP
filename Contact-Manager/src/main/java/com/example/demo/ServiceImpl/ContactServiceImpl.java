package com.example.demo.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Contact;
import com.example.demo.Repository.ContactRepository;
import com.example.demo.Service.ContactService;

@Service
public class ContactServiceImpl implements ContactService {
	
	@Autowired
	ContactRepository contactRepository;
	
	@Override
	public void addContact(Contact contact) {
		contactRepository.save(contact);
		
	}

}
