package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.Contact;
import com.example.demo.Entity.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	//pegination.....
	
	@Query("from Contact as c where c.user.userId =:userId")
	public List<Contact> findContactsByUser(@Param("userId")int userId);

	 @Query("from Contact as c where c.user.userId = :userId")
	    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	 
	 //search
	 List<Contact> findByCnameContainingAndUser(String cname, User user);
}
