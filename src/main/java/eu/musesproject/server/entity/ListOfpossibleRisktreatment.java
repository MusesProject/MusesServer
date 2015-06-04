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
 * The persistent class for the list_ofpossible_risktreatments database table.
 * 
 */
@Entity
@Table(name="list_ofpossible_risktreatments")
@NamedQueries({
	@NamedQuery(name="ListOfpossibleRisktreatment.findbyId", 
			query="SELECT l FROM ListOfpossibleRisktreatment l where l.listofpossiblerisktreatmentId = :risktreatment_id"),
@NamedQuery(name="ListOfpossibleRisktreatment.findAll", query="SELECT l FROM ListOfpossibleRisktreatment l")
})
public class ListOfpossibleRisktreatment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="listofpossiblerisktreatment_id")
	private int listofpossiblerisktreatmentId;

	private String description;

	private String french;

	private String german;

	private String spanish;

	public ListOfpossibleRisktreatment() {
	}

	public int getListofpossiblerisktreatmentId() {
		return this.listofpossiblerisktreatmentId;
	}

	public void setListofpossiblerisktreatmentId(int listofpossiblerisktreatmentId) {
		this.listofpossiblerisktreatmentId = listofpossiblerisktreatmentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFrench() {
		return this.french;
	}

	public void setFrench(String french) {
		this.french = french;
	}

	public String getGerman() {
		return this.german;
	}

	public void setGerman(String german) {
		this.german = german;
	}

	public String getSpanish() {
		return this.spanish;
	}

	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}

}