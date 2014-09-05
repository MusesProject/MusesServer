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
	@Column(name="risk_policy_id")
	private int riskPolicyId;

	@Lob
	private String description;

	private double riskvalue;

	public RiskPolicy() {
	}

	public int getRiskPolicyId() {
		return this.riskPolicyId;
	}

	public void setRiskPolicyId(int riskPolicyId) {
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