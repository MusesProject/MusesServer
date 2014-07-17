package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the domains database table.
 * 
 */
@Entity
@Table(name="domains")
@NamedQuery(name="Domain.findAll", query="SELECT d FROM Domain d")
public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="domain_id")
	private int domainId;

	private String description;

	private String name;

	//bi-directional many-to-one association to Sensitivity
	@ManyToOne
	@JoinColumn(name="sensitivity_id")
	private Sensitivity sensitivity;

	public Domain() {
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

	public Sensitivity getSensitivity() {
		return this.sensitivity;
	}

	public void setSensitivity(Sensitivity sensitivity) {
		this.sensitivity = sensitivity;
	}

}