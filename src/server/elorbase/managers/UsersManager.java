package server.elorbase.managers;

import java.util.Base64;
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

			// Actualizar los atributos del usuario
			if (updatedUser != null) {
				// Recuperar el usuario de la base de datos
	            User existingUser = session.get(User.class, updatedUser.getId());
				if (existingUser != null) {
					if (updatedUser.getName() != null) {
	                    existingUser.setName(updatedUser.getName());
	                }
	                if (updatedUser.getEmail() != null) {
	                    existingUser.setEmail(updatedUser.getEmail());
	                }
	                if (updatedUser.getPassword() != null) {
	                    existingUser.setPassword(updatedUser.getPassword());
	                }
	                if (updatedUser.getLastname() != null) {
	                    existingUser.setLastname(updatedUser.getLastname());
	                }
	                if (updatedUser.getPin() != null) {
	                    existingUser.setPin(updatedUser.getPin());
	                }
	                if (updatedUser.getAddress() != null) {
	                    existingUser.setAddress(updatedUser.getAddress());
	                }
	                if (updatedUser.getPhone1() != null) {
	                    existingUser.setPhone1(updatedUser.getPhone1());
	                }
	                if (updatedUser.getPhone2() != null) {
	                    existingUser.setPhone2(updatedUser.getPhone2());
	                }
	                if (updatedUser.getPhoto() != null) {
	                    existingUser.setPhoto(updatedUser.getPhoto());
	                }

	                existingUser.setRegistered(true);

					transaction.commit();
					logger.info("Usuario actualizado correctamente: " + updatedUser.getEmail());
					isUpdated = true;
				} else {
					logger.error("No se ha encontrado el usuario.");
				}
			} else {
				logger.error("El usuario proporcionado es nulo. No se puede actualizar.");
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
