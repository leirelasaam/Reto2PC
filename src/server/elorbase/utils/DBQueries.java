package server.elorbase.utils;

public class DBQueries {
	private static final String U = "User";
	// JOIN FETCH se utiliza para que forzar a que se carguen los datos de la otra entidad
	public static final String U_BY_EMAIL = "FROM " + U + " as U WHERE U.email = :email";
}
