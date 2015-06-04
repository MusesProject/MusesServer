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
 * The persistent class for the risk_policy database table.
 * 
 */
@Entity
@Table(name="risk_policy")
@NamedQuery(name="RiskPolicy.findAll", query="SELECT r FROM RiskPolicy r")
public class RiskPolicy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="risk_policy_id", unique=true, nullable=false)
	private String riskPolicyId;

	@Lob
	@Column(nullable=false)
	private String description;

	@Column(nullable=false)
	private double riskvalue;

	public RiskPolicy() {
	}

	public String getRiskPolicyId() {
		return this.riskPolicyId;
	}

	public void setRiskPolicyId(String riskPolicyId) {
		this.riskPolicyId = riskPolicyId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRiskvalue() {
		return this.riskvalue;
	}

	public void setRiskvalue(double riskvalue) {
		this.riskvalue = riskvalue;
	}

}