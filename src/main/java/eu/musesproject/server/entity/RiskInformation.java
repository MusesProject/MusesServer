package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the risk_information database table.
 * 
 */
@Entity
@Table(name="risk_information")
@NamedQuery(name="RiskInformation.findAll", query="SELECT r FROM RiskInformation r")
public class RiskInformation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="risk_information_id")
	private String riskInformationId;

	private double probability;

	//bi-directional many-to-one association to ThreatType
	@ManyToOne
	@JoinColumn(name="threat_type")
	private ThreatType threatTypeBean;

	//bi-directional many-to-one association to Asset
	@ManyToOne
	@JoinColumn(name="asset_id")
	private Asset asset;

	//bi-directional many-to-one association to SimpleEvent
	@ManyToOne
	@JoinColumn(name="event_id")
	private SimpleEvent simpleEvent;

	public RiskInformation() {
	}

	public String getRiskInformationId() {
		return this.riskInformationId;
	}

	public void setRiskInformationId(String riskInformationId) {
		this.riskInformationId = riskInformationId;
	}

	public double getProbability() {
		return this.probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public ThreatType getThreatTypeBean() {
		return this.threatTypeBean;
	}

	public void setThreatTypeBean(ThreatType threatTypeBean) {
		this.threatTypeBean = threatTypeBean;
	}

	public Asset getAsset() {
		return this.asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public SimpleEvent getSimpleEvent() {
		return this.simpleEvent;
	}

	public void setSimpleEvent(SimpleEvent simpleEvent) {
		this.simpleEvent = simpleEvent;
	}

}