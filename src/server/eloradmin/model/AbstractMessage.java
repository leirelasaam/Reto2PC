package server.eloradmin.model;

/**
 * This class contains the fields sent between the server and the client for a
 * Plain Text Message. The actual message is sent in JSON format, and the
 * netty-socket.io libraries will handle the parsing.
 */
public abstract class AbstractMessage {

	private String message = null;

	public AbstractMessage(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}