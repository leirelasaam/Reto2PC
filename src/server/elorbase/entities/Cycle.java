package server.elorbase.entities;
// Generated 18 ene 2025, 9:48:26 by Hibernate Tools 6.5.1.Final

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Cycles generated by hbm2java
 */
@Entity
public class Cycle implements java.io.Serializable {

	private static final long serialVersionUID = -8590566815223039499L;
	
	@Id
	private Long id;
	
	private String code;
	private String name;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	
	@OneToMany(mappedBy = "cycle", fetch = FetchType.LAZY)
	@JsonBackReference
	private Set<Module> modules = new HashSet<Module>(0);

	public Cycle() {
	}

	public Cycle(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public Cycle(String code, String name, Timestamp createdAt, Timestamp updatedAt, Set<Module> modules) {
		this.code = code;
		this.name = name;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.modules = modules;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Set<Module> getModules() {
		return this.modules;
	}

	public void setModules(Set<Module> modules) {
		this.modules = modules;
	}

}
