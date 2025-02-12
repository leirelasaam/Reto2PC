package server.elorbase.entities;
// Generated 18 ene 2025, 9:48:26 by Hibernate Tools 6.5.1.Final

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

/**
 * Users generated by hbm2java
 */
@Entity
public class User implements java.io.Serializable {

	private static final long serialVersionUID = 8633915848476097381L;
	
	@Id
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Role role;
	private String name;
	private String email;
	private Timestamp emailVerifiedAt;
	private String password;
	private String rememberToken;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private Timestamp deletedAt;
	private String lastname;
	private String pin;
	private String address;
	private String phone1;
	private String phone2;
	private byte[] photo;
	private boolean intensive;
	private boolean registered;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@JsonBackReference
	private Set<Module> modules = new HashSet<Module>(0);
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@JsonBackReference
	private Set<Enrollment> enrollments = new HashSet<Enrollment>(0);
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@JsonBackReference
	@Transient
	private Set<Meeting> meetings = new HashSet<Meeting>(0);

	public User() {
	}

	public User(String name, String email, String password, String lastname, String pin, String address, String phone1,
			boolean intensive, boolean registered) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.lastname = lastname;
		this.pin = pin;
		this.address = address;
		this.phone1 = phone1;
		this.intensive = intensive;
		this.registered = registered;
	}

	public User(Role role, String name, String email, Timestamp emailVerifiedAt, String password,
			String rememberToken, Timestamp createdAt, Timestamp updatedAt, Timestamp deletedAt, String lastname,
			String pin, String address, String phone1, String phone2, byte[] photo, boolean intensive,
			boolean registered, Set<Module> modules, Set<Enrollment> enrollments, Set<Meeting> meetings) {
		this.role = role;
		this.name = name;
		this.email = email;
		this.emailVerifiedAt = emailVerifiedAt;
		this.password = password;
		this.rememberToken = rememberToken;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
		this.lastname = lastname;
		this.pin = pin;
		this.address = address;
		this.phone1 = phone1;
		this.phone2 = phone2;
		this.photo = photo;
		this.intensive = intensive;
		this.registered = registered;
		this.modules = modules;
		this.enrollments = enrollments;
		this.meetings = meetings;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getEmailVerifiedAt() {
		return this.emailVerifiedAt;
	}

	public void setEmailVerifiedAt(Timestamp emailVerifiedAt) {
		this.emailVerifiedAt = emailVerifiedAt;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRememberToken() {
		return this.rememberToken;
	}

	public void setRememberToken(String rememberToken) {
		this.rememberToken = rememberToken;
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

	public Timestamp getDeletedAt() {
		return this.deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPin() {
		return this.pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone1() {
		return this.phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public byte[] getPhoto() {
		return this.photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public boolean isIntensive() {
		return this.intensive;
	}

	public void setIntensive(boolean intensive) {
		this.intensive = intensive;
	}

	public boolean isRegistered() {
		return this.registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public Set<Module> getModules() {
		return this.modules;
	}

	public void setModules(Set<Module> modules) {
		this.modules = modules;
	}

	public Set<Enrollment> getEnrollments() {
		return this.enrollments;
	}

	public void setEnrollments(Set<Enrollment> enrollments) {
		this.enrollments = enrollments;
	}

	public Set<Meeting> getMeetings() {
		return this.meetings;
	}

	public void setMeetings(Set<Meeting> meetings) {
		this.meetings = meetings;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", emailVerifiedAt=" + emailVerifiedAt
				+ ", password=" + password + ", rememberToken=" + rememberToken + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt + ", lastname=" + lastname + ", pin=" + pin
				+ ", address=" + address + ", phone1=" + phone1 + ", phone2=" + phone2 + ", photo="
				+ Arrays.toString(photo) + ", intensive=" + intensive + ", registered=" + registered + "]";
	}
	
	

}
