package server;

import server.eloradmin.socketIO.SocketIOModule;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class Server {
	public static void main(String[] args) {
		// Server configuration
		Configuration config = new Configuration();
		config.setHostname(ServerConfig.HOST_NAME);
		config.setPort(ServerConfig.PORT);

		// We start the server
		SocketIOServer server = new SocketIOServer(config);
		SocketIOModule module = new SocketIOModule(server);
		module.start();

		System.out.println("Server listening on IP " + config.getHostname() + " and port " + config.getPort());
	}
}
