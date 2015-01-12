package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the muses_config database table.
 * 
 */
@Entity
@Table(name="sensor_configuration")
@NamedQueries({
	@NamedQuery(name="SensorConfiguration.findAll", query="SELECT c FROM SensorConfiguration c"),
})

public class SensorConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	private int id;

	@Column(name="sensor_type", nullable=false, length=45)
	private String sensorType;
	
	@Column(name="keyproperty", nullable=false, length=45)
	private String keyProperty;
	
	@Column(name="valueproperty", nullable=false, length=45)
	private String valueProperty;

	public SensorConfiguration() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public String getKeyProperty() {
		return keyProperty;
	}

	public void setKeyProperty(String keyProperty) {
		this.keyProperty = keyProperty;
	}

	public String getValueProperty() {
		return valueProperty;
	}

	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}


}