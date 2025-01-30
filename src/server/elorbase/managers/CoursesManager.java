package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.entities.Course;
import server.elorbase.utils.DBQueries;

public class CoursesManager {
	
	SessionFactory sesion = null;
	
	public CoursesManager(SessionFactory sesion) {
		this.sesion = sesion;
	}
	
	public ArrayList<Course> getAllCourses() {
		ArrayList<Course> courses =  null; 
		
		Session session = sesion.openSession();
		String hql = DBQueries.ALL_COURSES;
		Query<Course> query = session.createQuery(hql, Course.class);
		List<Course> filas = query.list();
		
		if (filas.size() > 0) {
			for (Course fila : filas) {
				if (courses ==null )
					courses = new ArrayList<Course>();
				
				courses.add(fila);
				
				System.out.println("Curso: " + fila.getName());
			}
		}
		session.close();
		return courses;
	}
	
	
}

