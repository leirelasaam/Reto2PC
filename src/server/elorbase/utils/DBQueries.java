package server.elorbase.utils;

public class DBQueries {
	private static final String U = "User";
	private static final String S = "Schedule";
	public static final String U_BY_EMAIL_OR_PIN = "FROM " + U + " as U WHERE U.email = :email OR U.pin = :pin";
	public static final String S_BY_TEACHER = "FROM " + S + " as S WHERE S.module.user.id = :id";
}
