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
import server.elorbase.managers.SchedulesManager;
import server.elorbase.managers.UsersManager;
import server.elorbase.dtos.ScheduleDTO;
import server.elorbase.entities.Schedule;
import server.elorbase.entities.TeacherSchedule;
import server.elorbase.entities.User;
import server.elorbase.utils.AESUtil;
import server.elorbase.utils.BcryptUtil;
import server.elorbase.utils.HibernateUtil;
import server.elorbase.utils.JSONUtil;
import server.elormail.EmailSender;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * Server control main configuration class
 */
public class SocketIOModule {

	// The server
	private SocketIOServer server = null;
	private SessionFactory sesion = null;
	private static final Logger logger = Logger.getLogger(SocketIOModule.class);
	private SecretKey key = null;

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
		
		//Registro - Cargar datos del usuario en el registro
		server.addEventListener(Events.ON_REGISTER_INFO.value, MessageInput.class, this.getUserDataForSignUp());
	    
	    //Registro - Guardar datos actualizados del usuario en la BBDD
	    server.addEventListener(Events.ON_REGISTER_UPDATE.value, MessageInput.class, this.saveUpdatedSignUpData());

	    //server.addEventListener(Events.ON_REGISTER_INFO_ANSWER.value, MessageInput.class, this.sendResetPassEmail());
	    //server.addEventListener(Events.ON_REGISTER_UPDATE_ANSWER.value, MessageInput.class, this.sendResetPassEmail());
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
	                    logger.debug("[Client = " + ip + "] Not encripted user: " + answerMessage);
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
				JsonObject messageObject = new JsonObject();
				
				SchedulesManager sm = new SchedulesManager(sesion);
				ArrayList<TeacherSchedule> schedules = sm.getTeacherWeeklySchedule(teacherId, selectedWeek);
				
				MessageOutput msgOut = null;
				if (schedules != null) {
					JsonArray schedulesArray = new JsonArray();
					for (TeacherSchedule s : schedules) {
						JsonObject scheduleJson = gson.toJsonTree(s).getAsJsonObject();
						schedulesArray.add(scheduleJson);
					}
					
					messageObject.add("schedules", schedulesArray);
					String messageContent = gson.toJson(messageObject);
					msgOut = new MessageOutput(HttpURLConnection.HTTP_OK, messageContent);
				} else {
					msgOut = DefaultMessages.NOT_FOUND;
				}
				
