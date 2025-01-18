package server.eloradmin.socketIO;

import java.net.HttpURLConnection;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.JsonObject;

import server.eloradmin.config.Events;
import server.eloradmin.model.DefaultMessages;
import server.eloradmin.model.MessageInput;
import server.eloradmin.model.MessageOutput;
import server.elorbase.managers.UsersManager;
import server.elorbase.dtos.UserDTO;
import server.elorbase.entities.User;
import server.elorbase.utils.BcryptUtils;
import server.elorbase.utils.HibernateUtil;
import server.elormail.EmailSender;

import com.google.gson.Gson;

/**
 * Server control main configuration class
 */
public class SocketIOModule {

	// The server
	private SocketIOServer server = null;
	private SessionFactory sesion = null;

	public SocketIOModule(SocketIOServer server) {
		super();
		this.server = server;
		this.sesion = HibernateUtil.getSessionFactory();

		// Default events (for control the connection of clients)
		server.addConnectListener(onConnect());
		server.addDisconnectListener(onDisconnect());

		// Custom events
		server.addEventListener(Events.ON_LOGIN.value, MessageInput.class, this.login());
		server.addEventListener(Events.ON_LOGOUT.value, MessageInput.class, this.logout());
		server.addEventListener(Events.ON_RESET_PASS_EMAIL.value, MessageInput.class, this.sendResetPassEmail());
	}

	// Default events

	private ConnectListener onConnect() {
		return (client -> {
			client.joinRoom("default-room");
			System.out.println("New connection, Client: " + client.getRemoteAddress());
		});
	}

	private DisconnectListener onDisconnect() {
		return (client -> {
			client.leaveRoom("default-room");
			System.out.println(client.getRemoteAddress() + " disconected from server");
		});
	}

	// Custom events

	private DataListener<MessageInput> login() {
		return ((client, data, ackSender) -> {
			System.out.println("Client from " + client.getRemoteAddress() + " wants to login");

			try {
				String clientMsg = data.getMessage();
				System.out.println("Server received: " + data.getMessage());

				/*
				 * Ejemplo de lo que nos llega: { "message": { "email": "user@example.com",
				 * "password": "1234" } }
				 */
				Gson gson = new Gson();
				// Extraer el JSON
				JsonObject jsonObject = gson.fromJson(clientMsg, JsonObject.class);
				// Extraer el message
				String messageString = jsonObject.get("message").getAsString();
				// Extraer el JSON dentro de message
				JsonObject messageJsonObject = gson.fromJson(messageString, JsonObject.class);
				// Extraer login y password
				String login = messageJsonObject.get("login").getAsString();
				String password = messageJsonObject.get("password").getAsString();

				// Buscar el usuario por email
				UsersManager um = new UsersManager(sesion);
				User user = um.getByEmailOrPin(login);

				// No se ha encontrado usuario > 404 - NOT FOUND
				if (user == null) {
					client.sendEvent(Events.ON_LOGIN_ANSWER.value, DefaultMessages.NOT_FOUND);
					System.out.println("Sending: " + DefaultMessages.NOT_FOUND.toString());
				} else {
					if (BcryptUtils.verifyPassword(password, user.getPassword())) {
						UserDTO userDTO = new UserDTO(user);
						String answerMessage = gson.toJson(userDTO);
						// Se ha encontrado el usuario, la contraseña coincide y ya está registrado >
						// 200 - User
						if (user.isRegistered()) {
							MessageOutput messageOutput = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
							client.sendEvent(Events.ON_LOGIN_ANSWER.value, messageOutput);
							System.out.println("Sending: " + messageOutput.toString());
							// Se ha encontrado el usuario, la contraseña coincide y no está registrado >
							// 403 - User
						} else {
							MessageOutput messageOutput = new MessageOutput(HttpURLConnection.HTTP_FORBIDDEN,
									answerMessage);
							client.sendEvent(Events.ON_LOGIN_ANSWER.value, messageOutput);
							System.out.println("Sending: " + messageOutput.toString());
						}
						// Se ha encontrado el usuario y la contraseña no coincide > 401 - UNAUTHORIZEDs
					} else {
						client.sendEvent(Events.ON_LOGIN_ANSWER.value, DefaultMessages.UNAUTHORIZED);
						System.out.println("Sending: " + DefaultMessages.UNAUTHORIZED.toString());
					}
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				client.sendEvent(Events.ON_LOGIN_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
				System.out.println("Sending: " + DefaultMessages.INTERNAL_SERVER.toString());
			}

		});
	}

	private DataListener<MessageInput> logout() {
		return ((client, data, ackSender) -> {
			// This time, we simply write the message in data
			System.out.println("Client from " + client.getRemoteAddress() + " wants to logout");

			// The JSON message from MessageInput
			String message = data.getMessage();

			// We parse the JSON into an JsonObject
			// The JSON should be something like this: {"message": "patata"}
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
			String userName = jsonObject.get("message").getAsString();

			// We do something on dataBase? ¯_(ツ)_/¯

			System.out.println(userName + " loged out");
		});
	}

	private DataListener<MessageInput> sendResetPassEmail() {
		return ((client, data, ackSender) -> {
			System.out.println("Client from " + client.getRemoteAddress() + " wants to reset password");

			try {
				String clientMsg = data.getMessage();
				System.out.println("Server received: " + data.getMessage());

				/*
				 * Ejemplo de lo que nos llega: { "message": "ejemplo@usuario.com"}
				 */
				Gson gson = new Gson();
				// Extraer el JSON
				JsonObject jsonObject = gson.fromJson(clientMsg, JsonObject.class);
				// Extraer el message
				String login = jsonObject.get("message").getAsString();
				
				// Buscar el usuario por email
				UsersManager um = new UsersManager(sesion);
				User user = um.getByEmailOrPin(login);
				
				if (user != null) {
					EmailSender es = new EmailSender();
					@SuppressWarnings("deprecation")
					String password = RandomStringUtils.randomAlphanumeric(10);
					um.updatePasswordByUser(user, password);
					es.sendEmail(user.getEmail(), "Nueva contraseña", "Contraseña nueva: " + password);
					client.sendEvent(Events.ON_RESET_PASS_EMAIL_ANSWER.value, DefaultMessages.OK);
					System.out.println("Sending: " + DefaultMessages.OK.toString());
				} else {
					client.sendEvent(Events.ON_RESET_PASS_EMAIL_ANSWER.value, DefaultMessages.NOT_FOUND);
					System.out.println("Sending: " + DefaultMessages.NOT_FOUND.toString());
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				client.sendEvent(Events.ON_RESET_PASS_EMAIL_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
				System.out.println("Sending: " + DefaultMessages.INTERNAL_SERVER.toString());
			}
		});
	}

	// Server control
	public void start() {
		server.start();
		System.out.println("Server started...");
	}

	public void stop() {
		server.stop();
		// Cerrar la sesión bbdd
		sesion.close();
		System.out.println("Server stopped");
	}
}
