package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the applications database table.
 * 
 */
@Entity
@Table(name="applications")
@NamedQuery(name="Applications.findAll", query="SELECT a FROM Applications a")
public class Applications implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="app_id", unique=true, nullable=false)
	private String appId;

	@Column(length=100)
	private String description;

	private int is_MUSES_aware;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_update")
	private Date lastUpdate;

	@Column(nullable=false, length=30)
	private String name;

	@Column(length=30)
	private String vendor;

	@Column(length=20)
	private String version;

	//bi-directional many-to-one association to AppType
	@ManyToOne
	@JoinColumn(name="type")
	private AppType appType;

	//bi-directional many-to-one association to SimpleEvents
	@OneToMany(mappedBy="application")
	private List<SimpleEvents> simpleEvents;

	public Applications() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIs_MUSES_aware() {
		return this.is_MUSES_aware;
	}

	public void setIs_MUSES_aware(int is_MUSES_aware) {
		this.is_MUSES_aware = is_MUSES_aware;
	}

	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVendor() {
		return this.vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public AppType getAppType() {
		return this.appType;
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	public List<SimpleEvents> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvents> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvents addSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setApplication(this);

		return simpleEvent;
	}

	public SimpleEvents removeSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setApplication(null);

		return simpleEvent;
	}

}