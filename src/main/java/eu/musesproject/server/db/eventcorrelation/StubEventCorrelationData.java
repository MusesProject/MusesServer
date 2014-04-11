package eu.musesproject.server.db.eventcorrelation;

import java.util.ArrayList;

import eu.musesproject.client.model.contextmonitoring.Event;




public class StubEventCorrelationData {
	
	ArrayList<Event> eventList = null;
	
	public boolean addEvent(Event event){
		if(eventList == null){
			eventList = new ArrayList<Event>();
			eventList.add(event);
			return true;
		}
		return false;
	}
	
	public ArrayList<Event> getEventList(){
		return eventList;
	}
}
