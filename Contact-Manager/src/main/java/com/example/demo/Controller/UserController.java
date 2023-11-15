package com.example.demo.Controller;

import com.razorpay.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Entity.Contact;
import com.example.demo.Entity.User;
import com.example.demo.Helper.Message;
import com.example.demo.Repository.ContactRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.ContactService;
import com.example.demo.Service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@RestController
@SessionAttributes("userobj")
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository; 
	
	@Autowired
	UserService userService;
	
	@Autowired
	ContactService contactService;
	
	
	@GetMapping("/Home")
	public ModelAndView Home() {
		return new ModelAndView("Home");
	}
	
	@GetMapping("/about")
	public ModelAndView about() {
		return new ModelAndView("about");
	}
	
	@GetMapping("/AboutPage1")
	public ModelAndView AboutPage() {
		return new ModelAndView("AboutPage");
	}
	
	@GetMapping("/index")
	 public ModelAndView index(Model model)
	 {
		 return new ModelAndView("index");
	 }
	 
	
	@PostMapping(value = "/userLogin")
	public ResponseEntity<Object> userLogin(@RequestBody User user)
	{
		boolean flag = userService.findCustomer(user);
		
		return new ResponseEntity<Object>(flag, HttpStatus.OK);
	}
	
	@PostMapping(value = "/addContacts")
	public ResponseEntity<Object> addProduct(@RequestBody Contact contact,HttpSession session){

		contactService.addContact(contact);
		return new ResponseEntity<Object>("Contact Added Successfully" ,HttpStatus.OK);
	}
	
	
	@GetMapping("/logout")
	public ModelAndView logout(HttpSession session) {
	    try {
	        // Invalidate the user's session
	        session.invalidate();
	        
	        // Redirect to the login page
	        return new ModelAndView("redirect:/login");
	    } catch (Exception e) {
	        // Handle any exceptions that might occur
	        e.printStackTrace();
	        // Redirect to the login page in case of error
	        return new ModelAndView("redirect:/login");
	    }
	}



	
	@GetMapping("/login")
	public ModelAndView login(Model model)
	{
		User user = new User();
		model.addAttribute("user", user);
		return new ModelAndView("login");
	}
	
	@PostMapping("/loginsuccess")
	public ModelAndView Loginsuccess(@ModelAttribute("user") User user, Model model, HttpSession session)
	{
		System.out.println("User="+user);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<User> entity = new HttpEntity<User>(user , headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Boolean> response = restTemplate.exchange("http://localhost:8091/userLogin", HttpMethod.POST, entity , boolean.class);
		boolean responseString = response.getBody();
		if(responseString == false)
		{
			return new ModelAndView("redirect:/login");
		}
		else {
		
		//get User Using Email
		Optional<User> userobj = userRepository.findByEmail(user.getEmail());
	    if (userobj.isPresent()) {
	        session.setAttribute("userobj", userobj.get());
	        System.out.println("User Details=" + userobj.get());
	       
	    } else {
	        System.out.println("User not found in the database.");
	    }System.out.println("User Details="+userobj);
		
		model.addAttribute("user" , userobj);
		return new ModelAndView("userdashboard");
		}
	}
	

	@GetMapping("/addcontact")
    public ModelAndView showAddContactForm(HttpSession session, Model model) {
        try {
            User userobj = (User) session.getAttribute("userobj");
            if (userobj == null) {
                // Handle user not logged in
                return new ModelAndView("login");
            }

            Contact contact = new Contact();
            model.addAttribute("contact", contact);

            return new ModelAndView("addcontactform");
        } catch (Exception e) {
            e.printStackTrace();
            return new ModelAndView("redirect:/login");
        }
    }
	
	
	 @PostMapping("/processcontact")
	    public ModelAndView processContact(@ModelAttribute("contact") Contact contact,
	            @RequestParam("cimage1") MultipartFile file, Model model, HttpSession session) {
	        try {
	            Random rnd = new Random();
	            String filename = rnd.nextInt() + file.getOriginalFilename();
	            File fileName;
	            try {
	                fileName = new ClassPathResource("/").getFile();

	                String filePath = fileName.getPath().toString();
	                int index = filePath.indexOf("\\target");
	                filePath = filePath.substring(0, index);
	                filePath += "/src/main/resources/static/uploads";

	                Path uploadPath = Paths.get(filePath);
	                if (!Files.exists(uploadPath)) {
	                    Files.createDirectories(uploadPath);
	                    System.out.println("\nPath Created..");
	                } else {
	                    System.out.println("\nPath Exists..");
	                }
	                try {
	                    byte[] bytes = file.getBytes();
	                    Path path = Paths.get(filePath + "/" + filename);
	                    Files.write(path, bytes);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                contact.setCimage(filename);
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }

	            User userobj = (User) session.getAttribute("userobj");
	            Optional<User> userOptional = userRepository.findByEmail(userobj.getEmail());
	            if (userOptional.isPresent()) {
	                User user = userOptional.get();
	                contact.setUser(user);
	                user.getContacts().add(contact);
	                userRepository.save(user);

	                // Set success message
	                session.setAttribute("message", new Message("Your Contact Is Added!!  Add More..", "success"));
	            } else {
	                // Set error message
	                session.setAttribute("message", new Message("Something Went Wrong, Try Again", "danger"));
	                System.out.println("Data Not Added.....");
	            }
	            contact = new Contact();
	            model.addAttribute("contact", contact);
	            ModelAndView view = new ModelAndView("addcontactform");
	            return view;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ModelAndView("redirect:/login");
	        }
	    }
	
	//show contacts handler
//	 @GetMapping("/showcontacts")
//	 public ModelAndView showContacts(Model model,HttpSession session)
//	 {
//		 User userobj = (User) session.getAttribute("userobj");
//		 Optional<User> userOptional = userRepository.findByEmail(userobj.getEmail());
//		 if (userOptional.isPresent())  {  
//			 User user = userOptional.get();
//			 List<Contact> contacts = contactRepository.findContactsByUser(user.getUserId());
//			 model.addAttribute("contacts", contacts);
//			 return new ModelAndView("showcontacts");
//		 }else {
//			 System.out.println("No Contacts.....");
//		 }
//		return new ModelAndView("showcontacts");		 
//	 }
	 
	 
	 //Showing Contacts With Pagination
	 @GetMapping("/showcontacts")
	 public ModelAndView showContacts(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page) {
	     try {
	         User userobj = (User) session.getAttribute("userobj");
	         Optional<User> userOptional = userRepository.findByEmail(userobj.getEmail());

	         if (userOptional.isPresent()) {
	             User user = userOptional.get();
	             Pageable pageable = PageRequest.of(page, 5); // 5 contacts per page
	             Page<Contact> contactPage = contactRepository.findContactsByUser(user.getUserId(), pageable);

	             if (contactPage.isEmpty() && page > 0) {
	                 // If no contacts and the user tries to navigate to a non-existent page,
	                 // redirect to the first page
	                 return new ModelAndView("redirect:/showcontacts");
	             }

	             model.addAttribute("contacts", contactPage.getContent());
	             model.addAttribute("currentPage", page);
	             model.addAttribute("totalPages", contactPage.getTotalPages());

	             return new ModelAndView("showcontacts");
	         } else {
	             System.out.println("No Contacts.....");
	             return new ModelAndView("redirect:/login");
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         return new ModelAndView("redirect:/showcontacts");
	     }
	 }
	 
	 //Showing Particular Contact Detail...
	 @GetMapping("/contact/{cid}")
	    public ModelAndView showParticularObject(@PathVariable("cid") int cid, HttpSession session, Model model) {
	        try {
	            User userobj = (User) session.getAttribute("userobj");
	            model.addAttribute("user", userobj);
	            System.out.println("cid=" + cid);

	            Optional<Contact> contactOptional = contactRepository.findById(cid);
	            Contact contact = contactOptional.get();

	            // Getting authorized user; only user can see their own contacts
	            Optional<User> userOptional = userRepository.findByEmail(userobj.getEmail());
	            if (userOptional.isPresent()) {
	                User user = userOptional.get();

	                if (user.getUserId() == contact.getUser().getUserId())
	                    model.addAttribute("contact", contact);
	            }

	            return new ModelAndView("contactdetail");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ModelAndView("redirect:/login");
	        }
	    }

		//delete contact handler
	 @GetMapping("/delete/{cid}")
	    public ModelAndView deleteContact(@PathVariable("cid") int cid, HttpSession session) {
	        try {
	            Optional<Contact> contactOptional = contactRepository.findById(cid);
	            Contact contact = contactOptional.get();

	            contactRepository.deleteById(contact.getCid());
	            session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
	            return new ModelAndView("redirect:/showcontacts");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ModelAndView("redirect:/login");
	        }
	    }
   
		//open contact-update form handler
	   @GetMapping("/updatecontact/{cid}")
	    public ModelAndView updateForm(@PathVariable("cid") int cid, Model model,HttpSession session ) {
	        try {
	            Optional<Contact> contactOptional = contactRepository.findById(cid);
	            if (contactOptional.isPresent()) {
	                Contact contact = contactOptional.get();
	                model.addAttribute("contact", contact);
	                return new ModelAndView("updateform");
	            } else {
	                // Handle contact not found
	                session.setAttribute("message", new Message("Contact Not Found", "danger"));
	                return new ModelAndView("redirect:/showcontacts");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ModelAndView("redirect:/login");
	        }
	    }
	 
		
		
//		//open contact-update form handler
	   @PostMapping("/processupdate")
	    public ModelAndView processUpdate(@ModelAttribute("contact") Contact contact,
	            @RequestParam("cimage1") MultipartFile file, Model model, HttpSession session) {
	        try {
	            Random rnd = new Random();
	            String filename = rnd.nextInt() + file.getOriginalFilename();
	            
	            File fileName;
	            try {
	                fileName = new ClassPathResource("/").getFile();

	                String filePath = fileName.getPath().toString();
	                int index = filePath.indexOf("\\target");
	                System.out.println("index=" + index);
	                filePath = filePath.substring(0, index);
	                filePath += "/src/main/resources/static/uploads";

	                Path uploadPath = Paths.get(filePath);
	                if (!Files.exists(uploadPath)) {
	                    try {
	                        Files.createDirectories(uploadPath);
	                        System.out.println("\nPath Created..");
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                } else {
	                    System.out.println("\nPath Exists..");
	                }

	                try {
	                    byte[] bytes = file.getBytes();
	                    Path path = Paths.get(filePath + "/" + filename);
	                    Files.write(path, bytes);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                contact.setCimage(filename);

	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }

	            try {
	                Optional<Contact> oldContactDetails = contactRepository.findById(contact.getCid());
	                if (oldContactDetails.isPresent()) {
	                    Contact con = oldContactDetails.get();
	                }

	                if (!file.isEmpty()) {
	                    // file rewrite
	                } else {
	                    contact.setCimage(contact.getCimage());
	                }

	                User userobj = (User) session.getAttribute("userobj");
	                model.addAttribute("user", userobj);
	                Optional<User> userOptional = userRepository.findByEmail(userobj.getEmail());
	                if (userOptional.isPresent()) {
	                    User user = userOptional.get();
	                    contact.setUser(user);
	                    contactRepository.save(contact);
	                }

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            
	            System.out.println("Contact=" + contact.getCname());
	            System.out.println("Contact=" + contact.getCid());
	            return new ModelAndView("redirect:/showcontacts");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ModelAndView("redirect:/login");
	        }
	    }

	   @GetMapping("/signup")
	    public ModelAndView signup(Model model) {
	        try {
	            User user = new User();
	            model.addAttribute(user);
	            return new ModelAndView("signup");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ModelAndView("redirect:/login");
	        }
	    }

	
	   @PostMapping("/doregister")
	   public ModelAndView signUpUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
	           @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) {
	       try {
	           if (!agreement) {
	               System.out.println("You have not Agreed terms and conditions");
	               throw new Exception("You have not Agreed terms and conditions");
	           }

	           if (result1.hasErrors()) {
	               System.out.println("Error=" + result1.toString());
	               model.addAttribute("user", user);
	               return new ModelAndView("signup");
	           }

	           user.setUserRole("ROLE_USER");
	           user.setUserEnabled(true);
	           user.setUserImage("default.png");

	           try {
	               User result = userRepository.save(user);
	               model.addAttribute("user", new User());
	               session.setAttribute("message", new Message("Successfully Signed-Up!!", "alert-success"));
	               return new ModelAndView("signup");
	           } catch (DataIntegrityViolationException ex) {
	               // Duplicate entry error handling
	               ex.printStackTrace();
	               model.addAttribute("user", user);
	               session.setAttribute("message", new Message("Email already exists. Please choose a different email.", "alert-danger"));
	               return new ModelAndView("signup");
	           }

	       } catch (Exception e) {
	           e.printStackTrace();
	           model.addAttribute("user", user);
	           session.setAttribute("message", new Message("Something Went Wrong!!" + e.getMessage(), "alert-danger"));
	           return new ModelAndView("redirect:/signup");
	       }
	   }

	
	//Your Profile Handler

	    @GetMapping("/profile")
	    public ModelAndView yourProfile(Model model, HttpSession session) {
	        try {
	            User userobj = (User) session.getAttribute("userobj");
	            if (userobj == null) {
	                // Handle user not logged in
	                // You can redirect or show an error page
	                return new ModelAndView("login");
	            }

	            model.addAttribute("user", userobj);
	            return new ModelAndView("profile");
	        } catch (Exception e) {
	            e.printStackTrace();
	            // Handle the error here, such as showing an error message to the user
	            return new ModelAndView("redirect:/login"); // You can customize the error page
	        }
	    }
	
	    @GetMapping("/userdashboard")
	    public ModelAndView userdashboard(Model model, HttpSession session) {
	        try {
	            User userobj = (User) session.getAttribute("userobj");
	            if (userobj == null) {
	                // Handle user not logged in
	                // You can redirect or show an error page
	                return new ModelAndView("login");
	            }

	            model.addAttribute("user", userobj);

	            // Create a ModelAndView object and specify the view name (Thymeleaf template)
	            ModelAndView modelAndView = new ModelAndView("userdashboard");

	            // You can add more data to the model if needed
	            // modelAndView.addObject("otherAttribute", otherValue);

	            return modelAndView;
	        } catch (Exception e) {
	            e.printStackTrace();
	            // Handle the error here, such as showing an error message to the user
	            return new ModelAndView("redirect:/login"); // You can customize the error page
	        }
	    }
	    @GetMapping("/userdashboard1")
	    public ModelAndView userdashboard1(Model model, HttpSession session) {
	        try {
	            User userobj = (User) session.getAttribute("userobj");
	            if (userobj == null) {
	                // Handle user not logged in
	                // You can redirect or show an error page
	                return new ModelAndView("login");
	            }

	            model.addAttribute("user", userobj);

	            // Create a ModelAndView object and specify the view name (Thymeleaf template)
	            ModelAndView view = new ModelAndView("userdashboard1");

	            // You can add more data to the model if needed
	            // modelAndView.addObject("otherAttribute", otherValue);

	            return view;
	        } catch (Exception e) {
	            e.printStackTrace();
	            // Handle the error here, such as showing an error message to the user
	            return new ModelAndView("redirect:/login"); // You can customize the error page
	        }
	    }

//Search-handler
	    @GetMapping("/search/{query}")
	    public ResponseEntity<Object> search(@PathVariable("query") String query, HttpSession session) {
	        try {
	            User userobj = (User) session.getAttribute("userobj");
	            if (userobj == null) {
	                // Handle user not logged in
	                // You can redirect or show an error response
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
	            }

	            Optional<User> userOptional = userRepository.findByEmail(userobj.getEmail());

	            if (userOptional.isPresent()) {
	                User user = userOptional.get();

	                // Use the proper entity class and method to search for contacts
	                List<Contact> contacts = contactRepository.findByCnameContainingAndUser(query, user);

	                if (!contacts.isEmpty()) {
	                    return ResponseEntity.ok(contacts);
	                } else {
	                    return ResponseEntity.notFound().build(); // Return a not found response
	                }
	            } else {
	                return ResponseEntity.badRequest().body("User not found");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            // Handle the error here, such as logging the exception or showing an error message
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
	        }
	    }
	
	//open setting handler
	    @GetMapping("/settings")
	    public ModelAndView openSettings() {
	        try {
	            // Your existing code here
	            return new ModelAndView("settings");
	        } catch (Exception e) {
	            // Handle any exceptions that might occur
	            e.printStackTrace();
	            // Redirect to the login page in case of error
	            return new ModelAndView("redirect:/login");
	        }
	    }

	    //changepassword
	    @PostMapping("/changepassword")
	    public ModelAndView changePassword(
	        @RequestParam("oldpassword") String oldpassword,
	        @RequestParam("newpassword") String newpassword,
	        HttpSession session) 
	    {
	        try {
	            User userobj = (User) session.getAttribute("userobj");
	            Optional<User> userOptional = userRepository.findByEmail(userobj.getEmail());

	            if (userOptional.isPresent()) {
	                User currentUser = userOptional.get();

	                // Compare old password (without hashing)
	                if (oldpassword.equals(currentUser.getPassword())) {
	                    // Change the password (without hashing)
	                    currentUser.setPassword(newpassword);
	                    userRepository.save(currentUser);
	                    session.setAttribute("message", new Message("Your Password is successfully changed", "success"));
	                } else {
	                    // Error: Wrong old password
	                    session.setAttribute("message", new Message("Wrong old Password", "danger"));
	                }
	            } else {
	                System.out.println("User Not Found");
	            }
	        } catch (Exception e) {
	            // Handle any exceptions that might occur
	            e.printStackTrace();
	            session.setAttribute("message", new Message("An error occurred while changing your password", "danger"));
		        return new ModelAndView("redirect:/login");

	        }

	        return new ModelAndView("redirect:/settings");
	    }
	    
	    @PostMapping("/createorder")
	    @ResponseBody
	    public String createOrder(@RequestBody Map<String, Object> data) throws Exception
	    {
	        System.out.println(data);

	        int amt = Integer.parseInt(data.get("amount").toString());

	        var client = new RazorpayClient("rzp_test_ss2ub5A5qePEy6", "2ENdh9DTGADzr8oLf2nAnvyl");

	        try {
	            JSONObject ob = new JSONObject();
	            ob.put("amount", amt * 100); // amount in the smallest currency unit
	            ob.put("currency", "INR");
	            ob.put("receipt", "order_rcptid_11");

	            // Creating a new order
	            Order order = client.orders.create(ob);
	            System.out.println(order);

	            // You might want to save this order data to your database...
	            return order.toString();
	        } catch (RazorpayException e) {
	            // Handle Exception
	            System.out.println(e.getMessage());
	        }

	        return "Error creating order";
	    }
	}
	    
//	    // Utility method to hash a password (you can use your preferred password hashing library)
//	    private String hashPassword(String password) {
//	        // Hash the password using a secure hashing algorithm (e.g., BCrypt)
//	        // Example using BCryptPasswordEncoder from Spring Security
//	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//	        return passwordEncoder.encode(password);
//	    }


//@GetMapping("/addcontact")
//public ModelAndView addContact(Model model, HttpSession session) {
//    User userobj = (User) session.getAttribute("userobj");
//    if (userobj == null) {
//        System.out.println("Userobj is null. Redirecting to login.");
//        return new ModelAndView("redirect:/login");
//    }
//    session.setAttribute("userobj", userobj);
//    System.out.println("Mohammed Farhan Ahmed="+userobj);
//    model.addAttribute("userobj", userobj);
//    
//    Contact contact = new Contact();
//    model.addAttribute("contact",contact);
//    
//    return new ModelAndView("addcontactform");
//    
//} 






//HttpHeaders headers = new HttpHeaders();
//headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//HttpEntity<Contact> entity = new HttpEntity<Contact>(contact , headers);
//RestTemplate restTemplate = new RestTemplate();
//ResponseEntity<String> response = restTemplate.exchange("http://localhost:8091/addContacts", HttpMethod.POST, entity , String.class);
//String responseString = response.getBody();