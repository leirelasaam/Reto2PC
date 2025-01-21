package server.elorbase.managers;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import server.elorbase.model.Meetings;
import server.elorbase.model.Participants;


public class MeetingManager {

    private SessionFactory sesion;

    public MeetingManager(SessionFactory sesion) {
        this.sesion = sesion;
    }

    // Método para crear una nueva reunión
    public void createMeeting(Meetings meeting, List<Participants> participants) {
    	
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
            
            for (Participants participant : participants) {
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
    }
}
