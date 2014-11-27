package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Arrays;
import java.util.Date;


/**
 * The persistent class for the refined_security_rules database table.
 * 
 */
@Entity
@Table(name="refined_security_rules")
@NamedQueries ({
	@NamedQuery(name="RefinedSecurityRule.findAll", 
				query="SELECT r FROM RefinedSecurityRule r"),
	@NamedQuery(name="RefinedSecurityRule.findByStatus", 
				query="SELECT r FROM RefinedSecurityRule r where r.status = :status")
})
public class RefinedSecurityRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="refined_security_rules_id")
	private int refinedSecurityRulesId;

	@Lob
	private byte[] file;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	private String name;

	private String status;

	//bi-directional many-to-one association to SecurityRule
	@ManyToOne
	@JoinColumn(name="original_security_rule_id")
	private SecurityRule securityRule;

	public RefinedSecurityRule() {
	}

	public int getRefinedSecurityRulesId() {
		return this.refinedSecurityRulesId;
	}

	public void setRefinedSecurityRulesId(int refinedSecurityRulesId) {
		this.refinedSecurityRulesId = refinedSecurityRulesId;
	}

	public byte[] getFile() {
		return this.file;
	}

	public void setFile(byte[] file) {
		if (file == null) {
			this.file = new byte[0];
		} else {
			this.file = Arrays.copyOf(file, file.length);
		}
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

	public SecurityRule getSecurityRule() {
		return this.securityRule;
	}

	public void setSecurityRule(SecurityRule securityRule) {
		this.securityRule = securityRule;
	}

}