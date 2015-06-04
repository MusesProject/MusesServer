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
 * The persistent class for the app_type database table.
 * 
 */
@Entity
@Table(name="app_type")
@NamedQuery(name="AppType.findAll", query="SELECT a FROM AppType a")
public class AppType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="app_type_id", unique=true, nullable=false)
	private String appTypeId;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=30)
	private String type;



	public AppType() {
	}

	public String getAppTypeId() {
		return this.appTypeId;
	}

	public void setAppTypeId(String appTypeId) {
		this.appTypeId = appTypeId;
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




}