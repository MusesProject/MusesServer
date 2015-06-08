/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.entity;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 UGR
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
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;

/**
 * The persistent class for the pattern_krs database table.
 * 
 */
@Entity
@Table(name="patterns_krs")
@NamedQueries ({
	@NamedQuery(name="PatternsKrs.findAll",
			query="SELECT p FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctDecisionCause",
			query="SELECT DISTINCT p.decisionCause FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctEventTypes",
			query="SELECT DISTINCT p.eventType FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctEventLevel",
			query="SELECT DISTINCT p.eventLevel FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctUsername",
			query="SELECT DISTINCT p.username FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctUserRole",
			query="SELECT DISTINCT p.userRole FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctDeviceType",
			query="SELECT DISTINCT p.deviceType FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctDeviceOS",
			query="SELECT DISTINCT p.deviceOS FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctDeviceOwnedBy",
			query="SELECT DISTINCT p.deviceOwnedBy FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctAppName",
			query="SELECT DISTINCT p.appName FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctAppVendor",
			query="SELECT DISTINCT p.appVendor FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctAssetName",
			query="SELECT DISTINCT p.assetName FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctAssetConfidentialLevel",
			query="SELECT DISTINCT p.assetConfidentialLevel FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctAssetLocation",
			query="SELECT DISTINCT p.assetLocation FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctLabels",
			query="SELECT DISTINCT p.label FROM PatternsKrs p"),
	@NamedQuery(name="PatternsKrs.findDistinctWifiEncryptions",
			query="SELECT DISTINCT p.wifiEncryption FROM PatternsKrs p")
})
public class PatternsKrs implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="log_entry_id", unique=true)
	private BigInteger logEntryId;

	@Column(name="label")
	private String label;

	@Column(name="decision_cause")
	private String decisionCause;
	
	@Column(name="silent_mode")
	private int silentMode;

	@Column(name="event_type")
	private String eventType;
	
	@Column(name="event_level")
	private String eventLevel;
	
	@Column(name="username")
	private String username;
	
	@Column(name="password_length")
	private int passwordLength;
	
	@Column(name="letters_in_password")
	private int lettersInPassword;
	
	@Column(name="numbers_in_password")
	private int numbersInPassword;
	
	@Column(name="passwd_has_capital_letters")
	private int passwdHasCapitalLetters;
	
	@Column(name="user_trust_value")
	private double userTrustValue;
	
	@Column(name="activated_account")
	private int activatedAccount;
	
	@Column(name="user_role")
	private String userRole;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="event_detection")
	private Date eventTime;
	
	@Column(name="device_type")
	private String deviceType;
	
	@Column(name="device_OS")
	private String deviceOS;
	
	@Column(name="device_has_antivirus")
	private int deviceHasAntivirus;
	
	@Column(name="device_has_certificate")
	private int deviceHasCertificate;
	
	@Column(name="device_trust_value")
	private double deviceTrustValue;
	
	@Column(name="device_owned_by")
	private String deviceOwnedBy;
	
	@Column(name="device_has_password")
	private int deviceHasPassword;
	
	@Column(name="device_screen_timeout")
	private BigInteger deviceScreenTimeout;
	
	@Column(name="device_has_accessibility")
	private int deviceHasAccessibility;
	
	@Column(name="device_is_rooted")
	private int deviceIsRooted;
	
	@Column(name="app_name")
	private String appName;
	
	@Column(name="app_vendor")
	private String appVendor;
	
	@Column(name="app_is_MUSES_aware")
	private int appMUSESAware;
	
	@Column(name="asset_name")
	private String assetName;
	
	@Column(name="asset_value")
	private double assetValue;
	
	@Column(name="asset_confidential_level")
	private String assetConfidentialLevel;
	
	@Column(name="asset_location")
	private String assetLocation;
	
	@Column(name="mail_recipient_allowed")
	private int mailRecipientAllowed;
	
	@Column(name="mail_contains_cc_allowed")
	private int mailContainsCC;
	
	@Column(name="mail_contains_bcc_allowed")
	private int mailContainsBCC;
	
	@Column(name="mail_has_attachment")
	private int mailHasAttachment;
	
	@Column(name="wifiencryption")
	private String wifiEncryption;
	
	@Column(name="wifienabled")
	private int wifiEnabled;
	
	@Column(name="wificonnected")
	private int wifiConnected;

	public PatternsKrs() {
	}

	public BigInteger getLogEntryId() {
		return logEntryId;
	}

	public void setLogEntryId(BigInteger logEntryId) {
		this.logEntryId = logEntryId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDecisionCause() {
		return decisionCause;
	}

	public void setDecisionCause(String decisionCause) {
		this.decisionCause = decisionCause;
	}

	public int getSilentMode() {
		return silentMode;
	}

	public void setSilentMode(int silentMode) {
		this.silentMode = silentMode;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventLevel() {
		return eventLevel;
	}

	public void setEventLevel(String eventLevel) {
		this.eventLevel = eventLevel;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(int passwordLength) {
		this.passwordLength = passwordLength;
	}

	public int getLettersInPassword() {
		return lettersInPassword;
	}

	public void setLettersInPassword(int lettersInPassword) {
		this.lettersInPassword = lettersInPassword;
	}

	public int getNumbersInPassword() {
		return numbersInPassword;
	}

	public void setNumbersInPassword(int numbersInPassword) {
		this.numbersInPassword = numbersInPassword;
	}

	public int getPasswdHasCapitalLetters() {
		return passwdHasCapitalLetters;
	}

	public void setPasswdHasCapitalLetters(int passwdHasCapitalLetters) {
		this.passwdHasCapitalLetters = passwdHasCapitalLetters;
	}

	public double getUserTrustValue() {
		return userTrustValue;
	}

	public void setUserTrustValue(double userTrustValue) {
		this.userTrustValue = userTrustValue;
	}

	public int getActivatedAccount() {
		return activatedAccount;
	}

	public void setActivatedAccount(int activatedAccount) {
		this.activatedAccount = activatedAccount;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceOS() {
		return deviceOS;
	}

	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}

	public int getDeviceHasAntivirus() {
		return deviceHasAntivirus;
	}

	public void setDeviceHasAntivirus(int deviceHasAntivirus) {
		this.deviceHasAntivirus = deviceHasAntivirus;
	}

	public int getDeviceHasCertificate() {
		return deviceHasCertificate;
	}

	public void setDeviceHasCertificate(int deviceHasCertificate) {
		this.deviceHasCertificate = deviceHasCertificate;
	}

	public double getDeviceTrustValue() {
		return deviceTrustValue;
	}

	public void setDeviceTrustValue(double deviceTrustValue) {
		this.deviceTrustValue = deviceTrustValue;
	}

	public String getDeviceOwnedBy() {
		return deviceOwnedBy;
	}

	public void setDeviceOwnedBy(String deviceOwnedBy) {
		this.deviceOwnedBy = deviceOwnedBy;
	}

	public int getDeviceHasPassword() {
		return deviceHasPassword;
	}

	public void setDeviceHasPassword(int deviceHasPassword) {
		this.deviceHasPassword = deviceHasPassword;
	}

	public BigInteger getDeviceScreenTimeout() {
		return deviceScreenTimeout;
	}

	public void setDeviceScreenTimeout(BigInteger deviceScreenTimeout) {
		this.deviceScreenTimeout = deviceScreenTimeout;
	}

	public int getDeviceHasAccessibility() {
		return deviceHasAccessibility;
	}

	public void setDeviceHasAccessibility(int deviceHasAccessibility) {
		this.deviceHasAccessibility = deviceHasAccessibility;
	}

	public int getDeviceIsRooted() {
		return deviceIsRooted;
	}

	public void setDeviceIsRooted(int deviceIsRooted) {
		this.deviceIsRooted = deviceIsRooted;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppVendor() {
		return appVendor;
	}

	public void setAppVendor(String appVendor) {
		this.appVendor = appVendor;
	}

	public int getAppMUSESAware() {
		return appMUSESAware;
	}

	public void setAppMUSESAware(int appMUSESAware) {
		this.appMUSESAware = appMUSESAware;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public double getAssetValue() {
		return assetValue;
	}

	public void setAssetValue(double assetValue) {
		this.assetValue = assetValue;
	}

	public String getAssetConfidentialLevel() {
		return assetConfidentialLevel;
	}

	public void setAssetConfidentialLevel(String assetConfidentialLevel) {
		this.assetConfidentialLevel = assetConfidentialLevel;
	}

	public String getAssetLocation() {
		return assetLocation;
	}

	public void setAssetLocation(String assetLocation) {
		this.assetLocation = assetLocation;
	}

	public int getMailRecipientAllowed() {
		return mailRecipientAllowed;
	}

	public void setMailRecipientAllowed(int mailRecipientAllowed) {
		this.mailRecipientAllowed = mailRecipientAllowed;
	}

	public int getMailContainsCC() {
		return mailContainsCC;
	}

	public void setMailContainsCC(int mailContainsCC) {
		this.mailContainsCC = mailContainsCC;
	}

	public int getMailContainsBCC() {
		return mailContainsBCC;
	}

	public void setMailContainsBCC(int mailContainsBCC) {
		this.mailContainsBCC = mailContainsBCC;
	}

	public int getMailHasAttachment() {
		return mailHasAttachment;
	}

	public void setMailHasAttachment(int mailHasAttachment) {
		this.mailHasAttachment = mailHasAttachment;
	}

	public String getWifiEncryption() {
		return wifiEncryption;
	}

	public void setWifiEncryption(String wifiEncryption) {
		this.wifiEncryption = wifiEncryption;
	}

	public int getWifiEnabled() {
		return wifiEnabled;
	}

	public void setWifiEnabled(int wifiEnabled) {
		this.wifiEnabled = wifiEnabled;
	}

	public int getWifiConnected() {
		return wifiConnected;
	}

	public void setWifiConnected(int wifiConnected) {
		this.wifiConnected = wifiConnected;
	}

}
