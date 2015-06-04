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

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the decision database table.
 * 
 */
@Entity
@Table(name="decision")
@NamedQueries ({
	@NamedQuery(name="Decision.findDecisionById", 
	    	query="SELECT d FROM Decision d where d.decisionId = :decision_id"),
	    	@NamedQuery(name="Decision.findAll", query="SELECT d FROM Decision d"),
})
public class Decision implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="decision_id", unique=true, nullable=false)
	private String decisionId;

	@Lob
	private String information;

	@Column(name="solving_risktreatment")
	private int solvingRisktreatment;

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
	@JoinColumn(name="risk_communication_id", nullable=true)
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

	public String getInformation() {
		return this.information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public int getSolvingRisktreatment() {
		return this.solvingRisktreatment;
	}

	public void setSolvingRisktreatment(int solvingRisktreatment) {
		this.solvingRisktreatment = solvingRisktreatment;
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