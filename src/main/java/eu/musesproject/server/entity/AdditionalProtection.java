package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the additional_protection database table.
 * 
 */
@Entity
@Table(name="additional_protection")
@NamedQuery(name="AdditionalProtection.findAll", query="SELECT a FROM AdditionalProtection a")
public class AdditionalProtection implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="additional_protection_id")
	private String additionalProtectionId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	private String name;

	//bi-directional many-to-one association to AccessRequest
	@ManyToOne
	@JoinColumn(name="access_request_id")
	private AccessRequest accessRequest;

	//bi-directional many-to-one association to SimpleEvent
	@ManyToOne
	@JoinColumn(name="event_id")
	private SimpleEvent simpleEvent;

	//bi-directional many-to-one association to Device
	@ManyToOne
	@JoinColumn(name="device_id")
	private Device device;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public AdditionalProtection() {
	}

	public String getAdditionalProtectionId() {
		return this.additionalProtectionId;
	}

	public void setAdditionalProtectionId(String additionalProtectionId) {
		this.additionalProtectionId = additionalProtectionId;
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

	public SimpleEvent getSimpleEvent() {
		return this.simpleEvent;
	}

	public void setSimpleEvent(SimpleEvent simpleEvent) {
		this.simpleEvent = simpleEvent;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}