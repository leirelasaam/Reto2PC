package server;

import server.eloradmin.socketIO.SocketIOModule;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.mindrot.jbcrypt.BCrypt;

public class Server {
	private static final String HOST_NAME = "0.0.0.0";
	private static final int PORT = 3000;

	public static void main(String[] args) {
		// Server configuration
		Configuration config = new Configuration();
		config.setHostname(HOST_NAME);
		config.setPort(PORT);

		System.out.println("Server listening on IP " + config.getHostname() + " and port " + config.getPort());

		// We start the server
		SocketIOServer server = new SocketIOServer(config);
		SocketIOModule module = new SocketIOModule(server);
		module.start();

		/*
		String pass = "$2y$12$.QNgk2SxqG2Eu7Npq/gC6O8LmE7tudL4T7NXAFWkEOGoKTWApM/VG";
		String raw = "1234";

		if (BCrypt.checkpw(raw, pass)) {
			System.out.println("Contraseña correcta.");
		} else {
			System.out.println("Contraseña incorrecta.");
		}
		*/

	}
}
