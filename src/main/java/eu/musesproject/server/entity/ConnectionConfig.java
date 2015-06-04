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
 * The persistent class for the connection_config database table.
 * 
 */
@Entity
@Table(name="connection_config")
@NamedQuery(name="ConnectionConfig.findAll", query="SELECT c FROM ConnectionConfig c")

public class ConnectionConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="config_id", unique=true, nullable=false)
	private int configId;

	@Column(name="timeout", nullable=false)
	private int timeout;
	
	@Column(name="poll_timeout", nullable=false)
	private int pollTimeout;

	@Column(name="sleep_poll_timeout", nullable=false)
	private int sleepPollTimeout;
	
	@Column(name="polling_enabled", nullable=false, length=10)
	private int pollingEnabled;
	
	@Column(name="login_attempts", nullable=false)
	private int loginAttempts;

	public ConnectionConfig() {
	}

	public int getConfigId() {
		return this.configId;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getPollTimeout() {
		return pollTimeout;
	}

	public void setPollTimeout(int pollTimeout) {
		this.pollTimeout = pollTimeout;
	}

	public int getSleepPollTimeout() {
		return sleepPollTimeout;
	}

	public void setSleepPollTimeout(int sleepPollTimeout) {
		this.sleepPollTimeout = sleepPollTimeout;
	}

	public int getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(int loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public int getPollingEnabled() {
		return pollingEnabled;
	}

	public void setPollingEnabled(int pollingEnabled) {
		this.pollingEnabled = pollingEnabled;
	}

	

}