package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name="users")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id")
	private int userId;

	private String email;

	private byte[] enabled;

	private String name;

	private String password;

	@Column(name="role_id")
	private int roleId;

	private String surname;

	@Column(name="trust_value")
	private double trustValue;

	private String username;

	//bi-directional many-to-one association to AccessRequest
	@OneToMany(mappedBy="user")
	private List<AccessRequest> accessRequests;

	//bi-directional many-to-one association to AdditionalProtection
	@OneToMany(mappedBy="user")
	private List<AdditionalProtection> additionalProtections;

	//bi-directional many-to-one association to SecurityIncident
	@OneToMany(mappedBy="user")
	private List<SecurityIncident> securityIncidents;

	//bi-directional many-to-one association to SimpleEvent
	@OneToMany(mappedBy="user")
	private List<SimpleEvent> simpleEvents;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="user")
	private List<ThreatClue> threatClues;

	//bi-directional many-to-one association to UserBehaviour
	@OneToMany(mappedBy="user")
	private List<UserBehaviour> userBehaviours;

	public User() {
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getEnabled() {
		return this.enabled;
	}

	public void setEnabled(byte[] enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public double getTrustValue() {
		return this.trustValue;
	}

	public void setTrustValue(double trustValue) {
		this.trustValue = trustValue;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<AccessRequest> getAccessRequests() {
		return this.accessRequests;
	}

	public void setAccessRequests(List<AccessRequest> accessRequests) {
		this.accessRequests = accessRequests;
	}

	public AccessRequest addAccessRequest(AccessRequest accessRequest) {
		getAccessRequests().add(accessRequest);
		accessRequest.setUser(this);

		return accessRequest;
	}

	public AccessRequest removeAccessRequest(AccessRequest accessRequest) {
		getAccessRequests().remove(accessRequest);
		accessRequest.setUser(null);

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
		additionalProtection.setUser(this);

		return additionalProtection;
	}

	public AdditionalProtection removeAdditionalProtection(AdditionalProtection additionalProtection) {
		getAdditionalProtections().remove(additionalProtection);
		additionalProtection.setUser(null);

		return additionalProtection;
	}

	public List<SecurityIncident> getSecurityIncidents() {
		return this.securityIncidents;
	}

	public void setSecurityIncidents(List<SecurityIncident> securityIncidents) {
		this.securityIncidents = securityIncidents;
	}

	public SecurityIncident addSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().add(securityIncident);
		securityIncident.setUser(this);

		return securityIncident;
	}

	public SecurityIncident removeSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().remove(securityIncident);
		securityIncident.setUser(null);

		return securityIncident;
	}

	public List<SimpleEvent> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvent> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvent addSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setUser(this);

		return simpleEvent;
	}

	public SimpleEvent removeSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setUser(null);

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
		threatClue.setUser(this);

		return threatClue;
	}

	public ThreatClue removeThreatClue(ThreatClue threatClue) {
		getThreatClues().remove(threatClue);
		threatClue.setUser(null);

		return threatClue;
	}

	public List<UserBehaviour> getUserBehaviours() {
		return this.userBehaviours;
	}

	public void setUserBehaviours(List<UserBehaviour> userBehaviours) {
		this.userBehaviours = userBehaviours;
	}

	public UserBehaviour addUserBehaviour(UserBehaviour userBehaviour) {
		getUserBehaviours().add(userBehaviour);
		userBehaviour.setUser(this);

		return userBehaviour;
	}

	public UserBehaviour removeUserBehaviour(UserBehaviour userBehaviour) {
		getUserBehaviours().remove(userBehaviour);
		userBehaviour.setUser(null);

		return userBehaviour;
	}

}