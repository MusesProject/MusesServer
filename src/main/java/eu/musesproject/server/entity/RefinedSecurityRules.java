package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the refined_security_rules database table.
 * 
 */
@Entity
@Table(name="refined_security_rules")
@NamedQueries ({
	@NamedQuery(name="RefinedSecurityRules.findAll", 
				query="SELECT r FROM RefinedSecurityRules r"),
	@NamedQuery(name="RefinedSecurityRules.findByStatus", 
				query="SELECT r FROM RefinedSecurityRules r where r.status = :status")
})
public class RefinedSecurityRules implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="refined_security_rules_id", unique=true, nullable=false)
	private String refinedSecurityRulesId;

	@Lob
	private byte[] file;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date modification;

	@Column(nullable=false, length=2000)
	private String name;

	@Column(nullable=false, length=1)
	private String status;

	//bi-directional many-to-one association to SecurityRules
	@ManyToOne
	@JoinColumn(name="original_security_rule_id", nullable=false)
	private SecurityRules securityRule;

	public RefinedSecurityRules() {
	}

	public String getRefinedSecurityRulesId() {
		return this.refinedSecurityRulesId;
	}

	public void setRefinedSecurityRulesId(String refinedSecurityRulesId) {
		this.refinedSecurityRulesId = refinedSecurityRulesId;
	}

	public byte[] getFile() {
		return this.file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public Date getModification() {
		return this.modification;
	}

	public void setModification(Date modification) {
		this.modification = modification;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SecurityRules getSecurityRule() {
		return this.securityRule;
	}

	public void setSecurityRule(SecurityRules securityRule) {
		this.securityRule = securityRule;
	}

}