package server.elorbase.managers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.model.Users;
import server.elorbase.utils.DBQueries;

public class UsersManager {

	SessionFactory sesion = null;

	public UsersManager(SessionFactory sesion) {
		this.sesion = sesion;
	}
	
	public Users getByEmail(String email) {
		Users u = null;

		Session session = sesion.openSession();
		String hql = DBQueries.U_BY_EMAIL;
		Query<Users> q = session.createQuery(hql, Users.class);
		q.setParameter("email", email);
		q.setMaxResults(1);

		// No obtener como lista, ya que solo puede devolver uno o null
		u = q.uniqueResult();

		session.close();

		return u;
	}

}
