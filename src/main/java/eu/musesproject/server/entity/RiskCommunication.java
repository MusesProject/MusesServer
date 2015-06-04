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

import java.util.List;


/**
 * The persistent class for the risk_communication database table.
 * 
 */
@Entity
@Table(name="risk_communication")
@NamedQueries ({@NamedQuery(name ="RiskCommunication.findAll", query="SELECT r FROM RiskCommunication r"),
@NamedQuery(name="RiskCommunication.findRiskCommunicationById", 
query="SELECT t FROM RiskCommunication t where t.riskCommunicationId = :risk_communication_id"),
})

public class RiskCommunication implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="risk_communication_id", unique=true, nullable=false)
	private int riskCommunicationId;

	@Column(nullable=false, length=255)
	private String description;

	//bi-directional many-to-one association to Decision
	@OneToMany(mappedBy="riskCommunication")
	private List<Decision> decisions;

	//bi-directional many-to-one association to RiskTreatment
	@OneToMany(mappedBy="riskCommunication")
	private List<RiskTreatment> riskTreatments;

	public RiskCommunication() {
	}

	public int getRiskCommunicationId() {
		return this.riskCommunicationId;
	}

	public void setRiskCommunicationId(int riskCommunicationId) {
		this.riskCommunicationId = riskCommunicationId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Decision> getDecisions() {
		return this.decisions;
	}

	public void setDecisions(List<Decision> decisions) {
		this.decisions = decisions;
	}

	public Decision addDecision(Decision decision) {
		getDecisions().add(decision);
		decision.setRiskCommunication(this);

		return decision;
	}

	public Decision removeDecision(Decision decision) {
		getDecisions().remove(decision);
		decision.setRiskCommunication(null);

		return decision;
	}

	public List<RiskTreatment> getRiskTreatments() {
		return this.riskTreatments;
	}

	public void setRiskTreatments(List<RiskTreatment> riskTreatments) {
		this.riskTreatments = riskTreatments;
	}

	public RiskTreatment addRiskTreatment(RiskTreatment riskTreatment) {
		getRiskTreatments().add(riskTreatment);
		riskTreatment.setRiskCommunication(this);

		return riskTreatment;
	}

	public RiskTreatment removeRiskTreatment(RiskTreatment riskTreatment) {
		getRiskTreatments().remove(riskTreatment);
		riskTreatment.setRiskCommunication(null);

		return riskTreatment;
	}

}