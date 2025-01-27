package server.elorbase.managers;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.entities.User;
import server.elorbase.utils.BcryptUtil;
import server.elorbase.utils.DBQueries;
import server.elorbase.utils.JSONUtil;

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
		
		String hashedPass = BcryptUtil.getHashedPass(password);

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

	//Comprobar que las funciones funcionan correctamente
	public boolean updateUser(User updatedUser) {
	    Session session = sesion.openSession();
	    session.beginTransaction();

	    try {
	        if (updatedUser != null) {
	            // Actualiza el usuario en la base de datos usando `merge`
	            session.merge(updatedUser);
	            session.getTransaction().commit();
	            System.out.println("Usuario actualizado correctamente: " + updatedUser.getEmail());
	            return true;
	        } else {
	            System.out.println("El usuario proporcionado es nulo. No se puede actualizar.");
	            return false;
	        }
	    } catch (Exception e) {
	        // En caso de error, realiza un rollback
	        session.getTransaction().rollback();
	        System.err.println("Error actualizando usuario: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    } finally {
	        // Cierra la sesión para liberar recursos
	        session.close();
	    }
	}


}
