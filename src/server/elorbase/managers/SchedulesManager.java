package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.entities.Schedule;
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

}
