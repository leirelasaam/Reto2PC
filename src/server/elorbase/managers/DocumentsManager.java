package server.elorbase.managers;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import server.config.ServerConfig;
import server.elorbase.entities.Document;
import server.elorbase.utils.DBQueries;

public class DocumentsManager {

	SessionFactory sesion = null;

	public DocumentsManager(SessionFactory sesion) {
		this.sesion = sesion;
	}

	public ArrayList<Document> getDocumentsByUserId(int id) {
		ArrayList<Document> documents = null;
		Session session = sesion.openSession();

		try {
			String hql = DBQueries.DOCUMENTS_BY_STUDENT;
			Query<Document> q = session.createQuery(hql, Document.class);
			q.setParameter("id", id);

			List<Document> filas = q.list();

			if (filas.size() > 0) {
				for (Document fila : filas) {
					if (documents == null)
						documents = new ArrayList<Document>();
					String path = ServerConfig.MODULE_FILES + fila.getRoute() + "." + fila.getExtension();
					File file = new File(path);
					byte[] fileContent = Files.readAllBytes(file.toPath());

					fila.setFile(fileContent);

					documents.add(fila);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return documents;
	}

}