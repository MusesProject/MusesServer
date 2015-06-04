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
 * The persistent class for the clue database table.
 * 
 */
@Entity
@Table(name="clue")
@NamedQueries({
		@NamedQuery(name="Clue.findAll", 
					query="SELECT c FROM Clue c"),
		@NamedQuery(name="Clue.findByValue", 
					query="SELECT c FROM Clue c where c.value = :value"),
		@NamedQuery(name="Clue.deleteClueByValue", 
					query="delete FROM Clue c where c.value = :value")
})
public class Clue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="clue_id", unique=true, nullable=false)
	private String clueId;

	@Lob
	@Column(nullable=false)
	private String value;

	public Clue() {
	}

	public String getClueId() {
		return this.clueId;
	}

	public void setClueId(String clueId) {
		this.clueId = clueId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}