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
 * The persistent class for the corporate_policies database table.
 * 
 */
@Entity
@Table(name="corporate_policies")
@NamedQuery(name="CorporatePolicies.findAll", query="SELECT c FROM CorporatePolicies c")
public class CorporatePolicies implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="corporate_policy_id", unique=true, nullable=false)
	private int corporatePolicyId;

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

	public CorporatePolicies() {
	}

	public int getCorporatePolicyId() {
		return this.corporatePolicyId;
	}

	public void setCorporatePolicyId(int corporatePolicyId) {
		this.corporatePolicyId = corporatePolicyId;
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