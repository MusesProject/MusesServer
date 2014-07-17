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
	@Column(name="app_type_id")
	private String appTypeId;

	private String description;

	private String type;

	//bi-directional many-to-one association to Application
	@OneToMany(mappedBy="appType")
	private List<Application> applications;

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

	public List<Application> getApplications() {
		return this.applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public Application addApplication(Application application) {
		getApplications().add(application);
		application.setAppType(this);

		return application;
	}

	public Application removeApplication(Application application) {
		getApplications().remove(application);
		application.setAppType(null);

		return application;
	}

}