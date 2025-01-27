package server.elorbase.utils;

public class DBQueries {

	private static final String U = "User";
	private static final String S = "Schedule";
	public static final String U_BY_EMAIL_OR_PIN = "FROM " + U + " as U WHERE U.email = :email OR U.pin = :pin";
	public static final String S_BY_TEACHER = "FROM " + S + " as S WHERE S.module.user.id = :id ORDER BY S.day DESC, S.hour DESC";
	
	// JOIN FETCH se utiliza para que forzar a que se carguen los datos de la otra entidad
	public static final String U_BY_EMAIL = "FROM " + U + " as U JOIN FETCH U.roles WHERE U.email = :email";
	
	// Consulta para obtener todos los USUARIOS con un ROL específico
	public static final String U_BY_ROLE = "FROM " + U + " as U WHERE U.role.id = :roleId";

}
