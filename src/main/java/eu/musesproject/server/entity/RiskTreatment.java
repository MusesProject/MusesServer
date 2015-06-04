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


/**
 * The persistent class for the risk_treatment database table.
 * 
 */
@Entity
@Table(name="risk_treatment")
@NamedQuery(name="RiskTreatment.findAll", query="SELECT r FROM RiskTreatment r")
public class RiskTreatment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="risk_treatment_id", unique=true, nullable=false)
	private int riskTreatmentId;

	@Column(nullable=false, length=50)
	private String description;

	//bi-directional many-to-one association to RiskCommunication
	@ManyToOne
	@JoinColumn(name="risk_communication_id", nullable=false)
	private RiskCommunication riskCommunication;

	public RiskTreatment() {
	}

	public int getRiskTreatmentId() {
		return this.riskTreatmentId;
	}

	public void setRiskTreatmentId(int riskTreatmentId) {
		this.riskTreatmentId = riskTreatmentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RiskCommunication getRiskCommunication() {
		return this.riskCommunication;
	}

	public void setRiskCommunication(RiskCommunication riskCommunication) {
		this.riskCommunication = riskCommunication;
	}

}