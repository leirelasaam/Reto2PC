package server.eloradmin.socketIO;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.JsonObject;

import server.eloradmin.config.Events;
import server.eloradmin.model.DefaultMessages;
import server.eloradmin.model.MessageInput;
import server.eloradmin.model.MessageOutput;
import server.elorbase.managers.CoursesManager;
import server.elorbase.managers.DocumentsManager;
import server.elorbase.managers.SchedulesManager;
import server.elorbase.managers.UsersManager;
import server.elorbase.entities.Course;
import server.elorbase.entities.Document;
import server.elorbase.entities.TeacherSchedule;
import server.elorbase.entities.User;
import server.elorbase.utils.AESUtil;
import server.elorbase.utils.BcryptUtil;
import server.elorbase.utils.HibernateUtil;
import server.elorbase.utils.JSONUtil;
import server.elormail.EmailSender;
import com.google.gson.Gson;
/**
 * Server control main configuration class
 */
public class SocketIOModule {

	// The server
	private SocketIOServer server = null;
	private SessionFactory sesion = null;
	private static final Logger logger = Logger.getLogger(SocketIOModule.class);
	private SecretKey key = null;
	private boolean isServerRunning = false;

	public SocketIOModule(SocketIOServer server, SecretKey key) {
		super();
		this.server = server;
		this.sesion = HibernateUtil.getSessionFactory();
		this.key = key;

		// Default events (for control the connection of clients)
		server.addConnectListener(onConnect());
		server.addDisconnectListener(onDisconnect());

		// Custom events
		server.addEventListener(Events.ON_LOGIN.value, MessageInput.class, this.login());
		server.addEventListener(Events.ON_LOGOUT.value, MessageInput.class, this.logout());
		server.addEventListener(Events.ON_RESET_PASS_EMAIL.value, MessageInput.class, this.sendResetPassEmail());
		server.addEventListener(Events.ON_TEACHER_SCHEDULE.value, MessageInput.class, this.getTeacherSchedule());
		//server.addEventListener(Events.ON_STUDENT_SCHEDULE.value, MessageInput.class, this.getStudentSchedule());
		server.addEventListener(Events.ON_STUDENT_DOCUMENTS.value, MessageInput.class, this.getStudentDocuments());
		server.addEventListener(Events.ON_STUDENT_COURSES.value, MessageInput.class, this.getAllCourses());
	}

	// Default events

	private ConnectListener onConnect() {
		return (client -> {
			String ip = client.getRemoteAddress().toString();

			client.joinRoom("default-room");
			logger.info("[Client = " + ip + "] New connection");
		});
	}

	private DisconnectListener onDisconnect() {
		return (client -> {
			String ip = client.getRemoteAddress().toString();

			client.leaveRoom("default-room");
			logger.info("[Client = " + ip + "] Disconected from server");
		});
	}

	// Custom events

	private DataListener<MessageInput> login() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to login");

			String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				/*
				 * Ejemplo de lo que nos llega: { "login": "user@example.com", "password": "1234" }
				 */
				
				// Extraer login y password
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				String login = jsonObject.get("login").getAsString();
				String password = jsonObject.get("password").getAsString();

				// Buscar el usuario por email
				UsersManager um = new UsersManager(sesion);
				User user = um.getByEmailOrPin(login.trim());
				

