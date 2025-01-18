package server.elorbase.managers;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.entities.User;
import server.elorbase.utils.DBQueries;

public class UsersManager {

	SessionFactory sesion = null;

	public UsersManager(SessionFactory sesion) {
		this.sesion = sesion;
	}
	
	public User getByEmail(String email) {
		User u = null;

		Session session = sesion.openSession();
		String hql = DBQueries.U_BY_EMAIL;
		Query<User> q = session.createQuery(hql, User.class);
		q.setParameter("email", email);
		q.setMaxResults(1);

		// No obtener como lista, ya que solo puede devolver uno o null
		u = q.uniqueResult();
		
		Hibernate.initialize(u.getRole());

		session.close();

		return u;
	}

}
