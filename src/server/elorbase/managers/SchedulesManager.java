package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import server.elorbase.entities.StudentSchedule;
import server.elorbase.entities.TeacherSchedule;
import server.elorbase.utils.DBQueries;

public class SchedulesManager {

	private static final Logger logger = Logger.getLogger(SchedulesManager.class);
	private SessionFactory sesion = null;

	public SchedulesManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	public ArrayList<TeacherSchedule> getTeacherWeeklySchedule(int teacherId, int selectedWeek) {
		ArrayList<TeacherSchedule> schedules = null;
		Session session = null;
		Transaction transaction = null;

		try {
			session = sesion.openSession();
			transaction = session.beginTransaction();

			String sql = DBQueries.TEACHER_SCHEDULE_PROCEDURE;
			NativeQuery<TeacherSchedule> q = session.createNativeQuery(sql, TeacherSchedule.class);
			q.setParameter("teacher_id", teacherId);
			q.setParameter("selected_week", selectedWeek);

			List<TeacherSchedule> filas = q.list();
			
			if (filas != null && filas.size() > 0) {
				for (TeacherSchedule fila : filas) {
					if (schedules == null)
						schedules = new ArrayList<TeacherSchedule>();
					schedules.add(fila);
				}
			}

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
		
		return schedules;
	}
	
	public ArrayList<StudentSchedule> getStudentSchedule(int studentId) {
		ArrayList<StudentSchedule> schedules = null;
		Session session = sesion.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			String sql = DBQueries.STUDENT_SCHEDULE_PROCEDURE;

			NativeQuery<StudentSchedule> query = session.createNativeQuery(sql, StudentSchedule.class);
			query.setParameter("student_id", studentId);

			List<StudentSchedule> filas = query.getResultList();
			if (filas != null && filas.size() > 0) {
				for (StudentSchedule fila : filas) {
					if (schedules == null)
						schedules = new ArrayList<StudentSchedule>();
					schedules.add(fila);
				}
			}
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
		
		return schedules;
	}

}
