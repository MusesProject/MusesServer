package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the sources database table.
 * 
 */
@Entity
@Table(name="sources")
@NamedQuery(name="Sources.findAll", query="SELECT s FROM Sources s")
public class Sources implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="source_id", unique=true, nullable=false)
	private String sourceId;

	@Column(nullable=false, length=50)
	private String name;

	//bi-directional many-to-one association to SecurityRules
	@OneToMany(mappedBy="source")
	private List<SecurityRules> securityRules;

	//bi-directional many-to-one association to SimpleEvents
	@OneToMany(mappedBy="source")
	private List<SimpleEvents> simpleEvents;

	public Sources() {
	}

	public String getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SecurityRules> getSecurityRules() {
		return this.securityRules;
	}

	public void setSecurityRules(List<SecurityRules> securityRules) {
		this.securityRules = securityRules;
	}

	public SecurityRules addSecurityRule(SecurityRules securityRule) {
		getSecurityRules().add(securityRule);
		securityRule.setSource(this);

		return securityRule;
	}

	public SecurityRules removeSecurityRule(SecurityRules securityRule) {
		getSecurityRules().remove(securityRule);
		securityRule.setSource(null);

		return securityRule;
	}

	public List<SimpleEvents> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvents> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvents addSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setSource(this);

		return simpleEvent;
	}

	public SimpleEvents removeSimpleEvent(SimpleEvents simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setSource(null);

		return simpleEvent;
	}

}