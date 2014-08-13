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
@NamedQuery(name="SimpleEvent.findAll", query="SELECT s FROM SimpleEvent s")
public class SimpleEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_id")
	private int eventId;

	private String data;

	@Temporal(TemporalType.DATE)
	private Date date;

	private int duration;

	private byte[] EP_can_access;

	private byte[] KRS_can_access;

	private byte[] RT2AE_can_access;

	private Time time;

	//bi-directional many-to-one association to AccessRequest
	@OneToMany(mappedBy="simpleEvent")
	private List<AccessRequest> accessRequests;

	//bi-directional many-to-one association to AdditionalProtection
	@OneToMany(mappedBy="simpleEvent")
	private List<AdditionalProtection> additionalProtections;

	//bi-directional many-to-one association to RiskInformation
	@OneToMany(mappedBy="simpleEvent")
	private List<RiskInformation> riskInformations;

	//bi-directional many-to-one association to SecurityIncident
	@OneToMany(mappedBy="simpleEvent")
	private List<SecurityIncident> securityIncidents;

	//bi-directional many-to-one association to Source
	@ManyToOne
	@JoinColumn(name="source_id")
	private Source source;

	//bi-directional many-to-one association to Application
	@ManyToOne
	@JoinColumn(name="app_id")
	private Application application;

	//bi-directional many-to-one association to Asset
	@ManyToOne
	@JoinColumn(name="asset_id")
	private Asset asset;

	//bi-directional many-to-one association to Device
	@ManyToOne
	@JoinColumn(name="device_id")
	private Device device;

	//bi-directional many-to-one association to EventType
	@ManyToOne
	@JoinColumn(name="event_type_id")
	private EventType eventType;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="simpleEvent")
	private List<ThreatClue> threatClues;

	public SimpleEvent() {
	}

	public int getEventId() {
		return this.eventId;
	}

	public void setEventId(int eventId) {
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

	public byte[] getEP_can_access() {
		return this.EP_can_access;
	}

	public void setEP_can_access(byte[] EP_can_access) {
		this.EP_can_access = EP_can_access;
	}

	public byte[] getKRS_can_access() {
		return this.KRS_can_access;
	}

	public void setKRS_can_access(byte[] KRS_can_access) {
		this.KRS_can_access = KRS_can_access;
	}

	public byte[] getRT2AE_can_access() {
		return this.RT2AE_can_access;
	}

	public void setRT2AE_can_access(byte[] RT2AE_can_access) {
		this.RT2AE_can_access = RT2AE_can_access;
	}

	public Time getTime() {
		return this.time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public List<AccessRequest> getAccessRequests() {
		return this.accessRequests;
	}

	public void setAccessRequests(List<AccessRequest> accessRequests) {
		this.accessRequests = accessRequests;
	}

	public AccessRequest addAccessRequest(AccessRequest accessRequest) {
		getAccessRequests().add(accessRequest);
		accessRequest.setSimpleEvent(this);

		return accessRequest;
	}

	public AccessRequest removeAccessRequest(AccessRequest accessRequest) {
		getAccessRequests().remove(accessRequest);
		accessRequest.setSimpleEvent(null);

		return accessRequest;
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

	public Source getSource() {
		return this.source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Application getApplication() {
		return this.application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Asset getAsset() {
		return this.asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
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