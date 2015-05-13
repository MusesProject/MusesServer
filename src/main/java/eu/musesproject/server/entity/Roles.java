package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name="roles")
@NamedQueries({
	@NamedQuery(name="Roles.findAll", 
				query="SELECT r FROM Roles r"),
	@NamedQuery(name="Roles.findByName", 
				query="SELECT r FROM Roles r where r.name = :name"),
	@NamedQuery(name="Roles.findById", 
				query="SELECT r FROM Roles r where r.roleId = :role_id")
})
public class Roles implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="role_id", unique=true, nullable=false)
	private int roleId;

	@Column(length=100)
	private String description;

	@Column(length=50)
	private String name;

	@Column(name="security_level")
	private short securityLevel;

	public Roles() {
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getSecurityLevel() {
		return this.securityLevel;
	}

	public void setSecurityLevel(short securityLevel) {
		this.securityLevel = securityLevel;
	}

}