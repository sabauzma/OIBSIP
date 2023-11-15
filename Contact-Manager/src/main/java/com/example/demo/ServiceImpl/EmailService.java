package com.example.demo.ServiceImpl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message, String to)
	{
		boolean f = false;
		//REST OF THE CODE	
		//variable for gmail
				String from = "mefarhan321@gmail.com";
				String host = "smtp.gmail.com";
				
				//get the system properties
				Properties properties = System.getProperties();
				System.out.println("Properties="+properties);
				
				//setting important information to properties object
				
				//setting host
				properties.put("mail.smtp.host", host);
				properties.put("mail.smtp.port", "465");
				properties.put("mail.smtp.ssl.enable", "true");
				properties.put("mail.smtp.auth", "true");
				
				//step1 !:To get the session object
				Session session = Session.getInstance(properties, new Authenticator() {
					
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("mefarhan321@gmail.com", "yuhe zxvs jwix urob");
					}
					
				});
				session.setDebug(true);
				
				//step2 : compose the message [text,multimedia]
				MimeMessage m = new MimeMessage(session);
				
				try {
				//from email(sender)
				m.setFrom(from);
				
				//to recipient(receiver)
				m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				
				//adding subject to message
				m.setSubject(subject);
				
				//adding text to message
				//m.setText(message);
				m.setContent(message, "text/html");
				
				//now sending
				//step3 : send the message using Transpot Class
				Transport.send(m);
				System.out.println("Email Sent Successfully...............");
				f = true;
				
				}catch (Exception e) {
					e.printStackTrace();
				}
				return f;
	}

}
