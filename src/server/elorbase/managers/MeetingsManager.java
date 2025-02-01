package server.elorbase.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
			//int currentWeek = 1;

			String hql = DBQueries.MEETINGS_BY_TEACHER;
			Query<Meeting> q = session.createQuery(hql, Meeting.class);
			q.setParameter("id", id);
			q.setParameter("currentWeek", currentWeek);
			List<Meeting> filas = q.list();

			if (filas.size() > 0) {
				meetings = new ArrayList<Meeting>();
				meetings.addAll(filas);
				for (Meeting m : meetings) {
					ArrayList<Participant> p = getParticipantsByMeeting(m.getId());
					Set<Participant> participantSet = new HashSet<>(p);
					m.setParticipants(participantSet);
				}
			}

		} catch (Exception e) {
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

			if (filas.size() > 0) {
				participants = new ArrayList<Participant>();
				participants.addAll(filas);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return participants;
	}

	public boolean updateParticipantStatus(int userId, int meetingId, String status) {
		boolean isUpdated = true;
		Session session = sesion.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			
			// Buscar el registro en participants
			String hql = "FROM Participant WHERE user.id = :userId AND meeting.id = :meetingId";
			Query<Participant> q = session.createQuery(hql, Participant.class);
			q.setParameter("userId", userId);
			q.setParameter("meetingId", meetingId);
			
			Participant p = q.uniqueResult();

			if (p != null) {
				p.setStatus(status);
				session.merge(p);
			} else {
				isUpdated = false;
			}

			transaction.commit();
		} catch (Exception e) {
			isUpdated = false;
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return isUpdated;
	}
	
	public boolean updateMeetingStatus(int userId, int meetingId, String status) {
		boolean isUpdated = true;
		Session session = sesion.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			
			// Buscar el registro en meetings
			String hql = "FROM Meeting WHERE user.id = :userId AND id = :meetingId";
			Query<Meeting> q = session.createQuery(hql, Meeting.class);
			q.setParameter("userId", userId);
			q.setParameter("meetingId", meetingId);
			
			Meeting m = q.uniqueResult();

			if (m != null) {
				m.setStatus(status);
				session.merge(m);
			} else {
				isUpdated = false;
			}

			transaction.commit();
		} catch (Exception e) {
			isUpdated = false;
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return isUpdated;
	}

}