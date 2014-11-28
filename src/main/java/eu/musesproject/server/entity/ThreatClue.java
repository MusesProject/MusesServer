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
	@Column(name="threat_clue_id", unique=true, nullable=false)
	private String threatClueId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date modification;

	@Column(nullable=false, length=50)
	private String name;

	//bi-directional many-to-one association to AccessRequest
	@ManyToOne
	@JoinColumn(name="access_request_id")
	private AccessRequest accessRequest;

	//bi-directional many-to-one association to Assets
	@ManyToOne
	@JoinColumn(name="asset_id")
	private Assets asset;

	//bi-directional many-to-one association to SimpleEvents
	@ManyToOne
	@JoinColumn(name="event_id")
	private SimpleEvents simpleEvent;

	//bi-directional many-to-one association to ThreatType
	@ManyToOne
	@JoinColumn(name="threat_type_id", nullable=false)
	private ThreatType threatType;

	//bi-directional many-to-one association to Users
	@ManyToOne
	@JoinColumn(name="user_id")
	private Users user;

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

	public AccessRequest getAccessRequest() {
		return this.accessRequest;
	}

	public void setAccessRequest(AccessRequest accessRequest) {
		this.accessRequest = accessRequest;
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

	public ThreatType getThreatType() {
		return this.threatType;
	}

	public void setThreatType(ThreatType threatType) {
		this.threatType = threatType;
	}

	public Users getUser() {
		return this.user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}