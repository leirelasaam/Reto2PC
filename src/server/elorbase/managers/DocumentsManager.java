package server.elorbase.managers;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.config.ServerConfig;
import server.elorbase.entities.Document;
import server.elorbase.utils.DBQueries;

public class DocumentsManager {

	private static final Logger logger = Logger.getLogger(DocumentsManager.class);
	private SessionFactory sesion = null;

	public DocumentsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	public ArrayList<Document> getDocumentsByUserId(int id) {
		ArrayList<Document> documents = null;
		Session session = null;

		try {
			session = sesion.openSession();
			String hql = DBQueries.DOCUMENTS_BY_STUDENT;
			Query<Document> q = session.createQuery(hql, Document.class);
			q.setParameter("id", id);

			List<Document> filas = q.list();

			if (filas.size() > 0) {
				for (Document fila : filas) {
					if (documents == null)
						documents = new ArrayList<Document>();
					
					String path = ServerConfig.ELORDOCS + fila.getRoute() + "." + fila.getExtension();
					File file = new File(path);
					byte[] fileContent = Files.readAllBytes(file.toPath());

					fila.setFile(fileContent);

					documents.add(fila);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return documents;
	}

}