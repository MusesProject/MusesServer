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
 * The persistent class for the sources database table.
 * 
 */
@Entity
@Table(name="sources")

@NamedQueries({
	@NamedQuery(name="Sources.findAll", query="SELECT s FROM Sources s"),
	@NamedQuery(name="Sources.findByName", 
				query="SELECT s FROM Sources s where s.name = :name")	
})
public class Sources implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="source_id", unique=true, nullable=false)
	private String sourceId;

	@Column(nullable=false, length=50)
	private String name;

	//bi-directional many-to-one association to SecurityRules
	@OneToMany(mappedBy="source")
	private List<SecurityRules> securityRules;

	//bi-directional many-to-one association to SimpleEvents
	@OneToMany(mappedBy="source")
	private List<SimpleEvents> simpleEvents;

	public Sources() {
	}

	public String getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SecurityRules> getSecurityRules() {
		return this.securityRules;
	}

	public void setSecurityRules(List<SecurityRules> securityRules) {
		this.securityRules = securityRules;
	}

	public SecurityRules addSecurityRule(SecurityRules securityRule) {
		getSecurityRules().add(securityRule);
		securityRule.setSource(this);

		return securityRule;
	}

	public SecurityRules removeSecurityRule(SecurityRules securityRule) {
		getSecurityRules().remove(securityRule);
		securityRule.setSource(null);

		return securityRule;
	}

	public List<SimpleEvents> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvents> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvents addSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setSource(this);

		return simpleEvent;
	}

	public SimpleEvents removeSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setSource(null);

		return simpleEvent;
	}

}