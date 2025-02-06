package server.elorbase.managers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import server.elorbase.entities.Participant;

public class ParticipantsManager {

	private SessionFactory sesion;

	public ParticipantsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	// Método para crear una nueva reunión
	public void createParticipants(Participant participants, Session session) {

		Transaction transaction = null;

		session.persist(participants);
	}
}
