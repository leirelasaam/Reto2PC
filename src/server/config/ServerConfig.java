package server.config;

public class ServerConfig {
	// Server
	//public static final String HOST_NAME = "localhost";
	/*
	 * LEIRE:
	 * Casa: 192.168.1.139
	 * Clase: 10.5.104.31
	 * 
	 * DAYANA:
	 * Clase: 10.5.104.51
	 */
	
	//Lucian
	//public static final String HOST_NAME = "10.5.104.25";
	//Lucian Casa PC
	public static final String HOST_NAME = "192.168.1.135";
	
	
	//public static final String HOST_NAME = "10.5.104.51";

	public static final int PORT = 3000;

	// SMTP
	public static String SMTP_HOST = "smtp.gmail.com";
	public static String SMTP_PORT = "587";
	public static String SMTP_SENDER = "elormail.server@gmail.com"; // Pass Elorrieta00, entrar en enviados
	public static String SMTP_PASS = "mmde jyrc unnu eifp"; 
	// Ecriptación
	public static String AES_KEY = "src/server/config/aes.key";
	
	public static String MODULE_FILES = "src/server/elordocs/";
}
