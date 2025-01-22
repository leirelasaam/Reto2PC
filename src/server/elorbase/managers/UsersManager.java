package server.elorbase.managers;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.entities.User;
import server.elorbase.utils.BcryptUtils;
import server.elorbase.utils.DBQueries;
import server.elorbase.utils.JSONUtils;

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

		if (u != null) {
	        Hibernate.initialize(u.getRole());  
	        Hibernate.initialize(u.getModules());  
	        Hibernate.initialize(u.getEnrollments()); 
	        Hibernate.initialize(u.getMeetings()); 
	    }
		
		session.close();

		return u;
	}

	public void updatePasswordByUser(User user, String password) {
		Session session = sesion.openSession();
		session.beginTransaction();
		
		String hashedPass = BcryptUtils.getHashedPass(password);

		try {
			if (user != null) {
				user.setPassword(hashedPass);
				session.merge(user);
				session.getTransaction().commit();

				System.out.println("Contraseña restablecida correctamente para el usuario: " + user.getEmail());
			}

		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

}
