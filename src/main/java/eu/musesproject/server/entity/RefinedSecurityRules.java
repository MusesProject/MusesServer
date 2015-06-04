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

import java.util.Arrays;
import java.util.Date;


/**
 * The persistent class for the refined_security_rules database table.
 * 
 */
@Entity
@Table(name="refined_security_rules")
@NamedQueries ({
	@NamedQuery(name="RefinedSecurityRules.findAll", 
				query="SELECT r FROM RefinedSecurityRules r"),
	@NamedQuery(name="RefinedSecurityRules.findByStatus", 
				query="SELECT r FROM RefinedSecurityRules r where r.status = :status")
})
public class RefinedSecurityRules implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="refined_security_rules_id", unique=true, nullable=false)
	private String refinedSecurityRulesId;

	@Lob
	private byte[] file;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date modification;

	@Column(nullable=false, length=2000)
	private String name;

	@Column(nullable=false, length=1)
	private String status;

	//bi-directional many-to-one association to SecurityRules
	@ManyToOne
	@JoinColumn(name="original_security_rule_id", nullable=false)
	private SecurityRules securityRule;

	public RefinedSecurityRules() {
	}

	public String getRefinedSecurityRulesId() {
		return this.refinedSecurityRulesId;
	}

	public void setRefinedSecurityRulesId(String refinedSecurityRulesId) {
		this.refinedSecurityRulesId = refinedSecurityRulesId;
	}

	public byte[] getFile() {
		return this.file;
	}

	public void setFile(byte[] fileArray) {
		this.file = Arrays.copyOf(fileArray,fileArray.length);
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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SecurityRules getSecurityRule() {
		return this.securityRule;
	}

	public void setSecurityRule(SecurityRules securityRule) {
		this.securityRule = securityRule;
	}

}