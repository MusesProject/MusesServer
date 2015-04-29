package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Time;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the simple_events database table.
 * 
 */
@Entity
@Table(name="simple_events")
@NamedQueries({
	@NamedQuery(name="SimpleEvents.findAll",
				query="SELECT s FROM SimpleEvents s"),
	@NamedQuery(name="SimpleEvents.findLastByUserId", 
    			query="SELECT s FROM SimpleEvents s where s.user = :user_id and (s.date = :day and s.time < :time) or s.date < :day"),
	@NamedQuery(name="SimpleEvents.findNextByUserId", 
    			query="SELECT s FROM SimpleEvents s where s.user = :user_id and (s.date = :day and s.time > :time) or s.date > :day")
})

public class SimpleEvents implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_id", unique=true, nullable=false)
	private String eventId;

	@Column(nullable=false, length=5000)
	private String data;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date date;

	private int duration;

	private int EP_can_access;

	private int KRS_can_access;

	private int RT2AE_can_access;

	@Column(nullable=false)
	private Time time;

	//bi-directional many-to-one association to AdditionalProtection
	@OneToMany(mappedBy="simpleEvent")
	private List<AdditionalProtection> additionalProtections;

	//bi-directional many-to-one association to RiskInformation
	@OneToMany(mappedBy="simpleEvent")
	private List<RiskInformation> riskInformations;

	//bi-directional many-to-one association to SecurityIncident
	@OneToMany(mappedBy="simpleEvent")
	private List<SecurityIncident> securityIncidents;

	//bi-directional many-to-one association to Applications
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	private Applications application;

	//bi-directional many-to-one association to Assets
	@ManyToOne
	@JoinColumn(name="asset_id", nullable=false)
	private Assets asset;

	//bi-directional many-to-one association to Devices
	@ManyToOne
	@JoinColumn(name="device_id", nullable=false)
	private Devices device;

	//bi-directional many-to-one association to EventType
	@ManyToOne
	@JoinColumn(name="event_type_id", nullable=false)
	private EventType eventType;

	//bi-directional many-to-one association to Sources
	@ManyToOne
	@JoinColumn(name="source_id")
	private Sources source;

	//bi-directional many-to-one association to Users
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private Users user;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="simpleEvent")
	private List<ThreatClue> threatClues;

	public SimpleEvents() {
	}

	public String getEventId() {
		return this.eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getEP_can_access() {
		return this.EP_can_access;
	}

	public void setEP_can_access(int EP_can_access) {
		this.EP_can_access = EP_can_access;
	}

	public int getKRS_can_access() {
		return this.KRS_can_access;
	}

	public void setKRS_can_access(int KRS_can_access) {
		this.KRS_can_access = KRS_can_access;
	}

	public int getRT2AE_can_access() {
		return this.RT2AE_can_access;
	}

	public void setRT2AE_can_access(int RT2AE_can_access) {
		this.RT2AE_can_access = RT2AE_can_access;
	}

	public Time getTime() {
		return this.time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public List<AdditionalProtection> getAdditionalProtections() {
		return this.additionalProtections;
	}

	public void setAdditionalProtections(List<AdditionalProtection> additionalProtections) {
		this.additionalProtections = additionalProtections;
	}

	public AdditionalProtection addAdditionalProtection(AdditionalProtection additionalProtection) {
		getAdditionalProtections().add(additionalProtection);
		additionalProtection.setSimpleEvent(this);

		return additionalProtection;
	}

	public AdditionalProtection removeAdditionalProtection(AdditionalProtection additionalProtection) {
		getAdditionalProtections().remove(additionalProtection);
		additionalProtection.setSimpleEvent(null);

		return additionalProtection;
	}

	public List<RiskInformation> getRiskInformations() {
		return this.riskInformations;
	}

	public void setRiskInformations(List<RiskInformation> riskInformations) {
		this.riskInformations = riskInformations;
	}

	public RiskInformation addRiskInformation(RiskInformation riskInformation) {
		getRiskInformations().add(riskInformation);
		riskInformation.setSimpleEvent(this);

		return riskInformation;
	}

	public RiskInformation removeRiskInformation(RiskInformation riskInformation) {
		getRiskInformations().remove(riskInformation);
		riskInformation.setSimpleEvent(null);

		return riskInformation;
	}

	public List<SecurityIncident> getSecurityIncidents() {
		return this.securityIncidents;
	}

	public void setSecurityIncidents(List<SecurityIncident> securityIncidents) {
		this.securityIncidents = securityIncidents;
	}

	public SecurityIncident addSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().add(securityIncident);
		securityIncident.setSimpleEvent(this);

		return securityIncident;
	}

	public SecurityIncident removeSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().remove(securityIncident);
		securityIncident.setSimpleEvent(null);

		return securityIncident;
	}

	public Applications getApplication() {
		return this.application;
	}

	public void setApplication(Applications application) {
		this.application = application;
	}

	public Assets getAsset() {
		return this.asset;
	}

	public void setAsset(Assets asset) {
		this.asset = asset;
	}

	public Devices getDevice() {
		return this.device;
	}

	public void setDevice(Devices device) {
		this.device = device;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Sources getSource() {
		return this.source;
	}

	public void setSource(Sources source) {
		this.source = source;
	}

	public Users getUser() {
		return this.user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public List<ThreatClue> getThreatClues() {
		return this.threatClues;
	}

	public void setThreatClues(List<ThreatClue> threatClues) {
		this.threatClues = threatClues;
	}

	public ThreatClue addThreatClue(ThreatClue threatClue) {
		getThreatClues().add(threatClue);
		threatClue.setSimpleEvent(this);

		return threatClue;
	}

	public ThreatClue removeThreatClue(ThreatClue threatClue) {
		getThreatClues().remove(threatClue);
		threatClue.setSimpleEvent(null);

		return threatClue;
	}

}