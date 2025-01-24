package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import server.elorbase.entities.Schedule;
import server.elorbase.entities.TeacherSchedule;
import server.elorbase.utils.DBQueries;

public class SchedulesManager {

	SessionFactory sesion = null;

	public SchedulesManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	public ArrayList<Schedule> getByUserId(int id) {
		ArrayList<Schedule> schedules = null;

		Session session = sesion.openSession();
		String hql = DBQueries.S_BY_TEACHER;
		Query<Schedule> q = session.createQuery(hql, Schedule.class);
		q.setParameter("id", id);

		List<Schedule> filas = q.list();

		if (filas.size() > 0) {
			for (Schedule fila : filas) {
				Hibernate.initialize(fila.getModule());
				Hibernate.initialize(fila.getModule().getUser());
				if (schedules == null)
					schedules = new ArrayList<Schedule>();
				schedules.add(fila);
			}
		}

		session.close();

		return schedules;
	}

	public ArrayList<TeacherSchedule> getTeacherWeeklySchedule(int teacherId, int selectedWeek) {
		ArrayList<TeacherSchedule> schedules = null;
		Session session = sesion.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			String sql = "CALL TeacherSchedule(:teacher_id, :selected_week)";

			NativeQuery<TeacherSchedule> query = session.createNativeQuery(sql, TeacherSchedule.class);
			query.setParameter("teacher_id", teacherId);
			query.setParameter("selected_week", selectedWeek);

			List<TeacherSchedule> filas = query.getResultList();
			if (filas.size() > 0) {
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
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return schedules;
	}

}
