package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the devices database table.
 * 
 */
@Entity
@Table(name="devices")
@NamedQueries({
	@NamedQuery(name="Devices.findAll", 
				query="SELECT d FROM Devices d"),
	@NamedQuery(name="Devices.findById", 
				query="SELECT d FROM Devices d where d.deviceId = :device_id"),
	@NamedQuery(name="Devices.findByIMEI", 
				query="SELECT d FROM Devices d where d.imei = :imei")
})
public class Devices implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="device_id", unique=true, nullable=false)
	private String deviceId;

	@Lob
	private byte[] certificate;

	@Column(length=100)
	private String description;

	@Column(length=30)
	private String imei;

	@Column(length=30)
	private String mac;

	@Column(nullable=false, length=30)
	private String name;

	@Column(length=30)
	private String OS_name;

	@Column(length=20)
	private String OS_version;

	@Column(name="owner_type", length=1)
	private String ownerType;

	@Column(name="security_level")
	private short securityLevel;

	@Column(name="trust_value")
	private double trustValue;

	//bi-directional many-to-one association to AdditionalProtection
	@OneToMany(mappedBy="device")
	private List<AdditionalProtection> additionalProtections;

	//bi-directional many-to-one association to DeviceType
	@ManyToOne
	@JoinColumn(name="type", nullable=false)
	private DeviceType deviceType;

	//bi-directional many-to-one association to SecurityIncident
	@OneToMany(mappedBy="device")
	private List<SecurityIncident> securityIncidents;

	//bi-directional many-to-one association to SimpleEvents
	@OneToMany(mappedBy="device")
	private List<SimpleEvents> simpleEvents;

	//bi-directional many-to-one association to UserBehaviour
	@OneToMany(mappedBy="device")
	private List<UserBehaviour> userBehaviours;

	public Devices() {
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public byte[] getCertificate() {
		return this.certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImei() {
		return this.imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getMac() {
		return this.mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOS_name() {
		return this.OS_name;
	}

	public void setOS_name(String OS_name) {
		this.OS_name = OS_name;
	}

	public String getOS_version() {
		return this.OS_version;
	}

	public void setOS_version(String OS_version) {
		this.OS_version = OS_version;
	}

	public String getOwnerType() {
		return this.ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public short getSecurityLevel() {
		return this.securityLevel;
	}

	public void setSecurityLevel(short securityLevel) {
		this.securityLevel = securityLevel;
	}

	public double getTrustValue() {
		return this.trustValue;
	}

	public void setTrustValue(double trustValue) {
		this.trustValue = trustValue;
	}

	public List<AdditionalProtection> getAdditionalProtections() {
		return this.additionalProtections;
	}

	public void setAdditionalProtections(List<AdditionalProtection> additionalProtections) {
		this.additionalProtections = additionalProtections;
	}

	public AdditionalProtection addAdditionalProtection(AdditionalProtection additionalProtection) {
		getAdditionalProtections().add(additionalProtection);
		additionalProtection.setDevice(this);

		return additionalProtection;
	}

	public AdditionalProtection removeAdditionalProtection(AdditionalProtection additionalProtection) {
		getAdditionalProtections().remove(additionalProtection);
		additionalProtection.setDevice(null);

		return additionalProtection;
	}

	public DeviceType getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public List<SecurityIncident> getSecurityIncidents() {
		return this.securityIncidents;
	}

	public void setSecurityIncidents(List<SecurityIncident> securityIncidents) {
		this.securityIncidents = securityIncidents;
	}

	public SecurityIncident addSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().add(securityIncident);
		securityIncident.setDevice(this);

		return securityIncident;
	}

	public SecurityIncident removeSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().remove(securityIncident);
		securityIncident.setDevice(null);

		return securityIncident;
	}

	public List<SimpleEvents> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvents> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvents addSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setDevice(this);

		return simpleEvent;
	}

	public SimpleEvents removeSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setDevice(null);

		return simpleEvent;
	}

	public List<UserBehaviour> getUserBehaviours() {
		return this.userBehaviours;
	}

	public void setUserBehaviours(List<UserBehaviour> userBehaviours) {
		this.userBehaviours = userBehaviours;
	}

	public UserBehaviour addUserBehaviour(UserBehaviour userBehaviour) {
		getUserBehaviours().add(userBehaviour);
		userBehaviour.setDevice(this);

		return userBehaviour;
	}

	public UserBehaviour removeUserBehaviour(UserBehaviour userBehaviour) {
		getUserBehaviours().remove(userBehaviour);
		userBehaviour.setDevice(null);

		return userBehaviour;
	}

}