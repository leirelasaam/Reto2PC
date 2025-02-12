package server.elorbase.entities;
// Generated 27 ene 2025, 19:50:40 by Hibernate Tools 6.5.1.Final

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Courses generated by hbm2java
 */
@Entity
public class Course implements java.io.Serializable {

	private static final long serialVersionUID = -8204058024530910569L;
	@Id
	private Long id;
	private String name;
	private Date date;
	private String contact;
	private String description;
	private String schedule;
	private float latitude;
	private float longitude;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public Course() {
	}

	public Course(String name, Date date, String contact, String description, String schedule, float latitude,
			float longitude) {
		this.name = name;
		this.date = date;
		this.contact = contact;
		this.description = description;
		this.schedule = schedule;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Course(String name, Date date, String contact, String description, String schedule, float latitude,
			float longitude, Timestamp createdAt, Timestamp updatedAt) {
		this.name = name;
		this.date = date;
		this.contact = contact;
		this.description = description;
		this.schedule = schedule;
		this.latitude = latitude;
		this.longitude = longitude;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSchedule() {
		return this.schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public float getLatitude() {
		return this.latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return this.longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
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
