package server.elorbase.managers;

import org.hibernate.SessionFactory;


public class DocumentsManager {

	SessionFactory sesion = null;

	public DocumentsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

}