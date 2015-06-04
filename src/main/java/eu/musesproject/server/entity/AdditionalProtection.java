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
import java.util.Date;


/**
 * The persistent class for the additional_protection database table.
 * 
 */
@Entity
@Table(name="additional_protection")
@NamedQuery(name="AdditionalProtection.findAll", query="SELECT a FROM AdditionalProtection a")
public class AdditionalProtection implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="additional_protection_id", unique=true, nullable=false)
	private int additionalProtectionId;

	@Column(name="access_request_id")
	private int accessRequestId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modification;

	@Column(nullable=false, length=50)
	private String name;

	//bi-directional many-to-one association to Devices
	@ManyToOne
	@JoinColumn(name="device_id")
	private Devices device;

	//bi-directional many-to-one association to SimpleEvents
	@ManyToOne
	@JoinColumn(name="event_id")
	private SimpleEvents simpleEvent;

	//bi-directional many-to-one association to Users
	@ManyToOne
	@JoinColumn(name="user_id")
	private Users user;

	public AdditionalProtection() {
	}

	public int getAdditionalProtectionId() {
		return this.additionalProtectionId;
	}

	public void setAdditionalProtectionId(int additionalProtectionId) {
		this.additionalProtectionId = additionalProtectionId;
	}

	public int getAccessRequestId() {
		return this.accessRequestId;
	}

	public void setAccessRequestId(int accessRequestId) {
		this.accessRequestId = accessRequestId;
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

	public Devices getDevice() {
		return this.device;
	}

	public void setDevice(Devices device) {
		this.device = device;
	}

	public SimpleEvents getSimpleEvent() {
		return this.simpleEvent;
	}

	public void setSimpleEvent(SimpleEvents simpleEvent) {
		this.simpleEvent = simpleEvent;
	}

	public Users getUser() {
		return this.user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}