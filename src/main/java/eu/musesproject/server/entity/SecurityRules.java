package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the security_rules database table.
 * 
 */
@Entity
@Table(name="security_rules")
@NamedQueries ({
	@NamedQuery(name="SecurityRules.findAll", 
				query="SELECT s FROM SecurityRules s"),
	@NamedQuery(name="SecurityRules.findByStatus", 
				query="SELECT s FROM SecurityRules s where s.status = :status")
})
public class SecurityRules implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="security_rule_id", unique=true, nullable=false)
	private String securityRuleId;

	@Column(nullable=false, length=2000)
	private String description;

	@Lob
	private byte[] file;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date modification;

	@Column(nullable=false, length=2000)
	private String name;

	@Column(nullable=false)
	private byte[] refined;

	@Column(nullable=false, length=1)
	private String status;

	//bi-directional many-to-one association to RefinedSecurityRules
	@OneToMany(mappedBy="securityRule")
	private List<RefinedSecurityRules> refinedSecurityRules;

	//bi-directional many-to-one association to Sources
	@ManyToOne
	@JoinColumn(name="source_id")
	private Sources source;

	public SecurityRules() {
	}

	public String getSecurityRuleId() {
		return this.securityRuleId;
	}

	public void setSecurityRuleId(String securityRuleId) {
		this.securityRuleId = securityRuleId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getFile() {
		return this.file;
	}

	public void setFile(byte[] fileArray) {
		this.file = Arrays.copyOf(fileArray,fileArray.length);
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

	public byte[] getRefined() {
		return this.refined;
	}

	public void setRefined(byte[] refinedArray) {
		this.refined = Arrays.copyOf(refinedArray,refinedArray.length);
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<RefinedSecurityRules> getRefinedSecurityRules() {
		return this.refinedSecurityRules;
	}

	public void setRefinedSecurityRules(List<RefinedSecurityRules> refinedSecurityRules) {
		this.refinedSecurityRules = refinedSecurityRules;
	}

	public RefinedSecurityRules addRefinedSecurityRule(RefinedSecurityRules refinedSecurityRule) {
		getRefinedSecurityRules().add(refinedSecurityRule);
		refinedSecurityRule.setSecurityRule(this);

		return refinedSecurityRule;
	}

	public RefinedSecurityRules removeRefinedSecurityRule(RefinedSecurityRules refinedSecurityRule) {
		getRefinedSecurityRules().remove(refinedSecurityRule);
		refinedSecurityRule.setSecurityRule(null);

		return refinedSecurityRule;
	}

	public Sources getSource() {
		return this.source;
	}

	public void setSource(Sources source) {
		this.source = source;
	}

}