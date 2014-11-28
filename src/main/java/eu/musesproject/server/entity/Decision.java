package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the decision database table.
 * 
 */
@Entity
@Table(name="decision")
@NamedQuery(name="Decision.findAll", query="SELECT d FROM Decision d")
public class Decision implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="decision_id", unique=true, nullable=false)
	private String decisionId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date time;

	@Column(nullable=false, length=1)
	private String value;

	//bi-directional many-to-one association to AccessRequest
	@ManyToOne
	@JoinColumn(name="access_request_id", nullable=false)
	private AccessRequest accessRequest;

	//bi-directional many-to-one association to RiskCommunication
	@ManyToOne
	@JoinColumn(name="risk_communication_id", nullable=false)
	private RiskCommunication riskCommunication;

	//bi-directional many-to-one association to SecurityIncident
	@OneToMany(mappedBy="decision")
	private List<SecurityIncident> securityIncidents;

	//bi-directional many-to-one association to UserBehaviour
	@OneToMany(mappedBy="decision")
	private List<UserBehaviour> userBehaviours;

	public Decision() {
	}

	public String getDecisionId() {
		return this.decisionId;
	}

	public void setDecisionId(String decisionId) {
		this.decisionId = decisionId;
	}

	public Date getTime() {
		return this.time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AccessRequest getAccessRequest() {
		return this.accessRequest;
	}

	public void setAccessRequest(AccessRequest accessRequest) {
		this.accessRequest = accessRequest;
	}

	public RiskCommunication getRiskCommunication() {
		return this.riskCommunication;
	}

	public void setRiskCommunication(RiskCommunication riskCommunication) {
		this.riskCommunication = riskCommunication;
	}

	public List<SecurityIncident> getSecurityIncidents() {
		return this.securityIncidents;
	}

	public void setSecurityIncidents(List<SecurityIncident> securityIncidents) {
		this.securityIncidents = securityIncidents;
	}

	public SecurityIncident addSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().add(securityIncident);
		securityIncident.setDecision(this);

		return securityIncident;
	}

	public SecurityIncident removeSecurityIncident(SecurityIncident securityIncident) {
		getSecurityIncidents().remove(securityIncident);
		securityIncident.setDecision(null);

		return securityIncident;
	}

	public List<UserBehaviour> getUserBehaviours() {
		return this.userBehaviours;
	}

	public void setUserBehaviours(List<UserBehaviour> userBehaviours) {
		this.userBehaviours = userBehaviours;
	}

	public UserBehaviour addUserBehaviour(UserBehaviour userBehaviour) {
		getUserBehaviours().add(userBehaviour);
		userBehaviour.setDecision(this);

		return userBehaviour;
	}

	public UserBehaviour removeUserBehaviour(UserBehaviour userBehaviour) {
		getUserBehaviours().remove(userBehaviour);
		userBehaviour.setDecision(null);

		return userBehaviour;
	}

}