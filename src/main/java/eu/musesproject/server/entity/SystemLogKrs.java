package eu.musesproject.server.entity;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
@NamedQuery(name="SystemLogKrs.findAll", query="SELECT s FROM SystemLogKrs s")
public class SystemLogKrs implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="log_id", unique=true, nullable=false)
	private String logId;

	@Column(name="current_event_id", nullable=false)
	private BigInteger currentEventId;

	@Column(name="decision_id", nullable=false)
	private BigInteger decisionId;

	@Column(name="device_security_state", nullable=false)
	private BigInteger deviceSecurityState;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="finish_time", nullable=false)
	private Date finishTime;

	@Column(name="previous_event_id", nullable=false)
	private BigInteger previousEventId;

	@Column(name="risk_treatment", nullable=true)
	private String riskTreatment;

	@Column(name="security_incident_id", nullable=false)
	private BigInteger securityIncidentId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_time", nullable=false)
	private Date startTime;

	@Column(name="user_behaviour_id", nullable=false)
	private BigInteger userBehaviourId;

	public SystemLogKrs() {
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

	public String getRiskTreatment() {
		return this.riskTreatment;
	}

	public void setRiskTreatment(String riskTreatment) {
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