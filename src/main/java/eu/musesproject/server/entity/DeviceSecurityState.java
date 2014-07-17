package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the device_security_state database table.
 * 
 */
@Entity
@Table(name="device_security_state")
@NamedQuery(name="DeviceSecurityState.findAll", query="SELECT d FROM DeviceSecurityState d")
public class DeviceSecurityState implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="device_security_state_id")
	private String deviceSecurityStateId;

	public DeviceSecurityState() {
	}

	public String getDeviceSecurityStateId() {
		return this.deviceSecurityStateId;
	}

	public void setDeviceSecurityStateId(String deviceSecurityStateId) {
		this.deviceSecurityStateId = deviceSecurityStateId;
	}

}