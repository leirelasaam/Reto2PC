package server.elorbase.utils;

public class DBQueries {
	public static final String USER_BY_EMAIL_OR_PIN = "FROM User as U LEFT JOIN FETCH U.role WHERE U.email = :email OR U.pin = :pin";
	public static final String SCHEDULE_BY_TEACHER = "FROM Schedule as S WHERE S.module.user.id = :id ORDER BY S.day DESC, S.hour DESC";
	public static final String DOCUMENTS_BY_STUDENT = "FROM Document as d INNER JOIN FETCH d.module m LEFT JOIN FETCH m.cycle c LEFT JOIN FETCH m.user u LEFT JOIN FETCH u.role INNER JOIN m.enrollments e WHERE e.user.id = :id ORDER BY m.id";
	public static final String ALL_COURSES = "FROM Course";

}
