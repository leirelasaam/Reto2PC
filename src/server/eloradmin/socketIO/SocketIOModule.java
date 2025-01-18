package server.eloradmin.socketIO;

import java.net.HttpURLConnection;

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
				// Extraer email y password
				String email = messageJsonObject.get("email").getAsString();
				String password = messageJsonObject.get("password").getAsString();

				// Buscar el usuario por email
				UsersManager um = new UsersManager(sesion);
				User user = um.getByEmail(email);

				// No se ha encontrado usuario > 404 - NOT FOUND
				if (user == null) {
					client.sendEvent(Events.ON_LOGIN_ANSWER.value, DefaultMessages.NOT_FOUND);
					System.out.println("Sending: " + DefaultMessages.NOT_FOUND.toString());
				} else {
					// Se ha encontrado el usuario y la contraseña coincide > 200 - User
					if (BcryptUtils.verifyPassword(password, user.getPassword())) {
						UserDTO userDTO = new UserDTO(user);
						String answerMessage = gson.toJson(userDTO);
						MessageOutput messageOutput = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
						client.sendEvent(Events.ON_LOGIN_ANSWER.value, messageOutput);
						System.out.println("Sending: " + messageOutput.toString());
					// Se ha encontrado el usuario y la contraseña no coincide > 403 - FORBIDDEN
					} else {
						client.sendEvent(Events.ON_LOGIN_ANSWER.value, DefaultMessages.FORBIDDEN);
						System.out.println("Sending: " + DefaultMessages.FORBIDDEN.toString());
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
