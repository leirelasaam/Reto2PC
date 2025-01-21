package server.elorbase.managers;

import java.util.List;

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

	public List<Users> getAllByRole(long idRole) {

		Session session = sesion.openSession();
		String hql = DBQueries.U_BY_ROLE;
		Query<Users> q = session.createQuery(hql, Users.class);
		q.setParameter("roleId", idRole);
		List<Users> users = q.getResultList();

		session.close();
		return users;
	}
}
