package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the security_violation database table.
 * 
 */
@Entity
@Table(name="security_violation")
@NamedQueries ({
	@NamedQuery(name="SecurityViolation.findAll", 
				query="SELECT s FROM SecurityViolation s"),
	@NamedQuery(name="SecurityViolation.findByEventId", 
    			query="SELECT s FROM SecurityViolation s where s.eventId = :event_id")

})
public class SecurityViolation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="security_violation_id", unique=true, nullable=false)
	private String securityViolationId;

	@Column(length=1000)
	private String message;
	
	@Column(length=1000)
	private String conditionText;
	
	@Column(length=1000)
	private String modeText;


	@Column(name="decision_id")
	private BigInteger decisionId;

	@Column(name="event_id", nullable=false)
	private BigInteger eventId;
	
	@Column(name="user_id", nullable=false)
	private BigInteger userId;
	
	@Column(name="device_id", nullable=false)
	private BigInteger deviceId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date detection;



	public SecurityViolation() {
	}



	public String getSecurityViolationId() {
		return securityViolationId;
	}



	public void setSecurityViolationId(String securityViolationId) {
		this.securityViolationId = securityViolationId;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public String getConditionText() {
		return conditionText;
	}



	public void setConditionText(String conditionText) {
		this.conditionText = conditionText;
	}



	public String getModeText() {
		return modeText;
	}



	public void setModeText(String modeText) {
		this.modeText = modeText;
	}



	public BigInteger getDecisionId() {
		return decisionId;
	}



	public void setDecisionId(BigInteger decisionId) {
		this.decisionId = decisionId;
	}



	public BigInteger getEventId() {
		return eventId;
	}



	public void setEventId(BigInteger eventId) {
		this.eventId = eventId;
	}



	public BigInteger getUserId() {
		return userId;
	}



	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}



	public BigInteger getDeviceId() {
		return deviceId;
	}



	public void setDeviceId(BigInteger deviceId) {
		this.deviceId = deviceId;
	}



	public Date getDetection() {
		return detection;
	}



	public void setDetection(Date detection) {
		this.detection = detection;
	}

	
}