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
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the corporate_policies database table.
 * 
 */
@Entity
@Table(name="default_policies")
@NamedQueries({
	@NamedQuery(name="DefaultPolicies.findAll", 
				query="SELECT d FROM DefaultPolicies d")
})
public class DefaultPolicies implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="default_policy_id", unique=true, nullable=false)
	private int defaultPolicyId;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date date;

	@Column(nullable=false, length=2000)
	private String description;

	@Lob
	@Column(nullable=false)
	private byte[] file;

	@Column(nullable=false, length=2000)
	private String name;

	public DefaultPolicies() {
	}

	public int getCorporatePolicyId() {
		return this.defaultPolicyId;
	}

	public void setCorporatePolicyId(int defaultPolicyId) {
		this.defaultPolicyId = defaultPolicyId;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescriptionEn() {
		return this.description;
	}

	public void setDescriptionEn(String description) {
		this.description = description;
	}

	public byte[] getFile() {
		return this.file;
	}

	public void setFile(byte[] fileArray) {
		this.file = Arrays.copyOf(fileArray,fileArray.length);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}