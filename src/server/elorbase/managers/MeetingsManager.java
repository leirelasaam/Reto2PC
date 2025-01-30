package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import server.elorbase.entities.Meeting;
import server.elorbase.utils.DBQueries;

public class MeetingsManager {
	
	SessionFactory sesion = null;

	public MeetingsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}
	
	public ArrayList<Meeting> getMeetingsByUser(int id) {
		ArrayList<Meeting> meetings = null;
		Session session = sesion.openSession();
		
		try {
			// Reuniones propias
			String hql1 = "FROM Meeting m JOIN FETCH m.participants WHERE m.user.id = :userId";
			Query<Meeting> query1 = session.createQuery(hql1, Meeting.class);
			query1.setParameter("userId", id);
			List<Meeting> result1 = query1.list();

			// Reuniones como participante
			String hql2 = "FROM Meeting m JOIN FETCH m.participants p WHERE p.user.id = :userId";
			Query<Meeting> query2 = session.createQuery(hql2, Meeting.class);
			query2.setParameter("userId", id);
			List<Meeting> result2 = query2.list();

			// Combinar las reuniones
			List<Meeting> combinedResults = new ArrayList<>();
			combinedResults.addAll(result1);
			combinedResults.addAll(result2);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return meetings;
	}

}