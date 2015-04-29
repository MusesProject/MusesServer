package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the risk_information database table.
 * 
 */
@Entity
@Table(name="risk_information")
@NamedQueries ({
	@NamedQuery(name="RiskInformation.findAll", 
				query="SELECT r FROM RiskInformation r"),
	@NamedQuery(name="RiskInformation.findByEventId", 
    			query="SELECT r FROM RiskInformation r where r.simpleEvent = :event_id")

})
public class RiskInformation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="risk_information_id", unique=true, nullable=false)
	private String riskInformationId;

	@Column(nullable=false)
	private double probability;

	//bi-directional many-to-one association to Assets
	@ManyToOne
	@JoinColumn(name="asset_id", nullable=false)
	private Assets asset;

	//bi-directional many-to-one association to SimpleEvents
	@ManyToOne
	@JoinColumn(name="event_id", nullable=false)
	private SimpleEvents simpleEvent;

	//bi-directional many-to-one association to ThreatType
	@ManyToOne
	@JoinColumn(name="threat_type", nullable=false)
	private ThreatType threatTypeBean;

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

	public Assets getAsset() {
		return this.asset;
	}

	public void setAsset(Assets asset) {
		this.asset = asset;
	}

	public SimpleEvents getSimpleEvent() {
		return this.simpleEvent;
	}

	public void setSimpleEvent(SimpleEvents simpleEvent) {
		this.simpleEvent = simpleEvent;
	}

	public ThreatType getThreatTypeBean() {
		return this.threatTypeBean;
	}

	public void setThreatTypeBean(ThreatType threatTypeBean) {
		this.threatTypeBean = threatTypeBean;
	}

}