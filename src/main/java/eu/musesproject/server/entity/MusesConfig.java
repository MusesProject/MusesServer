package eu.musesproject.server.entity;

import java.io.Serializable;

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

}