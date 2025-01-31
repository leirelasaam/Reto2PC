package server.elorbase.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import server.elorbase.entities.Meeting;
import server.elorbase.entities.Participant;
import server.elorbase.utils.DBQueries;
import server.elorbase.utils.DateUtil;

public class MeetingsManager {
	
	SessionFactory sesion = null;

	public MeetingsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}
	
	public ArrayList<Meeting> getMeetingsByUser(int id) {
		ArrayList<Meeting> meetings = null;
		Session session = sesion.openSession();
		
		try {
			int currentWeek = DateUtil.getCurrentWeek();
			//byte currentWeek = (byte) DateUtil.getCurrentWeek();

			String hql = DBQueries.MEETINGS_BY_TEACHER;
			Query<Meeting> q = session.createQuery(hql, Meeting.class);
			q.setParameter("id", id);
			q.setParameter("currentWeek", currentWeek);
			List<Meeting> filas = q.list();

			if (filas.size()> 0) {
				meetings = new ArrayList<Meeting>();
				meetings.addAll(filas);
				for (Meeting m : meetings) {
					ArrayList<Participant> p = getParticipantsByMeeting(m.getId());
					Set<Participant> participantSet = new HashSet<>(p);
					m.setParticipants(participantSet);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return meetings;
	}
	
	private ArrayList<Participant> getParticipantsByMeeting(long id) {
		ArrayList<Participant> participants = null;
		Session session = sesion.openSession();
		
		try {
			String hql = DBQueries.PARTICIPANTS_BY_MEETING;
			Query<Participant> q = session.createQuery(hql, Participant.class);
			q.setParameter("id", id);
			List<Participant> filas = q.list();

			if (filas.size()> 0) {
				participants = new ArrayList<Participant>();
				participants.addAll(filas);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return participants;
	}

}