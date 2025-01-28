package server;

import server.config.ServerConfig;
import server.eloradmin.socketIO.SocketIOModule;
import server.elorbase.utils.AESUtil;
import javax.crypto.SecretKey;
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
			// Buscar IP local y establecerla
			//String hostName = InetAddress.getLocalHost().getHostAddress();
			//config.setHostname(hostName);
			logger.info("Hostname: " + config.getHostname());
			
			// Probar encriptación
			SecretKey key = AESUtil.loadKey();
			String secret = "Elorrieta-Errekamari 2025";
			String encrypted = AESUtil.encrypt(secret, key);
			String decrypted = AESUtil.decrypt(encrypted, key);

			if (secret.equals(decrypted)) {
				logger.info("AES system working.");
				
				// We start the server
				SocketIOServer server = new SocketIOServer(config);
				SocketIOModule module = new SocketIOModule(server, key);
				module.start();
			} else {
				// No va a funcionar la comunicación, así que no se inicia el server
				logger.error("Unable to set up AES system. Server won't start.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
