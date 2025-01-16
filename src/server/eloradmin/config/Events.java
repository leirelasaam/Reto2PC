package server.eloradmin.config;

/**
 * The events our server is willing to listen or able to send
 */
public enum Events {

	ON_LOGIN ("onLogin"),
	ON_GET_ALL_USERS ("onGetAll"),
    ON_LOGOUT ("onLogout"),
    ON_LOGIN_ANSWER ("onLoginAnswer"),
    ON_LOGIN_ERROR_NO_EMAIL ("onLoginErrorNoEmail"),
    ON_LOGIN_ERROR ("onLoginError"),
	ON_GET_ALL_USERS_ANSWER ("onGetAllAnswer");
	
	public final String value;

	private Events(String value) {
		this.value = value;
	}
}
