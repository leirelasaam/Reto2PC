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
	ON_GET_ALL_USERS_ANSWER ("onGetAllUsersAnswer"),
	ON_GET_ALL_USERS("onGetAllUsers"), 
	ON_CREATE_MEETING("onCreateMeeting"),
	ON_CREATE_MEETING_ANSWER("onCreateMeetingAnswer"),
    ON_STUDENT_DOCUMENTS("onStudentDocuments"),
    ON_STUDENT_DOCUMENTS_ANSWER("onStudentDocumentsAnswer"),
    ON_UPDATE_PASS("onUpdatePass"),
    ON_UPDATE_PASS_ANSWER("onUpdatePassAnswer");
	
	public final String value;

	private Events(String value) {
		this.value = value;
	}
}
