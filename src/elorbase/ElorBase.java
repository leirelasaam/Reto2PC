package elorbase;

import org.hibernate.SessionFactory;

import elorbase.managers.UsersManager;
import elorbase.model.pojos.Users;
import elorbase.utils.HibernateUtil;

public class ElorBase {
	public static void main(String[] args) {
		SessionFactory sesion = HibernateUtil.getSessionFactory();
		UsersManager um = new UsersManager(sesion);
		
		Users u = um.getByEmail("god@admin.com");
		System.out.println(u.toStringSimple());
		
		// Cerrar la sesión
		sesion.close();
	}
}
