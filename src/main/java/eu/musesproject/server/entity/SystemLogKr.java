package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;


/**
 * The persistent class for the system_log_krs database table.
 * 
 */
@Entity
@Table(name="system_log_krs")
@NamedQuery(name="SystemLogKr.findAll", query="SELECT s FROM SystemLogKr s")
public class SystemLogKr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="log_id")
	private String logId;

	@Column(name="current_event_id")
	private BigInteger currentEventId;

	@Column(name="decision_id")
	private BigInteger decisionId;

	@Column(name="device_security_state")
	private BigInteger deviceSecurityState;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="finish_time")
	private Date finishTime;

	@Column(name="previous_event_id")
	private BigInteger previousEventId;

	@Column(name="risk_treatment")
	private int riskTreatment;

	@Column(name="security_incident_id")
	private BigInteger securityIncidentId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_time")
	private Date startTime;

	@Column(name="user_behaviour_id")
	private BigInteger userBehaviourId;

	public SystemLogKr() {
	}

	public String getLogId() {
		return this.logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public BigInteger getCurrentEventId() {
		return this.currentEventId;
	}

	public void setCurrentEventId(BigInteger currentEventId) {
		this.currentEventId = currentEventId;
	}

	public BigInteger getDecisionId() {
		return this.decisionId;
	}

	public void setDecisionId(BigInteger decisionId) {
		this.decisionId = decisionId;
	}

	public BigInteger getDeviceSecurityState() {
		return this.deviceSecurityState;
	}

	public void setDeviceSecurityState(BigInteger deviceSecurityState) {
		this.deviceSecurityState = deviceSecurityState;
	}

	public Date getFinishTime() {
		return this.finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public BigInteger getPreviousEventId() {
		return this.previousEventId;
	}

	public void setPreviousEventId(BigInteger previousEventId) {
		this.previousEventId = previousEventId;
	}

	public int getRiskTreatment() {
		return this.riskTreatment;
	}

	public void setRiskTreatment(int riskTreatment) {
		this.riskTreatment = riskTreatment;
	}

	public BigInteger getSecurityIncidentId() {
		return this.securityIncidentId;
	}

	public void setSecurityIncidentId(BigInteger securityIncidentId) {
		this.securityIncidentId = securityIncidentId;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public BigInteger getUserBehaviourId() {
		return this.userBehaviourId;
	}

	public void setUserBehaviourId(BigInteger userBehaviourId) {
		this.userBehaviourId = userBehaviourId;
	}

}