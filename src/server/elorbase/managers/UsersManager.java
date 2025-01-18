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
	
	public User getByEmailOrPin(String login) {
		User u = null;

		Session session = sesion.openSession();
		String hql = DBQueries.U_BY_EMAIL_OR_PIN;
		Query<User> q = session.createQuery(hql, User.class);
		q.setParameter("email", login);
		q.setParameter("pin", login.toUpperCase());
		q.setMaxResults(1);

		// No obtener como lista, ya que solo puede devolver uno o null
		u = q.uniqueResult();
		
		if (u != null)
			Hibernate.initialize(u.getRole());

		session.close();

		return u;
	}

}
