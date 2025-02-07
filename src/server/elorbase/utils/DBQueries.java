package server.elorbase.utils;

public class DBQueries {

	private static final String U = "User";
	private static final String S = "Schedule";
	private static final String D = "Document";
	public static final String U_BY_EMAIL_OR_PIN = "FROM " + U + " as U WHERE U.email = :email OR U.pin = :pin";
	public static final String S_BY_TEACHER = "FROM " + S + " as S WHERE S.module.user.id = :id ORDER BY S.day DESC, S.hour DESC";
	
	// JOIN FETCH se utiliza para que forzar a que se carguen los datos de la otra entidad
	public static final String U_BY_EMAIL = "FROM " + U + " as U JOIN FETCH U.roles WHERE U.email = :email";
	public static final String U_BY_ROLE = "FROM " + U + " as U JOIN FETCH U.role WHERE U.role.id = :roleId";

	//public static final String D_BY_TEACHER = "FROM " + S + " as S WHERE S.module.user.id = :id ORDER BY S.day DESC, S.hour DESC";
	public static final String USER_BY_EMAIL_OR_PIN = "FROM User as U LEFT JOIN FETCH U.role WHERE U.email = :email OR U.pin = :pin";
	public static final String DOCUMENTS_BY_STUDENT = "FROM Document as d INNER JOIN FETCH d.module m LEFT JOIN FETCH m.cycle c LEFT JOIN FETCH m.user u LEFT JOIN FETCH u.role INNER JOIN m.enrollments e WHERE e.user.id = :id ORDER BY m.id";
	public static final String MEETINGS_BY_TEACHER = "FROM Meeting m JOIN FETCH m.user us JOIN FETCH us.role ro JOIN m.participants p JOIN p.user u JOIN u.role WHERE u.id = :id AND m.week >= :currentWeek ORDER BY m.week ASC, m.day ASC, m.time ASC";
	public static final String PARTICIPANTS_BY_MEETING = "From Participant p JOIN FETCH p.meeting m JOIN FETCH p.user u JOIN FETCH u.role WHERE p.meeting.id = :id ORDER BY u.lastname ASC, u.name ASC";
	public static final String ALL_COURSES = "FROM Course";
	public static final String TEACHER_SCHEDULE_PROCEDURE = "CALL TeacherSchedule(:teacher_id, :selected_week)";
	public static final String USER_BY_ID = "FROM " + U + " as U JOIN FETCH U.role WHERE U.id = :id";

}
