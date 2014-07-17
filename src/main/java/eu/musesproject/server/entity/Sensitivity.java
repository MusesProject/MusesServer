package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the sensitivity database table.
 * 
 */
@Entity
@NamedQuery(name="Sensitivity.findAll", query="SELECT s FROM Sensitivity s")
public class Sensitivity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="sensitivity_id")
	private int sensitivityId;

	private short level;

	private String name;

	//bi-directional many-to-one association to Domain
	@OneToMany(mappedBy="sensitivity")
	private List<Domain> domains;

	public Sensitivity() {
	}

	public int getSensitivityId() {
		return this.sensitivityId;
	}

	public void setSensitivityId(int sensitivityId) {
		this.sensitivityId = sensitivityId;
	}

	public short getLevel() {
		return this.level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Domain> getDomains() {
		return this.domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	public Domain addDomain(Domain domain) {
		getDomains().add(domain);
		domain.setSensitivity(this);

		return domain;
	}

	public Domain removeDomain(Domain domain) {
		getDomains().remove(domain);
		domain.setSensitivity(null);

		return domain;
	}

}