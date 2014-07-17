package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the user_behaviour database table.
 * 
 */
@Entity
@Table(name="user_behaviour")
@NamedQuery(name="UserBehaviour.findAll", query="SELECT u FROM UserBehaviour u")
public class UserBehaviour implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_behaviour_id")
	private String userBehaviourId;

	private String action;

	@Column(name="additional_info")
	private String additionalInfo;

	@Temporal(TemporalType.TIMESTAMP)
	private Date time;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	//bi-directional many-to-one association to Device
	@ManyToOne
	@JoinColumn(name="device_id")
	private Device device;

	//bi-directional many-to-one association to Decision
	@ManyToOne
	@JoinColumn(name="decision_id")
	private Decision decision;

	public UserBehaviour() {
	}

	public String getUserBehaviourId() {
		return this.userBehaviourId;
	}

	public void setUserBehaviourId(String userBehaviourId) {
		this.userBehaviourId = userBehaviourId;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Date getTime() {
		return this.time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Device getDevice() {
		return this.device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Decision getDecision() {
		return this.decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}

}