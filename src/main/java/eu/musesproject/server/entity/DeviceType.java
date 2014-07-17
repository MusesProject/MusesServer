package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the device_type database table.
 * 
 */
@Entity
@Table(name="device_type")
@NamedQuery(name="DeviceType.findAll", query="SELECT d FROM DeviceType d")
public class DeviceType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="device_type_id")
	private int deviceTypeId;

	private String description;

	private String type;

	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="deviceType")
	private List<Device> devices;

	public DeviceType() {
	}

	public int getDeviceTypeId() {
		return this.deviceTypeId;
	}

	public void setDeviceTypeId(int deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
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

	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public Device addDevice(Device device) {
		getDevices().add(device);
		device.setDeviceType(this);

		return device;
	}

	public Device removeDevice(Device device) {
		getDevices().remove(device);
		device.setDeviceType(null);

		return device;
	}

}