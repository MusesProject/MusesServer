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
@NamedQuery(name="Application.findAll", query="SELECT a FROM Application a")
public class Application implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="app_id")
	private int appId;

	private String description;

	private byte[] is_MUSES_aware;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_update")
	private Date lastUpdate;

	private String name;

	private String vendor;

	private String version;

	//bi-directional many-to-one association to AppType
	@ManyToOne
	@JoinColumn(name="type")
	private AppType appType;

	//bi-directional many-to-one association to SimpleEvent
	@OneToMany(mappedBy="application")
	private List<SimpleEvent> simpleEvents;

	public Application() {
	}

	public int getAppId() {
		return this.appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getIs_MUSES_aware() {
		return this.is_MUSES_aware;
	}

	public void setIs_MUSES_aware(byte[] is_MUSES_aware) {
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

	public List<SimpleEvent> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvent> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvent addSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setApplication(this);

		return simpleEvent;
	}

	public SimpleEvent removeSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setApplication(null);

		return simpleEvent;
	}

}