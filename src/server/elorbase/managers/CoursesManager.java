package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.entities.Course;
import server.elorbase.utils.DBQueries;

public class CoursesManager {

	private static final Logger logger = Logger.getLogger(CoursesManager.class);
	private SessionFactory sesion = null;

	public CoursesManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	public ArrayList<Course> getAllCourses() {
		ArrayList<Course> courses = null;
		Session session = null;

		try {
			session = sesion.openSession();
			String hql = DBQueries.ALL_COURSES;
			Query<Course> query = session.createQuery(hql, Course.class);
			List<Course> filas = query.list();

			if (filas.size() > 0) {
				for (Course fila : filas) {
					if (courses == null)
						courses = new ArrayList<Course>();

					courses.add(fila);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return courses;
	}

}
