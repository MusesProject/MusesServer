package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the access_request database table.
 * 
 */
@Entity
@Table(name="access_request")
@NamedQuery(name="AccessRequest.findAll", query="SELECT a FROM AccessRequest a")
public class AccessRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="access_request_id")
	private String accessRequestId;

	private String action;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	//bi-directional many-to-one association to Asset
	@ManyToOne
	@JoinColumn(name="asset_id")
	private Asset asset;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	//bi-directional many-to-one association to Decision
	@ManyToOne
	@JoinColumn(name="decision_id")
	private Decision decision;

	//bi-directional many-to-one association to SimpleEvent
	@ManyToOne
	@JoinColumn(name="event_id")
	private SimpleEvent simpleEvent;

	//bi-directional many-to-one association to AdditionalProtection
	@OneToMany(mappedBy="accessRequest")
	private List<AdditionalProtection> additionalProtections;

	//bi-directional many-to-one association to Decision
	@OneToMany(mappedBy="accessRequest")
	private List<Decision> decisions;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="accessRequest")
	private List<ThreatClue> threatClues;

	public AccessRequest() {
	}

	public String getAccessRequestId() {
		return this.accessRequestId;
	}

	public void setAccessRequestId(String accessRequestId) {
		this.accessRequestId = accessRequestId;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getModification() {
		return this.modification;
	}

	public void setModification(Date modification) {
		this.modification = modification;
	}

	public Asset getAsset() {
		return this.asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Decision getDecision() {
		return this.decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	public SimpleEvent getSimpleEvent() {
		return this.simpleEvent;
	}

	public void setSimpleEvent(SimpleEvent simpleEvent) {
		this.simpleEvent = simpleEvent;
	}

	public List<AdditionalProtection> getAdditionalProtections() {
		return this.additionalProtections;
	}

	public void setAdditionalProtections(List<AdditionalProtection> additionalProtections) {
		this.additionalProtections = additionalProtections;
	}

	public AdditionalProtection addAdditionalProtection(AdditionalProtection additionalProtection) {
		getAdditionalProtections().add(additionalProtection);
		additionalProtection.setAccessRequest(this);

		return additionalProtection;
	}

	public AdditionalProtection removeAdditionalProtection(AdditionalProtection additionalProtection) {
		getAdditionalProtections().remove(additionalProtection);
		additionalProtection.setAccessRequest(null);

		return additionalProtection;
	}

	public List<Decision> getDecisions() {
		return this.decisions;
	}

	public void setDecisions(List<Decision> decisions) {
		this.decisions = decisions;
	}

	public Decision addDecision(Decision decision) {
		getDecisions().add(decision);
		decision.setAccessRequest(this);

		return decision;
	}

	public Decision removeDecision(Decision decision) {
		getDecisions().remove(decision);
		decision.setAccessRequest(null);

		return decision;
	}

	public List<ThreatClue> getThreatClues() {
		return this.threatClues;
	}

	public void setThreatClues(List<ThreatClue> threatClues) {
		this.threatClues = threatClues;
	}

	public ThreatClue addThreatClue(ThreatClue threatClue) {
		getThreatClues().add(threatClue);
		threatClue.setAccessRequest(this);

		return threatClue;
	}

	public ThreatClue removeThreatClue(ThreatClue threatClue) {
		getThreatClues().remove(threatClue);
		threatClue.setAccessRequest(null);

		return threatClue;
	}

}