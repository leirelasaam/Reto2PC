package server;

import server.eloradmin.socketIO.SocketIOModule;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class Server {
	private static final Logger logger = Logger.getLogger(Server.class);
	public static void main(String[] args) {
		// Server configuration
		Configuration config = new Configuration();
		config.setHostname(ServerConfig.HOST_NAME);
		config.setPort(ServerConfig.PORT);
		
		try {
			String hostName = InetAddress.getLocalHost().getHostAddress();
			config.setHostname(hostName);
		} catch (UnknownHostException e) {
			logger.error("Error: " + e.getMessage());
		}

		// We start the server
		SocketIOServer server = new SocketIOServer(config);
		SocketIOModule module = new SocketIOModule(server);
		module.start();

		logger.info("Server data: hostname=" + config.getHostname() + ", port=" + config.getPort());
	}
}
