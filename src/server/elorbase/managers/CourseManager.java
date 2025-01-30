package server.elorbase.managers;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.List;
import org.hibernate.query.Query;

import server.elorbase.entities.Course;
import server.elorbase.utils.DBQueries;

public class CourseManager {
	SessionFactory sesion = null;
	public CourseManager(SessionFactory sesion) {
		this.sesion = sesion;
	}
	public ArrayList<String> getCourseName() {
		ArrayList<String> courses =  new ArrayList<>(); 
		Session session = sesion.openSession();
		String hql = DBQueries.COURSE_NAME;
		Query<String> query = session.createQuery(hql, String.class);
		List<String> filas = query.list();
		if (filas.size() > 0) {
			courses.addAll(filas);
		}
		session.close();
		return courses;
	}
	public Course getCourseByName(String name) {
		Course course = null;
		Session session = sesion.openSession();
		String hql = DBQueries.COURSE_BY_NAME;
		Query<Course> query = session.createQuery(hql, Course.class);
		query.setParameter("name", name);
		List<Course> filas = query.list();
		if (filas.size() > 0) {
			course = filas.get(0);
		}
		session.close();
		return course;
	}
}

