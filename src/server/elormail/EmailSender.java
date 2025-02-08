package server.elormail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import server.config.ServerConfig;

public class EmailSender {
	private String smtpHost;
	private String smtpPort;
	private String username;
	private String password;
	private static final Logger logger = Logger.getLogger(EmailSender.class);

	public EmailSender() {
		this.smtpHost = ServerConfig.SMTP_HOST;
		this.smtpPort = ServerConfig.SMTP_PORT;
		this.username = ServerConfig.SMTP_SENDER;
		this.password = ServerConfig.SMTP_PASS;
	}

	public void sendEmail(String toEmail, String subject, String body) throws MessagingException {
		// Indicar las propiedades del servidor de correo
		Properties properties = new Properties();
		// Habilitar SSL
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.socketFactory.port", smtpPort);
		
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);
		properties.put("mail.smtp.auth", "true");

		// Autenticarse
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
		
		// Crear el cuerpo del mensaje
		Multipart multipart = new MimeMultipart();
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(body, "text/html");
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);

		// Enviar el mensaje
		Transport.send(message);
		logger.info("Correo enviado correctamente a " + toEmail);
	}

}
