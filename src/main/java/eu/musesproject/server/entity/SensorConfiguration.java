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
 * The persistent class for the muses_config database table.
 * 
 */
@Entity
@Table(name="sensor_configuration")
@NamedQueries({
	@NamedQuery(name="SensorConfiguration.findAll", query="SELECT c FROM SensorConfiguration c"),
})

public class SensorConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	private int id;

	@Column(name="sensor_type", nullable=false, length=45)
	private String sensorType;
	
	@Column(name="keyproperty", nullable=false, length=45)
	private String keyProperty;
	
	@Column(name="valueproperty", nullable=false, length=45)
	private String valueProperty;

	public SensorConfiguration() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public String getKeyProperty() {
		return keyProperty;
	}

	public void setKeyProperty(String keyProperty) {
		this.keyProperty = keyProperty;
	}

	public String getValueProperty() {
		return valueProperty;
	}

	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}


}