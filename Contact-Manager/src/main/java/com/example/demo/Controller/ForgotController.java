package com.example.demo.Controller;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.ServiceImpl.EmailService;

import jakarta.servlet.http.HttpSession;

@RestController
public class ForgotController {
	Random random = new Random(1000);
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/forgot")
	public ModelAndView openEmailForm()
	{
		return new ModelAndView("forgotemailform");
	}
	
	@PostMapping("/send-otp")
	public ModelAndView sendOtp(@RequestParam("email") String email, HttpSession session) 
	{
		System.out.println("Email-Id="+email);
		
		//Generating otp of 4 digit
		int otp = random.nextInt(99999999);
		System.out.println("OTP="+otp);
		
		//write code for send otp to email...\
		
		String subject = "OTP From Smart-Contact-Manager";
		String message = ""
					+ "<div style='border:1px solid #e2e2e2; padding:20px'>"
					+ "<h1>"
					+ "OTP is "
					+ "<b>"+ otp
					+ "</b>"
					+ "</h1>"
					+ "</div>";
		String to = email;
		
		boolean flag = emailService.sendEmail(subject, message, to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return new ModelAndView("verifyotp");
		}else
		{
			session.setAttribute("message", "Your Email-Id is Not Valid!!");
			return new ModelAndView("forgotemailform");			
		}
	}
	
	//verify otp
	@PostMapping("/verifyotp")
	public ModelAndView verifyotp(@RequestParam("otp") String otpString, HttpSession session )
	{
	    Integer myOtp = (Integer) session.getAttribute("myotp");
	    String email = (String) session.getAttribute("email");

	    try {
	        Integer otp = Integer.parseInt(otpString);

	        if (myOtp.equals(otp)) {
	            // OTP is correct, now check if the user exists
	            Optional<User> userOptional = userRepository.findByEmail(email);
	            if (!userOptional.isPresent()) {
	                // User does not exist, send error message
	                session.setAttribute("message", "User Does Not Exist With This Email!!");
	                return new ModelAndView("forgotemailform");
	            } else {
	                // User exists, send password change form
	                return new ModelAndView("passwordchangeform");
	            }
	        } 
	        else {
	            session.setAttribute("message", "You Have Entered Wrong OTP");
	            return new ModelAndView("verifyotp");
	        }
	    } catch (NumberFormatException e) {
	        session.setAttribute("message", "Invalid OTP Format");
	        return new ModelAndView("verifyotp");
	    }
	}
	
	//change-password
	
	// change-password
    @PostMapping("/changepassword2")
    public ModelAndView changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newpassword);
            userRepository.save(user);

            return new ModelAndView("redirect:/login?change=Password Changed Successfully");
        } else {
            session.setAttribute("message", "User Not Found");
            return new ModelAndView("forgotemailform");
        }
    }

}
