package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the decision_trustvalues database table.
 * 
 */
@Entity
@Table(name="decision_trustvalues")
@NamedQuery(name="DecisionTrustvalues.findAll", query="SELECT d FROM DecisionTrustvalues d")
public class DecisionTrustvalues implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="decision_trustvalue_id")
	private int decisionTrustvalueId;

	@Column(name="decision_id")
	private int decisionId;

	private double devicetrustvalue;

	private double usertrustvalue;

	public DecisionTrustvalues() {
	}

	public int getDecisionTrustvalueId() {
		return this.decisionTrustvalueId;
	}

	public void setDecisionTrustvalueId(int decisionTrustvalueId) {
		this.decisionTrustvalueId = decisionTrustvalueId;
	}

	public int getDecisionId() {
		return this.decisionId;
	}

	public void setDecisionId(int decisionId) {
		this.decisionId = decisionId;
	}

	public double getDevicetrustvalue() {
		return this.devicetrustvalue;
	}

	public void setDevicetrustvalue(double devicetrustvalue) {
		this.devicetrustvalue = devicetrustvalue;
	}

	public double getUsertrustvalue() {
		return this.usertrustvalue;
	}

	public void setUsertrustvalue(double usertrustvalue) {
		this.usertrustvalue = usertrustvalue;
	}

}