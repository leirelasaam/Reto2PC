package server.elorbase.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import server.elorbase.entities.User;
import server.elorbase.utils.BcryptUtil;
import server.elorbase.utils.DBQueries;

public class UsersManager {

	private static final Logger logger = Logger.getLogger(UsersManager.class);
	private SessionFactory sesion = null;

	public UsersManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	public User getByEmailOrPin(String login) {
		User u = null;
		Session session = null;
		
		try {
			session = sesion.openSession();
			String hql = DBQueries.USER_BY_EMAIL_OR_PIN;
			Query<User> q = session.createQuery(hql, User.class);
			q.setParameter("email", login.toLowerCase());
			q.setParameter("pin", login.toUpperCase());
			q.setMaxResults(1);

			// No obtener como lista, ya que solo puede devolver uno o null
			u = q.uniqueResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return u;
	}


	public List<User> getUsersByRole(long idRole) {

		Session session = sesion.openSession();
		String hql = DBQueries.U_BY_ROLE;
		Query<User> q = session.createQuery(hql, User.class);
		q.setParameter("roleId", idRole);
		List<User> users = q.getResultList();

		session.close();
		return users;
	}

	public void updatePasswordByUser(User user, String password) {
		Session session = null;
		Transaction transaction = null;

		try {
			session = sesion.openSession();
			transaction = session.beginTransaction();

			String hashedPass = BcryptUtil.getHashedPass(password);
			
			if (user != null) {
				user.setPassword(hashedPass);
				session.merge(user);
				session.getTransaction().commit();
				logger.info("Contraseña restablecida correctamente para el usuario: " + user.getEmail());
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
	}
	
	public User getUserById(int id) {
		User u = new User();
		Session session = null;
		
		try {
			session = sesion.openSession();
			String hql = DBQueries.USER_BY_ID;
			Query<User> q = session.createQuery(hql, User.class);
			q.setParameter("id", id);
			q.setMaxResults(1);

			// No obtener como lista, ya que solo puede devolver uno o null
			u = q.uniqueResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return u;
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
