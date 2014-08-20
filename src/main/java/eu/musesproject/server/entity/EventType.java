package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the event_type database table.
 * 
 */
@Entity
@Table(name="event_type")
@NamedQueries({
	@NamedQuery(name="EventType.findAll", 
				query="SELECT e FROM EventType e"),
	@NamedQuery(name="EventType.findByKey", 
				query="SELECT e FROM EventType e where e.eventTypeKey = :eventTypeKey")
})
public class EventType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_type_id")
	private int eventTypeId;

	@Column(name="event_level")
	private String eventLevel;

	@Column(name="event_type_key")
	private String eventTypeKey;

	//bi-directional many-to-one association to SimpleEvent
	@OneToMany(mappedBy="eventType")
	private List<SimpleEvent> simpleEvents;

	public EventType() {
	}

	public int getEventTypeId() {
		return this.eventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public String getEventLevel() {
		return this.eventLevel;
	}

	public void setEventLevel(String eventLevel) {
		this.eventLevel = eventLevel;
	}

	public String getEventTypeKey() {
		return this.eventTypeKey;
	}

	public void setEventTypeKey(String eventTypeKey) {
		this.eventTypeKey = eventTypeKey;
	}

	public List<SimpleEvent> getSimpleEvents() {
		return this.simpleEvents;
	}

	public void setSimpleEvents(List<SimpleEvent> simpleEvents) {
		this.simpleEvents = simpleEvents;
	}

	public SimpleEvent addSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().add(simpleEvent);
		simpleEvent.setEventType(this);

		return simpleEvent;
	}

	public SimpleEvent removeSimpleEvent(SimpleEvent simpleEvent) {
		getSimpleEvents().remove(simpleEvent);
		simpleEvent.setEventType(null);

		return simpleEvent;
	}

}