				client.sendEvent(Events.ON_TEACHER_SCHEDULE_ANSWER.value, msgOut);
				logger.debug("[Client = " + ip + "] Sending: " + msgOut.toString());
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				client.sendEvent(Events.ON_TEACHER_SCHEDULE_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
			}
		});
	}
	
	private DataListener<MessageInput> getUserDataForSingUp1() {
		return ((client, data, ackSender) -> {
			String ip = client.getRemoteAddress().toString();
			logger.info("[Client = " + ip + "] Client wants to sign up");
			
			
			String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);
				/*
				 * Ejemplo de lo que nos llega: { "message": "70"}
				 */
				Gson gson = new Gson();
				// Extraer el JSON
				JsonObject jsonObject = gson.fromJson(clientMsg, JsonObject.class);
				// Extraer el message
				String id = jsonObject.get("message").getAsString();
				int id_int = Integer.parseInt(id);

				JsonObject messageObject = new JsonObject();
				
				// Obtener datos del registro por id +++++++++++++++++++++++++++++++++++++++++++
				SchedulesManager sm = new SchedulesManager(sesion);
				ArrayList<Schedule> schedules = sm.getByUserId(id_int);
				if (schedules != null) {
					JsonArray schedulesArray = new JsonArray();
					for (Schedule s : schedules) {
						ScheduleDTO sDTO = new ScheduleDTO(s);
						JsonObject scheduleJson = gson.toJsonTree(sDTO).getAsJsonObject();
						schedulesArray.add(scheduleJson);
					}
					messageObject.add("schedules", schedulesArray);
					String messageContent = gson.toJson(messageObject);
					MessageOutput messageOutput = new MessageOutput(HttpURLConnection.HTTP_OK, messageContent);
					client.sendEvent(Events.ON_TEACHER_SCHEDULE_ANSWER.value, messageOutput);
					logger.debug("[Client = " + ip + "] Sending: " + messageOutput.toString());
				} else {
					client.sendEvent(Events.ON_TEACHER_SCHEDULE_ANSWER.value, DefaultMessages.NOT_FOUND);
					logger.debug("[Client = " + ip + "] Sending: " + DefaultMessages.NOT_FOUND.toString());
				}
			} catch (Exception e) {
				logger.error("[Client = " + ip + "] Error: " + e.getMessage());
				client.sendEvent(Events.ON_TEACHER_SCHEDULE_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
				logger.debug("[Client = " + ip + "] Sending: " + DefaultMessages.INTERNAL_SERVER.toString());
			}
		});
	}
	
	//Comprobar que las funciones funcionan correctamente
	private DataListener<MessageInput> getUserDataForSignUp() { 
	    return ((client, data, ackSender) -> {
	    	logger.info("[ON_REGISTER_INFO] Evento recibido");
	    	logger.info("++++++++++++++++++++++++Prueba+++++++++++++++++++++ Client wants to login");
	        String ip = client.getRemoteAddress().toString();
	        
	        logger.info("[Client = " + ip + "] Client requested user data for sign-up");

	        System.out.println("getUserDataForSingUp");
	        
	        String encryptedMsg = null;
			try {
				String clientMsg = data.getMessage();
				String decryptedMsg = AESUtil.decrypt(clientMsg, key);
				logger.debug("[Client = " + ip + "] Server received: " + decryptedMsg);

	            /*
	             * Ejemplo de lo que nos llega: { "message": { "id": "1234" } }
	             */
	            Gson gson = new Gson();
	            // Extraer el JSON
	            JsonObject jsonObject = gson.fromJson(clientMsg, JsonObject.class);
	            // Extraer el message
	            String messageString = jsonObject.get("message").getAsString();
	            // Extraer el JSON dentro de message
	            JsonObject messageJsonObject = gson.fromJson(messageString, JsonObject.class);
	            // Extraer el login del usuario
	            String login = messageJsonObject.get("login").getAsString();

	            // Buscar el usuario por email
	            UsersManager um = new UsersManager(sesion);
				User user = um.getByEmailOrPin(login.trim());

	            // No se ha encontrado el usuario > 404 - NOT FOUND
	            if (user == null) {
	                client.sendEvent(Events.ON_REGISTER_INFO_ANSWER.value, DefaultMessages.NOT_FOUND);
	                logger.debug("[Client = " + ip + "] Sending: " + DefaultMessages.NOT_FOUND.toString());
	            } else {
	                // Usuario encontrado, enviar todos los datos
	               // String answerMessage = JSONUtils.getSerializedString(user);
	                //MessageOutput messageOutput = new MessageOutput(HttpURLConnection.HTTP_OK, answerMessage);
	                //client.sendEvent(Events.ON_REGISTER_INFO_ANSWER.value, messageOutput);
	                //logger.debug("[Client = " + ip + "] Sending: " + messageOutput.toString());
	                
	                
	            }
	        } catch (Exception e) {
	            logger.error("[Client = " + ip + "] Error: " + e.getMessage());
	            client.sendEvent(Events.ON_REGISTER_INFO_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
	            logger.debug("[Client = " + ip + "] Sending: " + DefaultMessages.INTERNAL_SERVER.toString());
	        }
	    });
	}

	//Comprobar que las funciones funcionan correctamente
	private DataListener<MessageInput> saveUpdatedSignUpData() {
	    return ((client, data, ackSender) -> {
	        String ip = client.getRemoteAddress().toString();
	        logger.info("[Client = " + ip + "] Client wants to update SignUp data.");

	        try {
	            String clientMsg = data.getMessage();
	            logger.debug("[Client = " + ip + "] Server received: " + clientMsg);

	            // Deserializar el JSON recibido
	            Gson gson = new Gson();
	            User updatedUser = gson.fromJson(clientMsg, User.class);

	            // Validar que el usuario recibido no sea nulo
	            if (updatedUser == null || updatedUser.getId() == null) {
	                client.sendEvent(Events.ON_REGISTER_UPDATE_ANSWER.value, DefaultMessages.BAD_REQUEST);
	                logger.debug("[Client = " + ip + "] Sending: " + DefaultMessages.BAD_REQUEST.toString());
	                return;
	            }

	            // Actualizar los datos en la base de datos
	            UsersManager um = new UsersManager(sesion);
	            boolean updated = um.updateUser(updatedUser);

	            if (updated) {
	                // Actualización exitosa: 200 OK
	                client.sendEvent(Events.ON_REGISTER_UPDATE_ANSWER.value, DefaultMessages.OK);
	                logger.debug("[Client = " + ip + "] User updated successfully.");
	            } else {
	                // Fallo en la actualización: 500 INTERNAL SERVER ERROR
	                client.sendEvent(Events.ON_REGISTER_UPDATE_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
	                logger.debug("[Client = " + ip + "] Error updating user in the database.");
	            }
	        } catch (Exception e) {
	            logger.error("[Client = " + ip + "] Error: " + e.getMessage());
	            client.sendEvent(Events.ON_REGISTER_UPDATE_ANSWER.value, DefaultMessages.INTERNAL_SERVER);
	            logger.debug("[Client = " + ip + "] Sending: " + DefaultMessages.INTERNAL_SERVER.toString());
	        }
	    });
	}




	// Server control
	public void start() {
		server.start();
		logger.info("Server started");
	}

	public void stop() {
		server.stop();
		// Cerrar la sesión bbdd
		sesion.close();
		logger.info("Server stopped");
	}
}