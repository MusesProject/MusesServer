package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the threat_clue database table.
 * 
 */
@Entity
@Table(name="threat_clue")
@NamedQuery(name="ThreatClue.findAll", query="SELECT t FROM ThreatClue t")
public class ThreatClue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="threat_clue_id")
	private String threatClueId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	private String name;

	//bi-directional many-to-one association to ThreatType
	@ManyToOne
	@JoinColumn(name="threat_type_id")
	private ThreatType threatType;

	//bi-directional many-to-one association to AccessRequest
	@ManyToOne
	@JoinColumn(name="access_request_id")
	private AccessRequest accessRequest;

	//bi-directional many-to-one association to Asset
	@ManyToOne
	@JoinColumn(name="asset_id")
	private Asset asset;

	//bi-directional many-to-one association to SimpleEvent
	@ManyToOne
	@JoinColumn(name="event_id")
	private SimpleEvent simpleEvent;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public ThreatClue() {
	}

	public String getThreatClueId() {
		return this.threatClueId;
	}

	public void setThreatClueId(String threatClueId) {
		this.threatClueId = threatClueId;
	}

	public Date getModification() {
		return this.modification;
	}

	public void setModification(Date modification) {
		this.modification = modification;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ThreatType getThreatType() {
		return this.threatType;
	}

	public void setThreatType(ThreatType threatType) {
		this.threatType = threatType;
	}

	public AccessRequest getAccessRequest() {
		return this.accessRequest;
	}

	public void setAccessRequest(AccessRequest accessRequest) {
		this.accessRequest = accessRequest;
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

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}