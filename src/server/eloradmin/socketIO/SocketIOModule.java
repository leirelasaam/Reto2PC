package server.eloradmin.socketIO;

import java.util.List;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Base64;

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
import server.elorbase.managers.MeetingManager;
import server.elorbase.managers.SchedulesManager;
import server.elorbase.managers.UsersManager;
//import server.elorbase.dtos.ScheduleDTO;
import server.elorbase.entities.Schedule;
import server.elorbase.managers.CoursesManager;
import server.elorbase.managers.DocumentsManager;
import server.elorbase.managers.MeetingsManager;
import server.elorbase.entities.Course;
import server.elorbase.entities.Document;
import server.elorbase.entities.StudentSchedule;
import server.elorbase.entities.Meeting;
import server.elorbase.entities.TeacherSchedule;
import server.elorbase.entities.User;
import server.elorbase.utils.AESUtil;
import server.elorbase.utils.BcryptUtil;
import server.elorbase.utils.HibernateUtil;
import server.elorbase.utils.JSONUtil;
import server.elormail.EmailSender;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


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
		server.addEventListener(Events.ON_GET_ALL_USERS.value, MessageInput.class, this.getUsersByRole());
		server.addEventListener(Events.ON_RESET_PASS_EMAIL.value, MessageInput.class, this.sendResetPassEmail());
		server.addEventListener(Events.ON_TEACHER_SCHEDULE.value, MessageInput.class, this.getTeacherSchedule());
	    //Registro - Guardar datos actualizados del usuario en la BBDD
	    server.addEventListener(Events.ON_REGISTER_UPDATE.value, MessageInput.class, this.saveUpdatedSignUpData());

	    //server.addEventListener(Events.ON_REGISTER_INFO_ANSWER.value, MessageInput.class, this.sendResetPassEmail());
	    //server.addEventListener(Events.ON_REGISTER_UPDATE_ANSWER.value, MessageInput.class, this.sendResetPassEmail());
		//server.addEventListener(Events.ON_STUDENT_SCHEDULE.value, MessageInput.class, this.getStudentSchedule());
		server.addEventListener(Events.ON_CREATE_MEETING.value, MessageInput.class, this.createMeeting());

		server.addEventListener(Events.ON_STUDENT_DOCUMENTS.value, MessageInput.class, this.getStudentDocuments());
		server.addEventListener(Events.ON_UPDATE_PASS.value, MessageInput.class, this.updatePass());
		server.addEventListener(Events.ON_STUDENT_SCHEDULE.value, MessageInput.class, this.getStudentSchedule());
		server.addEventListener(Events.ON_STUDENT_DOCUMENTS.value, MessageInput.class, this.getStudentDocuments());
		server.addEventListener(Events.ON_ALL_MEETINGS.value, MessageInput.class, this.getTeacherMeetings());
		server.addEventListener(Events.ON_PARTICIPANT_STATUS_UPDATE.value, MessageInput.class, this.updateStatus(true));
		server.addEventListener(Events.ON_MEETING_STATUS_UPDATE.value, MessageInput.class, this.updateStatus(false));
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
				 * Ejemplo de lo que nos llega: { "login": "user@example.com", "password":
				 * "1234" }
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
							msgOut = new MessageOutput(HttpURLConnection.HTTP_FORBIDDEN, answerMessage);
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
				client.sendEvent(Events.ON_LOGIN_ANSWER.value, encryptedMsg);
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
					msgOut = DefaultMessages.UNAUTHORIZED;
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

	private DataListener<MessageInput> updatePass() {
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
				String user_email = jsonObject.get("email").getAsString();
				String old_password = jsonObject.get("oldPassword").getAsString();
				String new_password = jsonObject.get("newPassword").getAsString();

				UsersManager um = new UsersManager(sesion);
				User user = um.getByEmailOrPin(user_email);

				MessageOutput msgOut = null;
				if (user != null) {
					if (BcryptUtil.verifyPassword(old_password, user.getPassword())) {
						if (!old_password.equals(new_password)) {
							um.updatePasswordByUser(user, new_password);
							msgOut = DefaultMessages.OK;
						} else {
							msgOut = DefaultMessages.CONFLICT;
						}
						
					}else {
						msgOut = DefaultMessages.UNAUTHORIZED;
					}
				} else {
					msgOut = DefaultMessages.NOT_FOUND;
				}

				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_UPDATE_PASS_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_UPDATE_PASS_ANSWER.value, encryptedMsg);
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

	private DataListener<MessageInput> getStudentSchedule() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to get the schedule");
			String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				int studentId = jsonObject.get("message").getAsInt();

				MessageOutput msgOut = null;

				SchedulesManager sm = new SchedulesManager(sesion);
				ArrayList<StudentSchedule> schedules = sm.getStudentSchedule(studentId);

				if (schedules != null) {
					String answerMessage = JSONUtil.getSerializedArrayString(schedules, "schedules");
					msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
				} else {
					msgOut = DefaultMessages.NOT_FOUND;
				}

				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_STUDENT_SCHEDULE_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_STUDENT_SCHEDULE_ANSWER.value, encryptedMsg);
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
	
	//Funcion de prueba
	private DataListener<MessageInput> saveUpdatedSignUpData() {
	    return ((client, data, ackSender) -> {
	        String ip = client.getRemoteAddress().toString();
	        logger.info("[Client = " + ip + "] Received event: ON_REGISTER_UPDATE");

	        try {
	            if (data == null || data.getMessage() == null) {
	                logger.error("[Client = " + ip + "] ERROR: Received null message");
	                return;
	            }

	            String clientMsg = data.getMessage();
	            logger.debug("[Client = " + ip + "] Raw encrypted message: " + clientMsg);
	            
	            String decryptedMsg = AESUtil.decrypt(clientMsg, key);
	            logger.debug("[Client = " + ip + "] Decrypted message: " + decryptedMsg);
	            
	            
	         // Convertir el JSON a un objeto JsonObject
	            JsonObject jsonObject = JsonParser.parseString(decryptedMsg).getAsJsonObject();
	            
	            
	            
	         // Deserializar el JSON recibido
	            Gson gson = new Gson();
	            User updatedUser = new User();
				
	            //Datps extraidos del usuario actualizado
	            Long id = jsonObject.has("id") ? Long.parseLong(jsonObject.get("id").getAsString()) : null;
	            String name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : null;
	            String email = jsonObject.has("email") ? jsonObject.get("email").getAsString() : null;
	            String password = jsonObject.has("password") ? jsonObject.get("password").getAsString() : null;
	            String lastname = jsonObject.has("lastname") ? jsonObject.get("lastname").getAsString() : null;
	            String pin = jsonObject.has("pin") ? jsonObject.get("pin").getAsString() : null;
	            String address = jsonObject.has("address") ? jsonObject.get("address").getAsString() : null;
	            String phone1 = jsonObject.has("phone1") ? jsonObject.get("phone1").getAsString() : null;
	            String phone2 = jsonObject.has("phone2") ? jsonObject.get("phone2").getAsString() : null;
	            boolean registered = jsonObject.has("registered") && jsonObject.get("registered").getAsBoolean();

	            byte[] photo = jsonObject.has("photo") ? Base64.getDecoder().decode(jsonObject.get("photo").getAsString()) : null;

	            updatedUser.setId(id); 
	            updatedUser.setName(name);
	            updatedUser.setEmail(email);
	            //updatedUser.setPassword(password);
	            updatedUser.setLastname(lastname); 
	            updatedUser.setPin(pin);
	            updatedUser.setAddress(address); 
	            updatedUser.setPhone1(phone1);
	            updatedUser.setPhone2(phone2); 
	            updatedUser.setRegistered(registered); 
	            updatedUser.setPhoto(photo);
	            
	            // Ahora hay que tomar el id e ir actualizando los datos del usuario en la base de datos
	            UsersManager um = new UsersManager(sesion);
	            boolean updated = um.updateUser(updatedUser);
	            
	            um.updatePasswordByUser(updatedUser, password);
	            
	            if (updated) {
	                // Actualización exitosa: 200 OK
	                client.sendEvent(Events.ON_REGISTER_UPDATE_ANSWER.value, DefaultMessages.OK);
	                logger.debug("[Client = " + ip + "] User: "+ id + ", " + name + " updated successfully.");
	                
	                //Devolver un evento con que se ha guardado todo bien en la base de datos.
	                
	            } else {
	                // Fallo en la actualización: 500 INTERNAL SERVER ERROR
	                client.sendEvent(Events.ON_REGISTER_UPDATE_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
	                logger.debug("[Client = " + ip + "] Error updating user in the database.");
	            }
	            
	        } catch (Exception e) {
	            logger.error("[Client = " + ip + "] Error while decrypting: " + e.getMessage(), e);
	            logger.error("[Client = " + ip + "] Error while processing saveUpdatedSignUpData: " + e.getMessage(), e);
	            client.sendEvent(Events.ON_REGISTER_UPDATE_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
	            logger.debug("[Client = " + ip + "] Sending: " + DefaultMessages.INTERNAL_SERVER.toString());
	        }
	    });
	}
	
	private DataListener<MessageInput> getTeacherMeetings() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to get meetings");
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
				int teacherId = jsonObject.get("message").getAsInt();

				MessageOutput msgOut = null;

				MeetingsManager mm = new MeetingsManager(sesion);
				ArrayList<Meeting> meetings = mm.getMeetingsByUser(teacherId);

				if (meetings != null) {
					String answerMessage = JSONUtil.getSerializedArrayString(meetings, "meetings");
					msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
				} else {
					msgOut = DefaultMessages.NOT_FOUND;
				}

				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_ALL_MEETINGS_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_ALL_MEETINGS_ANSWER.value, encryptedMsg);
			}
		});
	}

	private DataListener<MessageInput> updateStatus(boolean isParticipant) {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to get the schedule");
			String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				/*
				 * Ejemplo de lo que nos llega: { "userId": 70, "meetingId": 1, "status":
				 * "aceptada"}
				 */
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				int teacherId = jsonObject.get("userId").getAsInt();
				int meetingId = jsonObject.get("meetingId").getAsInt();
				String status = jsonObject.get("status").getAsString();

				MessageOutput msgOut = null;
				if (status.equals("aceptada") || status.equals("rechazada") || status.equals("pendiente")
						|| status.equals("forzada") || status.equals("cancelada")) {

					MeetingsManager mm = new MeetingsManager(sesion);
					boolean isUpdated;
					if (isParticipant) {
						isUpdated = mm.updateParticipantStatus(teacherId, meetingId, status);
					} else {
						isUpdated = mm.updateMeetingStatus(teacherId, meetingId, status);
					}

					if (isUpdated) {
						msgOut = DefaultMessages.OK;
					} else {
						msgOut = DefaultMessages.NOT_FOUND;
					}
				} else {
					msgOut = DefaultMessages.BAD_REQUEST;
				}

				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_MEETING_STATUS_UPDATE_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_MEETING_STATUS_UPDATE_ANSWER.value, encryptedMsg);
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

	private DataListener<MessageInput> getUsersByRole() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to get users by role");

			String encryptedMsg = null;
			try {
				// Obtener y desencriptar el mensaje recibido
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				/*
				 * Ejemplo de lo que nos llega: { "role": "profesor" }
				 */

				// Extraer el rol del mensaje JSON
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				int roleId = jsonObject.get("roleId").getAsInt();

				// Buscar los usuarios por rol
				UsersManager um = new UsersManager(sesion);
				List<User> users = um.getUsersByRole(roleId);

				MessageOutput msgOut = null;

				// Si no se encuentran usuarios con el rol especificado
				if (users == null || users.isEmpty()) {
					msgOut = new MessageOutput(HttpURLConnection.HTTP_NO_CONTENT, null);
				} else {
					// Serializar la lista de usuarios y enviarla como respuesta
					String usersJson = JSONUtil.getSerializedString(users);
					// logger.debug("[Client = " + ip + "] Users found: " + usersJson);

					msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, usersJson);
				}

				// Enviar la respuesta encriptada al cliente
				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_GET_ALL_USERS_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_GET_ALL_USERS_ANSWER.value, encryptedMsg);
			}
		});
	}

	private DataListener<MessageInput> createMeeting() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to create a meeting");

			String encryptedMsg = null;
			try {
				// Decrypt the incoming message
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

				/*
				 * Example input JSON: { "title": "Team Meeting", "description":
				 * "Discuss project milestones", "day": , "hour": "1", "organizerId": 1 }
				 */

				// Parse the JSON input
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(decryptedMsg, JsonObject.class);
				Meeting meetingAInsertar = new Meeting();
				meetingAInsertar.setDay(jsonObject.get("date").getAsByte());
				meetingAInsertar.setTime(jsonObject.get("time").getAsByte());
				// int organizerId = jsonObject.get("organizerId").getAsInt();

				// Create a new meeting using MeetingManager
				MeetingManager meetingManager = new MeetingManager(sesion);
				Meeting meetingCreada = meetingManager.createMeeting(meetingAInsertar, null);

				MessageOutput msgOut;

				if (meetingCreada != null) {
					// Serialize the created meeting
					String meetingJson = JSONUtil.getSerializedString(meetingCreada);
					logger.debug("[Client = " + ip + "] Meeting created: " + meetingJson);

					msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, meetingJson);
				} else {
					msgOut = DefaultMessages.INTERNAL_SERVER;
				}

				// Encrypt and send the response
				encryptedMsg = AESUtil.encryptObject(msgOut, key);
				client.sendEvent(Events.ON_CREATE_MEETING_ANSWER.value, encryptedMsg);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());

			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				encryptedMsg = AESUtil.encryptObject(DefaultMessages.INTERNAL_SERVER, key);
				client.sendEvent(Events.ON_CREATE_MEETING_ANSWER.value, encryptedMsg);
			}
		});
	}
}
