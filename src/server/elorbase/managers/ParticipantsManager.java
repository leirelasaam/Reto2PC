package server.elorbase.managers;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import server.elorbase.entities.Participant;

public class ParticipantsManager {

	private SessionFactory sesion;
	private static final Logger logger = Logger.getLogger(ParticipantsManager.class);

	public ParticipantsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	// Método para crear una nueva reunión
	public void insertParticipant(Participant participant) {
		Session session = null;
		Transaction transaction = null;

		try {
			session = sesion.openSession();
			transaction = session.beginTransaction();
			session.persist(participant);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		
	}
}
