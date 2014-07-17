package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the risk_treatment database table.
 * 
 */
@Entity
@Table(name="risk_treatment")
@NamedQuery(name="RiskTreatment.findAll", query="SELECT r FROM RiskTreatment r")
public class RiskTreatment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="risk_treatment_id")
	private int riskTreatmentId;

	private String description;

	//bi-directional many-to-one association to RiskCommunication
	@ManyToOne
	@JoinColumn(name="risk_communication_id")
	private RiskCommunication riskCommunication;

	public RiskTreatment() {
	}

	public int getRiskTreatmentId() {
		return this.riskTreatmentId;
	}

	public void setRiskTreatmentId(int riskTreatmentId) {
		this.riskTreatmentId = riskTreatmentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RiskCommunication getRiskCommunication() {
		return this.riskCommunication;
	}

	public void setRiskCommunication(RiskCommunication riskCommunication) {
		this.riskCommunication = riskCommunication;
	}

}