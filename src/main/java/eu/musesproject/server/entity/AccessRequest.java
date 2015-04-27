package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the access_request database table.
 * 
 */
@Entity
@Table(name="access_request")
@NamedQueries ({
	@NamedQuery(name="AccessRequest.findAll", 
				query="SELECT a FROM AccessRequest a"),
				@NamedQuery(name="AccessRequest.findAccessrequestbyTimestampandThreat", 
				query="SELECT a FROM AccessRequest a where a.modification =:modification and a.threatId =:threat"),
	@NamedQuery(name="AccessRequest.findById", 
				query="SELECT a FROM AccessRequest a where a.accessRequestId = :access_request_id"),
	@NamedQuery(name="AccessRequest.findByEventId", 
				query="SELECT a FROM AccessRequest a where a.eventId = :event_id")

})
public class AccessRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="access_request_id", unique=true, nullable=false)
	private String accessRequestId;

	@Column(nullable=true, length=1)
	private String action;

	@Column(name="asset_id", nullable=false)
	private BigInteger assetId;

	@Column(name="decision_id")
	private BigInteger decisionId;

	@Column(name="event_id", nullable=false)
	private BigInteger eventId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	private int solved;

	@Column(name="threat_id")
	private int threatId;

	@Column(name="user_action")
	private int userAction;

	@Column(name="user_id", nullable=false)
	private BigInteger userId;

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

	public BigInteger getAssetId() {
		return this.assetId;
	}

	public void setAssetId(BigInteger assetId) {
		this.assetId = assetId;
	}

	public BigInteger getDecisionId() {
		return this.decisionId;
	}

	public void setDecisionId(BigInteger decisionId) {
		this.decisionId = decisionId;
	}

	public BigInteger getEventId() {
		return this.eventId;
	}

	public void setEventId(BigInteger eventId) {
		this.eventId = eventId;
	}

	public Date getModification() {
		return this.modification;
	}

	public void setModification(Date modification) {
		this.modification = modification;
	}

	public int getSolved() {
		return this.solved;
	}

	public void setSolved(int solved) {
		this.solved = solved;
	}

	public int getThreatId() {
		return this.threatId;
	}

	public void setThreatId(int threatId) {
		this.threatId = threatId;
	}

	public int getUserAction() {
		return this.userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public BigInteger getUserId() {
		return this.userId;
	}

	public void setUserId(BigInteger userId) {
		this.userId = userId;
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