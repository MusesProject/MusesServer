package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name="zone")
@NamedQueries({
	@NamedQuery(name="Zone.findAll", 
				query="SELECT z FROM Zone z"),
	@NamedQuery(name="Zone.findByDescription", 
				query="SELECT z FROM Zone z where z.description = :description")
})
public class Zone implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="zone_id", unique=true, nullable=false)
	private int zoneId;

	@Column(length=100)
	private String description;
	
	@Column(name="role_id")
	private int roleId;

	@Column(name="long")
	private double longitud;
	
	@Column(name="lat")
	private double latitude;
	
	@Column(name="radius")
	private float radius;

	public Zone() {
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}


}