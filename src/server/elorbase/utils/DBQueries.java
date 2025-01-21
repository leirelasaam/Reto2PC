package server.elorbase.utils;

public class DBQueries {
	private static final String U = "Users";
	
	// JOIN FETCH se utiliza para que forzar a que se carguen los datos de la otra entidad
	public static final String U_BY_EMAIL = "FROM " + U + " as U JOIN FETCH U.roles WHERE U.email = :email";
	
	// Consulta para obtener todos los usuarios con un rol específico
	public static final String U_BY_ROLE = 
		    "FROM " + U + " as U WHERE U.role.id = :roleId";
}
