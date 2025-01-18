package server.eloradmin.model;

import java.net.HttpURLConnection;

public class DefaultMessages {

	// CÓDIGOS 400
	// 401
	public static MessageOutput UNAUTHORIZED = new MessageOutput(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
	// 403
	public static MessageOutput FORBIDDEN = new MessageOutput(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
	// 404
	public static MessageOutput NOT_FOUND = new MessageOutput(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
	
	// 500
	public static MessageOutput INTERNAL_SERVER = new MessageOutput(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Server Error");
}
