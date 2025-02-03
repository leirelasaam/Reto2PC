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
   
    //Registro - Guardar datos actualizados del usuario en la BBDD
    ON_REGISTER_UPDATE("onRegisterUpdate"),
    ON_REGISTER_UPDATE_ANSWER("onRegisterUpdateAnswer"),

    ON_STUDENT_SCHEDULE("onStudentSchedule"),
	ON_STUDENT_SCHEDULE_ANSWER("onStudentScheduleAnswer"),
    ON_STUDENT_DOCUMENTS("onStudentDocuments"),
    ON_STUDENT_DOCUMENTS_ANSWER("onStudentDocumentsAnswer"),
	ON_STUDENT_COURSES("onStudentCourses"),
	ON_STUDENT_COURSES_ANSWER("onStudentCoursesAnswer");

	public final String value;

	private Events(String value) {
		this.value = value;
	}
}
