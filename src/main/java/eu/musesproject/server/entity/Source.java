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
@NamedQuery(name="Source.findAll", query="SELECT s FROM Source s")
public class Source implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="source_id")
	private String sourceId;

	private String name;

	//bi-directional many-to-one association to SecurityRule
	@OneToMany(mappedBy="source")
	private List<SecurityRule> securityRules;

	//bi-directional many-to-one association to SimpleEvent
	@OneToMany(mappedBy="source")
	private List<SimpleEvent> simpleEvents;

	public Source() {
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

	public List<SecurityRule> getSecurityRules() {
		return this.securityRules;
	}

	public void setSecurityRules(List<SecurityRule> securityRules) {
		this.securityRules = securityRules;
	}

	public SecurityRule addSecurityRule(SecurityRule securityRule) {
		getSecurityRules().add(securityRule);
		securityRule.setSource(this);

		return securityRule;
	}

	public SecurityRule removeSecurityRule(SecurityRule securityRule) {
		getSecurityRules().remove(securityRule);
		securityRule.setSource(null);

		return securityRule;
	}

	public List<SimpleEvent> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvent> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvent addSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setSource(this);

		return simpleEvent;
	}

	public SimpleEvent removeSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setSource(null);

		return simpleEvent;
	}

}