package com.example.demo.Service;

import org.springframework.stereotype.Service;

import com.example.demo.Entity.User;


@Service
public interface UserService {

	boolean findCustomer(User user);

	
}