				MessageOutput msgOut = null;
				// No se ha encontrado usuario
				if (user == null) {
					msgOut = DefaultMessages.NOT_FOUND;
				} else {
					// Se ha encontrado el usuario
					if (BcryptUtil.verifyPassword(password, user.getPassword())) {
						// Encriptar el objeto usuario
	                    String answerMessage = JSONUtil.getSerializedString(user);
	                    // Está registrado y su rol es profe/estudiante
						if (user.isRegistered() && (user.getRole().getRole().equals("profesor")
								|| user.getRole().getRole().equals("estudiante"))) {
							msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
						// No está registrado y su rol es profe/estudiante
						} else if ((user.getRole().getRole().equals("profesor")
								|| user.getRole().getRole().equals("estudiante"))) {
							msgOut = new MessageOutput(HttpURLConnection.HTTP_FORBIDDEN,
									answerMessage);
						// Es god o admin, no debe acceder a Elorclass
						} else {	
							msgOut = DefaultMessages.BAD_REQUEST;
						}
					// Se ha encontrado el usuario y la contraseña no coincide
					} else {
						msgOut = DefaultMessages.UNAUTHORIZED;
					}
				}
				
				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_LOGIN_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_LOGIN_ANSWER.value,  encryptedMsg);
			}

		});
	}

	// NO FUNCIONAL, HAY QUE HACERLO
	private DataListener<MessageInput> logout() {
		return ((client, data, ackSender) -> {
			// This time, we simply write the message in data
			logger.info("Client wants to logout");

			// The JSON message from MessageInput
			String message = data.getMessage();

			// We parse the JSON into an JsonObject
			// The JSON should be something like this: {"message": "patata"}
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
			String userName = jsonObject.get("message").getAsString();

			// We do something on dataBase? ¯_(ツ)_/¯

			logger.info("Loged out");
		});
	}

	private DataListener<MessageInput> sendResetPassEmail() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to reset password");

			String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				/*
				 * Ejemplo de lo que nos llega: { "message": "ejemplo@usuario.com"}
				 */
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				String login = jsonObject.get("message").getAsString();
				
				UsersManager um = new UsersManager(sesion);
				User user = um.getByEmailOrPin(login);

				MessageOutput msgOut = null;
				if (user != null) {
					EmailSender es = new EmailSender();
					@SuppressWarnings("deprecation")
					String password = RandomStringUtils.randomAlphanumeric(10);
					um.updatePasswordByUser(user, password);
					es.sendEmail(user.getEmail(), "ElorClass - Nueva contraseña", "Contraseña nueva: " + password);
					msgOut = DefaultMessages.OK;
				} else {
					msgOut = DefaultMessages.NOT_FOUND;
				}
				
				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_RESET_PASS_EMAIL_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_RESET_PASS_EMAIL_ANSWER.value, encryptedMsg);
			}
		});
	}

	private DataListener<MessageInput> getTeacherSchedule() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to get the schedule");
			String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				/*
				 * Ejemplo de lo que nos llega: { "id": 70, "week": 1}
				 */
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				int teacherId = jsonObject.get("id").getAsInt();
				int selectedWeek = jsonObject.get("week").getAsInt();
				
				
				MessageOutput msgOut = null;
				if (selectedWeek < 1 || selectedWeek > 39) {
					msgOut = DefaultMessages.BAD_REQUEST;
				} else {
					SchedulesManager sm = new SchedulesManager(sesion);
					ArrayList<TeacherSchedule> schedules = sm.getTeacherWeeklySchedule(teacherId, selectedWeek);
					
					if (schedules != null) {
						String answerMessage = JSONUtil.getSerializedArrayString(schedules, "schedules");
						msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
					} else {
						msgOut = DefaultMessages.NOT_FOUND;
					}
				}
				
				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_TEACHER_SCHEDULE_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_TEACHER_SCHEDULE_ANSWER.value, encryptedMsg);
			}
		});
	}
	
	private DataListener<MessageInput> getStudentDocuments() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to get documents");
			String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				/*
				 * Ejemplo de lo que nos llega: { "id": "70" }
				 */
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				int studentId = jsonObject.get("message").getAsInt();
				
				
				MessageOutput msgOut = null;
				
				DocumentsManager dm = new DocumentsManager(sesion);
				ArrayList<Document> documents = dm.getDocumentsByUserId(studentId);
				
				if (documents != null) {
					String answerMessage = JSONUtil.getSerializedArrayString(documents, "documents");
					msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
				} else {
					msgOut = DefaultMessages.NOT_FOUND;
				}
				
				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_STUDENT_DOCUMENTS_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_STUDENT_DOCUMENTS_ANSWER.value, encryptedMsg);
			}
		});
	}
	
	private DataListener<MessageInput> getAllCourses() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to get courses");
			String encryptedMsg = null;
			try {
				MessageOutput msgOut = null;
				
				CoursesManager cm = new CoursesManager(sesion);
				ArrayList<Course> courses = cm.getAllCourses();
				
				if (courses != null) {
					String answerMessage = JSONUtil.getSerializedArrayString(courses, "courses");
					msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
				} else {
					msgOut = DefaultMessages.NOT_FOUND;
				}
				
				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_STUDENT_COURSES_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_STUDENT_COURSES_ANSWER.value, encryptedMsg);
				e.printStackTrace();
			}
		});
	}

	// Server control
	public void start() {
		if (!isServerRunning) {
			isServerRunning = true;
			server.start();
			logger.info("Server started");
		} else {
			logger.warn("Server already running");
		}
	}

	public void stop() {
		server.stop();
		// Cerrar la sesión bbdd
		sesion.close();
		logger.info("Server stopped");
	}
}