package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the device_config database table.
 * 
 */
@Entity
@Table(name="device_config")
@NamedQuery(name="DeviceConfig.findAll", query="SELECT d FROM DeviceConfig d")
public class DeviceConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="device_config_id", unique=true, nullable=false)
	private int deviceConfigId;

	@Column(name="device_config_name", nullable=false, length=30)
	private String deviceConfigName;

	@Column(name="max_request_time", nullable=false)
	private int maxRequestTime;

	@Column(name="min_event_cache_size", nullable=false)
	private int minEventCacheSize;

	public DeviceConfig() {
	}

	public int getDeviceConfigId() {
		return this.deviceConfigId;
	}

	public void setDeviceConfigId(int deviceConfigId) {
		this.deviceConfigId = deviceConfigId;
	}

	public String getDeviceConfigName() {
		return this.deviceConfigName;
	}

	public void setDeviceConfigName(String deviceConfigName) {
		this.deviceConfigName = deviceConfigName;
	}

	public int getMaxRequestTime() {
		return this.maxRequestTime;
	}

	public void setMaxRequestTime(int maxRequestTime) {
		this.maxRequestTime = maxRequestTime;
	}

	public int getMinEventCacheSize() {
		return this.minEventCacheSize;
	}

	public void setMinEventCacheSize(int minEventCacheSize) {
		this.minEventCacheSize = minEventCacheSize;
	}

}