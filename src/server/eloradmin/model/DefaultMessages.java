package server.eloradmin.model;

import java.net.HttpURLConnection;

public class DefaultMessages {

	public static MessageOutput OK = new MessageOutput(HttpURLConnection.HTTP_OK, "OK");
	// CÓDIGOS 400
	public static MessageOutput BAD_REQUEST = new MessageOutput(HttpURLConnection.HTTP_BAD_REQUEST, "Bad Request");
	// 401
	public static MessageOutput UNAUTHORIZED = new MessageOutput(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
	// 403
	public static MessageOutput FORBIDDEN = new MessageOutput(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
	// 404
	public static MessageOutput NOT_FOUND = new MessageOutput(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
	
	// 500
	public static MessageOutput INTERNAL_SERVER = new MessageOutput(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Server Error");
}
