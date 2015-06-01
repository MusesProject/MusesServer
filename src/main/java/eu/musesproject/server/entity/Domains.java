package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the domains database table.
 * 
 */
@Entity
@Table(name="domains")
@NamedQueries({
	@NamedQuery(name="Domains.findAll", 
				query="SELECT d FROM Domains d"),
	@NamedQuery(name="Domains.findByName", 
				query="SELECT d FROM Domains d where d.name = :name")
})
public class Domains implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="domain_id", unique=true, nullable=false)
	private int domainId;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=50)
	private String name;

	@Column(name="sensitivity_id", nullable=false, length=1)
	private String sensitivity;

	public Domains() {
	}

	public int getDomainId() {
		return this.domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
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

	public String getSensitivity() {
		return this.sensitivity;
	}

	public void setSensitivity(String sensitivity) {
		this.sensitivity = sensitivity;
	}

}