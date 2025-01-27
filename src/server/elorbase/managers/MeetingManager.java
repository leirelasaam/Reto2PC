package server.elorbase.managers;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import server.elorbase.entities.Meeting;
import server.elorbase.entities.Participant;

public class MeetingManager {

	private SessionFactory sesion;

	public MeetingManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	// Método para CREAR NUEVA REUNIÓN

	public Meeting createMeeting(Meeting meeting, List<Participant> participants) {

		// Abrir una nueva sesión de Hibernate
		Session session = sesion.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			meeting.setStatus("pendiente");

			// Guardar la reunión en la base de datos
			session.persist(meeting);

			Long meetingId = meeting.getId();

			ParticipantsManager pm = new ParticipantsManager(sesion);

			for (Participant participant : participants) {
				participant.setMeetingId(meetingId);
				participant.setStatus("pendiente");
				pm.createParticipants(participant);
			}

			// Confirmar la transacción
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return meeting;
	}
}
