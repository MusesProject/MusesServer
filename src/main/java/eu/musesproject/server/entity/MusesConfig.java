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
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the muses_config database table.
 * 
 */
@Entity
@Table(name="muses_config")
@NamedQuery(name="MusesConfig.findAll", query="SELECT m FROM MusesConfig m")

public class MusesConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="config_id", unique=true, nullable=false)
	private int configId;

	@Column(name="access_attempts_before_blocking", nullable=false)
	private int accessAttemptsBeforeBlocking;

	@Column(name="config_name", nullable=false, length=30)
	private String configName;
	
	@Column(name="silent_mode", nullable=false, length=10)
	private boolean silentMode;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public MusesConfig() {
	}

	public int getConfigId() {
		return this.configId;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
	}

	public int getAccessAttemptsBeforeBlocking() {
		return this.accessAttemptsBeforeBlocking;
	}

	public void setAccessAttemptsBeforeBlocking(int accessAttemptsBeforeBlocking) {
		this.accessAttemptsBeforeBlocking = accessAttemptsBeforeBlocking;
	}

	public String getConfigName() {
		return this.configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
	public boolean getSilentMode() {
		return this.silentMode;
	}

	public void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date detection) {
		this.date = detection;
	}

}