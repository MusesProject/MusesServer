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
 * The persistent class for the threat_type database table.
 * 
 */
@Entity
@Table(name="threat_type")


@NamedQueries({
	@NamedQuery(name="ThreatType.findAll", query="SELECT t FROM ThreatType t"),
	@NamedQuery(name="ThreatType.findByType", 
				query="SELECT t FROM ThreatType t where t.type = :type")	
})
public class ThreatType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="threat_type_id", unique=true, nullable=false)
	private int threatTypeId;

	@Column(nullable=false, length=100)
	private String description;

	@Column(nullable=false, length=50)
	private String type;

	//bi-directional many-to-one association to ThreatClue
	@OneToMany(mappedBy="threatType")
	private List<ThreatClue> threatClues;

	public ThreatType() {
	}

	public int getThreatTypeId() {
		return this.threatTypeId;
	}

	public void setThreatTypeId(int threatTypeId) {
		this.threatTypeId = threatTypeId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ThreatClue> getThreatClues() {
		return this.threatClues;
	}

	public void setThreatClues(List<ThreatClue> threatClues) {
		this.threatClues = threatClues;
	}

	public ThreatClue addThreatClue(ThreatClue threatClue) {
		getThreatClues().add(threatClue);
		threatClue.setThreatType(this);

		return threatClue;
	}

	public ThreatClue removeThreatClue(ThreatClue threatClue) {
		getThreatClues().remove(threatClue);
		threatClue.setThreatType(null);

		return threatClue;
	}

}