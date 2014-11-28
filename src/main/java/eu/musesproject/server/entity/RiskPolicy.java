package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the risk_policy database table.
 * 
 */
@Entity
@Table(name="risk_policy")
@NamedQuery(name="RiskPolicy.findAll", query="SELECT r FROM RiskPolicy r")
public class RiskPolicy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="risk_policy_id", unique=true, nullable=false)
	private String riskPolicyId;

	@Lob
	@Column(nullable=false)
	private String description;

	@Column(nullable=false)
	private double riskvalue;

	public RiskPolicy() {
	}

	public String getRiskPolicyId() {
		return this.riskPolicyId;
	}

	public void setRiskPolicyId(String riskPolicyId) {
		this.riskPolicyId = riskPolicyId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRiskvalue() {
		return this.riskvalue;
	}

	public void setRiskvalue(double riskvalue) {
		this.riskvalue = riskvalue;
	}

}