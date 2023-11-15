package com.example.demo.ServiceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public boolean findCustomer(User user) {
		Optional<User> userobj = userRepository.findByEmail(user.getEmail());
		
		if(userobj.isPresent())
		{
			User obj = (User)userobj.get();
			String DbPass = obj.getPassword();
			if(DbPass.equals(user.getPassword()))
			{
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

}


//@Override
//public boolean findCustomer(Customers customer) {
//	
//	Optional<Customers> customerobj = customerrepository.findById(customer.getEmail());
//	
//	if(customerobj.isPresent())
//	{
//		Customers obj = (Customers)customerobj.get();
//		String Dbpass = obj.getPassword();
//		if(Dbpass.equals(customer.getPassword()))
//		{
//			return true;
//		}
//		else 
//		{
//		return false;
//		}
//	}
//	return false;
//}