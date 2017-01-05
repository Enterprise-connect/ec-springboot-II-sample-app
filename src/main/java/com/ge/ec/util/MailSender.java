/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.util;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
@Component
public class MailSender {

	private JavaMailSenderImpl mailSender;
	
	public JavaMailSender getMailSender(){
		if(mailSender==null){
			mailSender = new JavaMailSenderImpl();
			Properties mailProperties = new Properties();
			mailProperties.put("mail.smtp.auth", false);
			mailProperties.put("mail.smtp.starttls.enable", false);
			mailSender.setJavaMailProperties(mailProperties);
			mailSender.setHost("localhost");
			mailSender.setPort(7990);
			mailSender.setProtocol("smtp");
			mailSender.setUsername("username");
			mailSender.setPassword("password");
		}
		return mailSender;
	}
	public MimeMessage sendMail(String to, String from, String subject, String text) throws MessagingException {    
		MimeMessage mimeMessage = getMailSender().createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
		helper.setTo(to);
		helper.setFrom(from);
		helper.setSubject(subject);
		mimeMessage.setContent(text, "text/html");
		mailSender.send(mimeMessage);
		return mimeMessage;
	}
}
