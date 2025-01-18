package server.elorbase.utils;

public class DBQueries {
	private static final String U = "User";
	public static final String U_BY_EMAIL_OR_PIN = "FROM " + U + " as U WHERE U.email = :email OR U.pin = :pin";
}
