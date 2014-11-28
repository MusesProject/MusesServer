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
	@Column(name="device_type_id", unique=true, nullable=false)
	private int deviceTypeId;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=30)
	private String type;

	//bi-directional many-to-one association to Devices
	@OneToMany(mappedBy="deviceType")
	private List<Devices> devices;

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

	public List<Devices> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Devices> devices) {
		this.devices = devices;
	}

	public Devices addDevice(Devices device) {
		getDevices().add(device);
		device.setDeviceType(this);

		return device;
	}

	public Devices removeDevice(Devices device) {
		getDevices().remove(device);
		device.setDeviceType(null);

		return device;
	}

}