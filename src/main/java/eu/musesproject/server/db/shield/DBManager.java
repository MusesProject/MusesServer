package eu.musesproject.server.db.shield;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import eu.musesproject.server.entity.SimpleEvent;

public class DBManager {
	
	private final EntityManagerFactory emf;
	private final EntityManager em;
	Module module;
	
	public DBManager(Module module) {
		this.module = module;
	    emf = Persistence.createEntityManagerFactory("server"); // FIXME change to muses
	    em = emf.createEntityManager();	
	}
	
	public void inform(String module) {
		
	}
	
	// Normal DB access methods
	public void insert(Object obj){
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(obj);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
		
	}
	
	public List<SimpleEvent> getEvent(){
		List<SimpleEvent> simpleEvents;
		simpleEvents = em.createNamedQuery("SimpleEvent.findAll",SimpleEvent.class).getResultList();
		List<SimpleEvent> allowedEvents = new ArrayList<SimpleEvent>();
		for (SimpleEvent event : simpleEvents) {
			if (event.getKRS_can_access()[0] == 1){
				allowedEvents.add(event);
			}
		}
		return allowedEvents;
	}
	
	
	
}
