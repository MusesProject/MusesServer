package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the app_type database table.
 * 
 */
@Entity
@Table(name="app_type")
@NamedQuery(name="AppType.findAll", query="SELECT a FROM AppType a")
public class AppType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="app_type_id", unique=true, nullable=false)
	private String appTypeId;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=30)
	private String type;

	//bi-directional many-to-one association to Applications
	@OneToMany(mappedBy="appType")
	private List<Applications> applications;

	public AppType() {
	}

	public String getAppTypeId() {
		return this.appTypeId;
	}

	public void setAppTypeId(String appTypeId) {
		this.appTypeId = appTypeId;
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

	public List<Applications> getApplications() {
		return this.applications;
	}

	public void setApplications(List<Applications> applications) {
		this.applications = applications;
	}

	public Applications addApplication(Applications application) {
		getApplications().add(application);
		application.setAppType(this);

		return application;
	}

	public Applications removeApplication(Applications application) {
		getApplications().remove(application);
		application.setAppType(null);

		return application;
	}

}