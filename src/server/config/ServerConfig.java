package server.config;

public class ServerConfig {

	public static final String HOST_NAME = "192.168.56.1";

	public static final int PORT = 3000;
	// SMTP
	public static String SMTP_HOST = "smtp.gmail.com";
	public static String SMTP_PORT = "587";
	public static String SMTP_SENDER = "elormail.server@gmail.com"; // Pass Elorrieta00, entrar en enviados
	public static String SMTP_PASS = "mmde jyrc unnu eifp"; 
	// Ecriptación
	public static String AES_KEY = "src/server/config/aes.key";

	public static String MODULE_FILES = "resources/elordocs/";
}
