package server.elorbase.utils;

public class DBQueries {
	private static final String U = "User";
	private static final String S = "Schedule";
	private static final String D = "Document";
	public static final String U_BY_EMAIL_OR_PIN = "FROM " + U + " as U WHERE U.email = :email OR U.pin = :pin";
	public static final String S_BY_TEACHER = "FROM " + S + " as S WHERE S.module.user.id = :id ORDER BY S.day DESC, S.hour DESC";
	//public static final String D_BY_TEACHER = "FROM " + S + " as S WHERE S.module.user.id = :id ORDER BY S.day DESC, S.hour DESC";
}
