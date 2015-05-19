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
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
			query="SELECT p FROM PatternsKrs p")
})
public class PatternsKrs implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="log_entry_id", unique=true, nullable=false)
	private BigInteger logEntryId;

	@Column(name="label", nullable=false)
	private String label;

	@Column(name="decision_cause", nullable=false)
	private String decisionCause;
	
	@Column(name="silent_mode", nullable=false)
	private int silentMode;

	@Column(name="event_type", nullable=false)
	private String eventType;
	
	@Column(name="event_level", nullable=false)
	private String eventLevel;
	
	@Column(name="username", nullable=false)
	private String username;
	
	@Column(name="password_length", nullable=false)
	private int passwordLength;
	
	@Column(name="letters_in_password", nullable=false)
	private int lettersInPassword;
	
	@Column(name="numbers_in_password", nullable=false)
	private int numbersInPassword;
	
	@Column(name="passwd_has_capital_letters", nullable=false)
	private int passwdHasCapitalLetters;
	
	@Column(name="user_trust_value", nullable=false)
	private double userTrustValue;
	
	@Column(name="activated_account", nullable=false)
	private int activatedAccount;
	
	@Column(name="user_role", nullable=false)
	private String userRole;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="event_detection", nullable=false)
	private Date eventTime;
	
	@Column(name="device_type", nullable=false)
	private String deviceType;
	
	@Column(name="device_OS", nullable=false)
	private String deviceOS;
	
	@Column(name="device_has_antivirus", nullable=false)
	private int deviceHasAntivirus;
	
	@Column(name="device_has_certificate", nullable=false)
	private int deviceHasCertificate;
	
	@Column(name="device_trust_value", nullable=false)
	private double deviceTrustValue;
	
	@Column(name="device_security_level", nullable=false)
	private short deviceSecurityLevel;
	
	@Column(name="device_owned_by", nullable=false)
	private String deviceOwnedBy;
	
	@Column(name="device_has_password", nullable=false)
	private int deviceHasPassword;
	
	@Column(name="device_screen_timeout", nullable=false)
	private BigInteger deviceScreenTimeout;
	
	@Column(name="device_has_accessibility", nullable=false)
	private int deviceHasAccessibility;
	
	@Column(name="device_is_rooted", nullable=false)
	private int deviceIsRooted;
	
	@Column(name="app_name", nullable=false)
	private String appName;
	
	@Column(name="app_vendor", nullable=false)
	private String appVendor;
	
	@Column(name="app_is_MUSES_aware", nullable=false)
	private int appMUSESAware;
	
	@Column(name="asset_name", nullable=false)
	private String assetName;
	
	@Column(name="asset_value", nullable=false)
	private double assetValue;
	
	@Column(name="asset_confidential_level", nullable=false)
	private String assetConfidentialLevel;
	
	@Column(name="asset_location", nullable=false)
	private String assetLocation;
	
	@Column(name="mail_recipient_allowed", nullable=false)
	private int mailRecipientAllowed;
	
	@Column(name="mail_contains_cc_allowed", nullable=false)
	private int mailContainsCC;
	
	@Column(name="mail_contains_bcc_allowed", nullable=false)
	private int mailContainsBCC;
	
	@Column(name="mail_has_attachment", nullable=false)
	private int mailHasAttachment;

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

	public short getDeviceSecurityLevel() {
		return deviceSecurityLevel;
	}

	public void setDeviceSecurityLevel(short deviceSecurityLevel) {
		this.deviceSecurityLevel = deviceSecurityLevel;
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

}
