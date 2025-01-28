package server.elorbase.utils;

public class DBQueries {
	public static final String USER_BY_EMAIL_OR_PIN = "FROM User as U LEFT JOIN FETCH U.role WHERE U.email = :email OR U.pin = :pin";
	public static final String SCHEDULE_BY_TEACHER = "FROM Schedule as S WHERE S.module.user.id = :id ORDER BY S.day DESC, S.hour DESC";
	public static final String DOCUMENTS_BY_STUDENT = "SELECT d.name AS name, d.route as path, m.name AS module FROM User u INNER JOIN u.enrollments e INNER JOIN e.module m INNER JOIN m.documents d WHERE u.id = :id ORDER BY m.id";
}
