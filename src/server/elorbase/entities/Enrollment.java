package server.elorbase.entities;
// Generated 18 ene 2025, 9:48:26 by Hibernate Tools 6.5.1.Final

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Enrollments generated by hbm2java
 */
@Entity
@Table(name = "enrollments")
public class Enrollment implements java.io.Serializable {

	private Long id;
	private Module module;
	private User user;
	private Date date;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public Enrollment() {
	}

	public Enrollment(Module module, User user, Date date) {
		this.module = module;
		this.user = user;
		this.date = date;
	}

	public Enrollment(Module module, User user, Date date, Timestamp createdAt, Timestamp updatedAt) {
		this.module = module;
		this.user = user;
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

	public Module getModule() {
		return this.module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
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
