package server.eloradmin.model;

/**
 * This class contains the fields sent from the client to the server for a
 * Plain Text Message. The actual message is sent in JSON format, and the
 * netty-socket.io libraries will parse the JSON into this java object. 
 */
public class MessageInput extends AbstractMessage {

	public MessageInput(String message) {
		super(message);
	}
}
