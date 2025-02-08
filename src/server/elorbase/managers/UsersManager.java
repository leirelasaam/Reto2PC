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
			if (session != null) {
				session.close();
			}
		}

		return u;
	}

	public List<User> getUsersByRole(long idRole) {
		Session session = null;
		List<User> users = null;

		try {
			session = sesion.openSession();

			String hql = DBQueries.USER_BY_ROLE;
			Query<User> q = session.createQuery(hql, User.class);
			q.setParameter("roleId", idRole);
			users = q.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
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
				logger.info("Contraseña restablecida correctamente para el usuario: " + user.getEmail());
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public User getUserById(int id) {
		User u = null;
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
			if (session != null) {
				session.close();
			}
		}

		return u;
	}

	public boolean updateUser(User updatedUser) {
		boolean isUpdated = false;
		Session session = null;
		Transaction transaction = null;

		try {
			session = sesion.openSession();
			transaction = session.beginTransaction();

			if (updatedUser != null) {
				session.merge(updatedUser);
				transaction.commit();
				logger.info("Usuario actualizado correctamente: " + updatedUser.getEmail());
				isUpdated = true;
			} else {
				logger.info("El usuario proporcionado es nulo. No se puede actualizar.");
			}
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return isUpdated;
	}

}
