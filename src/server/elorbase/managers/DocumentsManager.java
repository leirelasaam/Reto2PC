package server.elorbase.managers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.elorbase.utils.DBQueries;


public class DocumentsManager {

	SessionFactory sesion = null;

	public DocumentsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}
	
	public ArrayList<Object> getDocumentsByUserId(int id) {
		ArrayList<Object> documents = null;

		Session session = sesion.openSession();
		String hql = DBQueries.DOCUMENTS_BY_STUDENT;
		Query<Object> q = session.createQuery(hql, Object.class);
		q.setParameter("id", id);
		
		List<?> filas = q.list();

		if (filas.size() > 0) {
			for (Object fila : filas) {
				fila.toString();
				if (documents == null)
					documents = new ArrayList<Object>();
				documents.add(fila);
			}
		}
		
		session.close();

		return documents;
	}

}