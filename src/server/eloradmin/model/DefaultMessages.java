package server.eloradmin.model;

import java.net.HttpURLConnection;

public class DefaultMessages {

	// 400
	public static MessageOutput NOT_FOUND = new MessageOutput(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
	public static MessageOutput FORBIDDEN = new MessageOutput(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
	
	// 500
	public static MessageOutput INTERNAL_SERVER = new MessageOutput(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Server Error");
}
