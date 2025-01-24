package server.eloradmin.config;

/**
 * The events our server is willing to listen or able to send
 */
public enum Events {

	ON_LOGIN ("onLogin"),
    ON_LOGOUT ("onLogout"),
    ON_LOGIN_ANSWER ("onLoginAnswer"),
    ON_RESET_PASS_EMAIL("onResetPassEmail"),
    ON_RESET_PASS_EMAIL_ANSWER("onResetPassEmailAnswer"),
    ON_TEACHER_SCHEDULE("onTeacherSchedule"),
    ON_TEACHER_SCHEDULE_ANSWER("onTeacherScheduleAnswer"),
    
    //Registro - Cargar datos del usuario en el registro
    ON_REGISTER_INFO("onRegisterInfo"),
    ON_REGISTER_INFO_ANSWER("onRegisterInfoAnswer"),
    
    //Registro - Guardar datos actualizados del usuario en la BBDD
    ON_REGISTER_UPDATE("onRegisterUpdate"),
    ON_REGISTER_UPDATE_ANSWER("onRegisterUpdateAnswer"),
    ;
	
	public final String value;

	private Events(String value) {
		this.value = value;
	}
}
