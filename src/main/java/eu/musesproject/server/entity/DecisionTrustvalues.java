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
 * The persistent class for the decision_trustvalues database table.
 * 
 */
@Entity
@Table(name="decision_trustvalues")
@NamedQueries ({
	@NamedQuery(name="DecisionTrustvalues.findAll",
			query="SELECT d FROM DecisionTrustvalues d"),
	@NamedQuery(name="DecisionTrustvalues.findByDecisionId",
			query="SELECT d FROM DecisionTrustvalues d where d.decisionId = :decision_id")
})
public class DecisionTrustvalues implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="decision_trustvalue_id")
	private int decisionTrustvalueId;

	@Column(name="decision_id")
	private int decisionId;

	private double devicetrustvalue;

	private double usertrustvalue;

	public DecisionTrustvalues() {
	}

	public int getDecisionTrustvalueId() {
		return this.decisionTrustvalueId;
	}

	public void setDecisionTrustvalueId(int decisionTrustvalueId) {
		this.decisionTrustvalueId = decisionTrustvalueId;
	}

	public int getDecisionId() {
		return this.decisionId;
	}

	public void setDecisionId(int decisionId) {
		this.decisionId = decisionId;
	}

	public double getDevicetrustvalue() {
		return this.devicetrustvalue;
	}

	public void setDevicetrustvalue(double devicetrustvalue) {
		this.devicetrustvalue = devicetrustvalue;
	}

	public double getUsertrustvalue() {
		return this.usertrustvalue;
	}

	public void setUsertrustvalue(double usertrustvalue) {
		this.usertrustvalue = usertrustvalue;
	}

}