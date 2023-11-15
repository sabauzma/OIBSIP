package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.User;

public interface UserRepository  extends JpaRepository<User, Integer> {
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public Optional<User> findByEmail(@Param("email") String email);
	
//	public User findByEmail(@Param("email") String email);
}	
	
//	String hql = "SELECT u FROM User u WHERE u.email = :email";

