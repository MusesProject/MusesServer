package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the security_incident database table.
 * 
 */
@Entity
@Table(name="security_incident")
@NamedQuery(name="SecurityIncident.findAll", query="SELECT s FROM SecurityIncident s")
public class SecurityIncident implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="security_incident_id")
	private String securityIncidentId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	private String name;

	//bi-directional many-to-one association to Decision
	@ManyToOne
	@JoinColumn(name="decision_id")
	private Decision decision;

	//bi-directional many-to-one association to Device
	@ManyToOne
	@JoinColumn(name="device_id")
	private Device device;

	//bi-directional many-to-one association to SimpleEvent
	@ManyToOne
	@JoinColumn(name="event_id")
	private SimpleEvent simpleEvent;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public SecurityIncident() {
	}

	public String getSecurityIncidentId() {
		return this.securityIncidentId;
	}

	public void setSecurityIncidentId(String securityIncidentId) {
		this.securityIncidentId = securityIncidentId;
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

	public Decision getDecision() {
		return this.decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
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