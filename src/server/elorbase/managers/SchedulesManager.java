package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import server.elorbase.entities.TeacherSchedule;

public class SchedulesManager {

	SessionFactory sesion = null;

	public SchedulesManager(SessionFactory sesion) {
		this.sesion = sesion;
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
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return schedules;
	}

}
