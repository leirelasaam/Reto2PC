package server.elormail;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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

	public void sendEmail(ArrayList<String> toEmails, String subject, String body, ArrayList<String> attachments)
			throws MessagingException, IllegalArgumentException {
		// Si algún campo necesario no se pasa, lanzar excepción que luego se controla en el SocketIOModule
		if (toEmails == null || (toEmails != null && toEmails.size() == 0) || subject == null || body == null) {
			throw new IllegalArgumentException("No se han añadido todos los parámetros necesarios para enviar el correo.");
		}

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
		message.setSubject(subject);

		// Añadir la posibilidad de enviar el correo a varios destinatarios
		// TO (destinatario), CC (copia) y BCC (copia oculta)
		if (toEmails != null) {
			for (String email : toEmails) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			}
		}

		// Crear el cuerpo del mensaje
		Multipart multipart = new MimeMultipart();
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(body, "text/html");
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);

		// Añadir adjuntos
		if (attachments != null && attachments.size() > 0) {
			for (String filePath : attachments) {
				try {
					MimeBodyPart attachmentPart = new MimeBodyPart();
					FileDataSource source = new FileDataSource(filePath);
					attachmentPart.setDataHandler(new DataHandler(source));
					attachmentPart.setFileName(new File(filePath).getName());
					multipart.addBodyPart(attachmentPart);
				} catch (Exception e) {
					// Si ocurre una excepción de FileNotFound o IOException, lanzar una excepción MessagingException
					throw new MessagingException("Error al añadir archivos adjuntos en el correo.");
				}
			}
		}

		// Enviar el mensaje
		Transport.send(message);
		logger.info("Correo enviado correctamente.");
	}

}
