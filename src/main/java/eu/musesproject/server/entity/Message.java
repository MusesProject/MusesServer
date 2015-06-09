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
import java.util.List;


/**
 * The persistent class for the security_rules database table.
 * 
 */
@Entity
@Table(name="message")
@NamedQueries ({
	@NamedQuery(name="Message.findAll", 
				query="SELECT m FROM Message m"),
	@NamedQuery(name="Message.findByKey", 
				query="SELECT m FROM Message m where m.key = :key"),
	@NamedQuery(name="Message.findByKeyAndLanguage", 
				query="SELECT m FROM Message m where m.key = :key and m.language = :language")
})
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="idmessage", unique=true, nullable=false)
	private String idMessage;

	@Column(length=2000)
	private String key;
	
	@Column(length=45)
	private String language;

	@Column(length=5000)
	private String translation;

	public Message() {
	}

	public String getIdMessage() {
		return idMessage;
	}

	public void setIdMessage(String idMessage) {
		this.idMessage = idMessage;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	
}