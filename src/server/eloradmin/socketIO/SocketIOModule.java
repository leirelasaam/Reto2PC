package server.eloradmin.socketIO;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.JsonObject;

import server.eloradmin.config.Events;
import server.eloradmin.model.MessageInput;
import server.eloradmin.model.MessageOutput;
import server.elorbase.managers.UsersManager;
import server.elorbase.model.Users;
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

			// The JSON message from MessageInput
			String message = data.getMessage();
			System.out.println("Server received: " + data.getMessage());

			// We parse the answer into JSON
			try {
				// We parse the JSON into an JsonObject
				// The JSON should be something like this: {"message": "patata"}
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
				String email = jsonObject.get("message").getAsString();

				UsersManager um = new UsersManager(sesion);
				Users user = um.getByEmail(email);
				
				if(user == null)
					client.sendEvent(Events.ON_LOGIN_ERROR_NO_EMAIL.value, new MessageOutput("No existe un usuario con el correo " + email + "."));
					
				String answerMessage = gson.toJson(user);
				// ... and we send it back to the client inside a MessageOutput
				MessageOutput messageOutput = new MessageOutput(answerMessage);
				client.sendEvent(Events.ON_LOGIN_ANSWER.value, messageOutput);
			} catch (Exception e) {
				System.out.println("Error: " + e);
				client.sendEvent(Events.ON_LOGIN_ERROR.value, new MessageOutput("No se ha podido parsear el JSON."));
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
