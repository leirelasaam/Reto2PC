package server.elormail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import server.ServerConfig;

public class EmailSender {
	private String smtpHost;
	private String smtpPort;
	private String username;
	private String password;

	public EmailSender() {
		this.smtpHost = ServerConfig.SMTP_HOST;
		this.smtpPort = ServerConfig.SMTP_PORT;
		this.username = ServerConfig.SMTP_SENDER;
		this.password = ServerConfig.SMTP_PASS;
	}

	public void sendEmail(String toEmail, String subject, String body) throws MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		// Crear el mensaje
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		message.setSubject(subject);
		message.setText(body);

		Transport.send(message);
		System.out.println("Correo enviado correctamente a " + toEmail);
	}

}
