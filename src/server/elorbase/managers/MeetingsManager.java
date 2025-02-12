package server.elorbase.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.elorbase.entities.Meeting;
import server.elorbase.entities.Participant;
import server.elorbase.utils.DBQueries;
import server.elorbase.utils.DateUtil;

public class MeetingsManager {

	private static final Logger logger = Logger.getLogger(MeetingsManager.class);
	private SessionFactory sesion = null;

	public MeetingsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	public ArrayList<Meeting> getMeetingsByUser(int id) {
		ArrayList<Meeting> meetings = null;
		Session session = null;

		try {
			session = sesion.openSession();
			int currentWeek = DateUtil.getCurrentWeek();
			//int today = DateUtil.getCurrentDay();

			String hql = DBQueries.MEETINGS_BY_TEACHER;
			Query<Meeting> q = session.createQuery(hql, Meeting.class);
			q.setParameter("id", id);
			q.setParameter("currentWeek", currentWeek);
			//q.setParameter("currentDay", today);
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
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return meetings;
	}

	private ArrayList<Participant> getParticipantsByMeeting(long id) {
		ArrayList<Participant> participants = null;
		Session session = null;

		try {
			session = sesion.openSession();
			String hql = DBQueries.PARTICIPANTS_BY_MEETING;
			Query<Participant> q = session.createQuery(hql, Participant.class);
			q.setParameter("id", id);
			List<Participant> filas = q.list();

			if (filas.size() > 0) {
				participants = new ArrayList<Participant>();
				participants.addAll(filas);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return participants;
	}

	public boolean updateParticipantStatus(int userId, int meetingId, String status) {
		boolean isUpdated = true;
		Session session = null;
		Transaction transaction = null;

		try {
			session = sesion.openSession();
			transaction = session.beginTransaction();
			
			// Buscar el registro en participants
			String hql = DBQueries.PARTICIPANT_BY_MEETING_AND_ID;
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
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return isUpdated;
	}
	
	public boolean updateMeetingStatus(int userId, int meetingId, String status) {
		boolean isUpdated = true;
		Session session = null;
		Transaction transaction = null;

		try {
			session = sesion.openSession();
			transaction = session.beginTransaction();
			
			// Buscar el registro en meetings
			String hql = DBQueries.MEETING_BY_ID_AND_USER;
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
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return isUpdated;
	}
	
	public Meeting createMeeting(Meeting meeting) {
		Session session = null;
		Transaction transaction = null;

		try {
			session = sesion.openSession();
			transaction = session.beginTransaction();

			// Guardar la reunión en la base de datos
			session.persist(meeting);

			for (Participant participant : meeting.getParticipants()) {
				participant.setMeeting(meeting);
				session.persist(participant);
			}

			// Confirmar la transacción
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return meeting;
	}

}