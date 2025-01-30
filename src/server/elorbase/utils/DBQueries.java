package server.elorbase.utils;

public class DBQueries {
	public static final String USER_BY_EMAIL_OR_PIN = "FROM User as U LEFT JOIN FETCH U.role WHERE U.email = :email OR U.pin = :pin";
	public static final String DOCUMENTS_BY_STUDENT = "FROM Document as d INNER JOIN FETCH d.module m LEFT JOIN FETCH m.cycle c LEFT JOIN FETCH m.user u LEFT JOIN FETCH u.role INNER JOIN m.enrollments e WHERE e.user.id = :id ORDER BY m.id";
	public static final String MEETINGS_BY_TEACHER = "FROM Meeting as m LEFT JOIN FETCH m.participants p WHERE m.user.id = :id OR p.user.id = :id";
}
