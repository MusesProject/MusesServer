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
	@NamedQuery(name="Asset.findAll", 
			    query="SELECT a FROM Asset a"),
	@NamedQuery(name="Asset.findByLocation", 
	 			query="SELECT a FROM Asset a where a.location = :location"),
	@NamedQuery(name="Asset.findBytitle", 
		query="SELECT a FROM Asset a where a.title = :title")
})
public class Asset implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="asset_id")
	private int assetId;

	@Column(name="confidential_level")
	private String confidentialLevel;

	private String description;

	private String location;

	private String title;

	private double value;

	//bi-directional many-to-one association to AccessRequest
	@OneToMany(mappedBy="asset")
	private List<AccessRequest> accessRequests;

	//bi-directional many-to-one association to RiskInformation
	@OneToMany(mappedBy="asset")
	private List<RiskInformation> riskInformations;

	//bi-directional many-to-one association to SimpleEvent
	@OneToMany(mappedBy="asset")
	private List<SimpleEvent> simpleEvents;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="asset")
	private List<ThreatClue> threatClues;

	public Asset() {
	}

	public int getAssetId() {
		return this.assetId;
	}

	public void setAssetId(int assetId) {
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

	public List<AccessRequest> getAccessRequests() {
		return this.accessRequests;
	}

	public void setAccessRequests(List<AccessRequest> accessRequests) {
		this.accessRequests = accessRequests;
	}

	public AccessRequest addAccessRequest(AccessRequest accessRequest) {
		getAccessRequests().add(accessRequest);
		accessRequest.setAsset(this);

		return accessRequest;
	}

	public AccessRequest removeAccessRequest(AccessRequest accessRequest) {
		getAccessRequests().remove(accessRequest);
		accessRequest.setAsset(null);

		return accessRequest;
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

	public List<SimpleEvent> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvent> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvent addSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setAsset(this);

		return simpleEvent;
	}

	public SimpleEvent removeSimpleEvent(SimpleEvent simpleEvent) {
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