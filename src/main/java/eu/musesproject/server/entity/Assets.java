package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the assets database table.
 * 
 */
@Entity
@Table(name="assets")
@NamedQueries({
	@NamedQuery(name="Assets.findAll", 
			    query="SELECT a FROM Assets a"),
	@NamedQuery(name="Assets.findByLocation", 
	 			query="SELECT a FROM Assets a where a.location = :location"),
	@NamedQuery(name="Assets.findByTitle", 
				query="SELECT a FROM Assets a where a.title = :title"),
	@NamedQuery(name="Assets.deleteAssetByTitle", 
				query="delete FROM Assets a where a.title = :title")
})
public class Assets implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="asset_id", unique=true, nullable=false)
	private String assetId;

	@Column(name="confidential_level", nullable=false, length=1)
	private String confidentialLevel;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=100)
	private String location;

	@Column(nullable=false, length=30)
	private String title;

	@Column(nullable=false)
	private double value;

	//bi-directional many-to-one association to RiskInformation
	@OneToMany(mappedBy="asset")
	private List<RiskInformation> riskInformations;

	//bi-directional many-to-one association to SimpleEvents
	@OneToMany(mappedBy="asset")
	private List<SimpleEvents> simpleEvents;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="asset")
	private List<ThreatClue> threatClues;

	public Assets() {
	}

	public String getAssetId() {
		return this.assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getConfidentialLevel() {
		return this.confidentialLevel;
	}

	public void setConfidentialLevel(String confidentialLevel) {
		this.confidentialLevel = confidentialLevel;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public List<RiskInformation> getRiskInformations() {
		return this.riskInformations;
	}

	public void setRiskInformations(List<RiskInformation> riskInformations) {
		this.riskInformations = riskInformations;
	}

	public RiskInformation addRiskInformation(RiskInformation riskInformation) {
		getRiskInformations().add(riskInformation);
		riskInformation.setAsset(this);

		return riskInformation;
	}

	public RiskInformation removeRiskInformation(RiskInformation riskInformation) {
		getRiskInformations().remove(riskInformation);
		riskInformation.setAsset(null);

		return riskInformation;
	}

	public List<SimpleEvents> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvents> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvents addSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setAsset(this);

		return simpleEvent;
	}

	public SimpleEvents removeSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setAsset(null);

		return simpleEvent;
	}

	public List<ThreatClue> getThreatClues() {
		return this.threatClues;
	}

	public void setThreatClues(List<ThreatClue> threatClues) {
		this.threatClues = threatClues;
	}

	public ThreatClue addThreatClue(ThreatClue threatClue) {
		getThreatClues().add(threatClue);
		threatClue.setAsset(this);

		return threatClue;
	}

	public ThreatClue removeThreatClue(ThreatClue threatClue) {
		getThreatClues().remove(threatClue);
		threatClue.setAsset(null);

		return threatClue;
	}

}