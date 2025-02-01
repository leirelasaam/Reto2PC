package server.elorbase.utils;

public class DBQueries {
	public static final String USER_BY_EMAIL_OR_PIN = "FROM User as U LEFT JOIN FETCH U.role WHERE U.email = :email OR U.pin = :pin";
	public static final String DOCUMENTS_BY_STUDENT = "FROM Document as d INNER JOIN FETCH d.module m LEFT JOIN FETCH m.cycle c LEFT JOIN FETCH m.user u LEFT JOIN FETCH u.role INNER JOIN m.enrollments e WHERE e.user.id = :id ORDER BY m.id";
	public static final String MEETINGS_BY_TEACHER = "FROM Meeting m JOIN FETCH m.user us JOIN FETCH us.role ro JOIN m.participants p JOIN p.user u JOIN u.role WHERE u.id = :id AND m.week >= :currentWeek ORDER BY m.week ASC, m.day ASC, m.time ASC";
	public static final String PARTICIPANTS_BY_MEETING = "From Participant p JOIN FETCH p.meeting m JOIN FETCH p.user u JOIN FETCH u.role WHERE p.meeting.id = :id ORDER BY u.lastname ASC, u.name ASC";
	public static final String ALL_COURSES = "FROM Course";
}
