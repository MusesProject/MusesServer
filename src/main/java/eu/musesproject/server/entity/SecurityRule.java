package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the security_rules database table.
 * 
 */
@Entity
@Table(name="security_rules")
@NamedQuery(name="SecurityRule.findAll", query="SELECT s FROM SecurityRule s")
public class SecurityRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="security_rule_id")
	private String securityRuleId;

	private String description;

	@Lob
	private byte[] file;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	private String name;

	private byte[] refined;

	private String status;

	//bi-directional many-to-one association to RefinedSecurityRule
	@OneToMany(mappedBy="securityRule")
	private List<RefinedSecurityRule> refinedSecurityRules;

	//bi-directional many-to-one association to Source
	@ManyToOne
	@JoinColumn(name="source_id")
	private Source source;

	public SecurityRule() {
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

	public byte[] getRefined() {
		return this.refined;
	}

	public void setRefined(byte[] refined) {
		this.refined = refined;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<RefinedSecurityRule> getRefinedSecurityRules() {
		return this.refinedSecurityRules;
	}

	public void setRefinedSecurityRules(List<RefinedSecurityRule> refinedSecurityRules) {
		this.refinedSecurityRules = refinedSecurityRules;
	}

	public RefinedSecurityRule addRefinedSecurityRule(RefinedSecurityRule refinedSecurityRule) {
		getRefinedSecurityRules().add(refinedSecurityRule);
		refinedSecurityRule.setSecurityRule(this);

		return refinedSecurityRule;
	}

	public RefinedSecurityRule removeRefinedSecurityRule(RefinedSecurityRule refinedSecurityRule) {
		getRefinedSecurityRules().remove(refinedSecurityRule);
		refinedSecurityRule.setSecurityRule(null);

		return refinedSecurityRule;
	}

	public Source getSource() {
		return this.source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

}