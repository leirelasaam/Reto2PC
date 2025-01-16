package server.eloradmin.model;

/**
 * This class contains the fields sent from the server to the client for a
 * Plain Text Message. The actual message is sent in JSON format, and the
 * netty-socket.io libraries will parse the java object into JSON. 
 */
public class MessageOutput extends AbstractMessage{

	public MessageOutput(String message) {
		super(message);
	}

}
