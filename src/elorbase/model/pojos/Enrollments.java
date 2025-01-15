package elorbase.model.pojos;
// Generated 15 ene 2025, 19:12:17 by Hibernate Tools 6.5.1.Final

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Enrollments generated by hbm2java
 */
public class Enrollments implements java.io.Serializable {

	private Long id;
	private Modules modules;
	private Users users;
	private Date date;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public Enrollments() {
	}

	public Enrollments(Modules modules, Users users, Date date) {
		this.modules = modules;
		this.users = users;
		this.date = date;
	}

	public Enrollments(Modules modules, Users users, Date date, Timestamp createdAt, Timestamp updatedAt) {
		this.modules = modules;
		this.users = users;
		this.date = date;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Modules getModules() {
		return this.modules;
	}

	public void setModules(Modules modules) {
		this.modules = modules;
	}

	public Users getUsers() {
		return this.users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
