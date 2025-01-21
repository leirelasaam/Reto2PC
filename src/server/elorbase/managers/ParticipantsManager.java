package server.elorbase.managers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import server.elorbase.model.Participants;

public class ParticipantsManager {

    private SessionFactory sesion;

    public ParticipantsManager(SessionFactory sesion) {
        this.sesion = sesion;
    }

    // Método para crear una nueva reunión
    public void createParticipants(Participants participants) {
    	
        // Abrir una nueva sesión de Hibernate
        Session session = sesion.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            participants.setStatus("pendiente"); // Estado predeterminado

            session.persist(participants);
            
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
