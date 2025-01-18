package server;

import server.eloradmin.socketIO.SocketIOModule;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class Server {
	private static final String HOST_NAME = "0.0.0.0";
	private static final int PORT = 3000;

	public static void main(String[] args) {
		// Server configuration
		Configuration config = new Configuration();
		config.setHostname(HOST_NAME);
		config.setPort(PORT);

		// We start the server
		SocketIOServer server = new SocketIOServer(config);
		SocketIOModule module = new SocketIOModule(server);
		module.start();

		System.out.println("Server listening on IP " + config.getHostname() + " and port " + config.getPort());
	}
}
