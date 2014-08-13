package eu.musesproject.server.scheduler;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import eu.musesproject.server.entity.LegalAspect;
import eu.musesproject.server.entity.SimpleEvent;

public class SchedulerImpl implements Scheduler {

	private final EntityManagerFactory emf;
	private final EntityManager em;
	
	
	public SchedulerImpl() {
	    emf = Persistence.createEntityManagerFactory("server"); // FIXME change to muses
	    em = emf.createEntityManager();		
	}
	
	@Override
	public void erase() {
		List<SimpleEvent> simpleEvents = em.createNamedQuery("SimpleEvent.findAll",SimpleEvent.class).getResultList();
		for (SimpleEvent event: simpleEvents){
			boolean [] results = checkHardLimit(event);
			boolean isKRSlimitExpired = results[0];
			boolean isEPlimitExpired = results[1];
			boolean isRT2AESlimitExpired = results[2];
			if (isKRSlimitExpired && isEPlimitExpired && isRT2AESlimitExpired)  {
				try {
					EntityTransaction entityTransaction = em.getTransaction();
					entityTransaction.begin();
					em.remove(event);
					entityTransaction.commit();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					em.close();
				}
			} else {
				event.setKRS_can_access(isKRSlimitExpired?new byte[]{0}:new byte[]{1});
				event.setEP_can_access(isKRSlimitExpired?new byte[]{0}:new byte[]{1});
				event.setRT2AE_can_access(isKRSlimitExpired?new byte[]{0}:new byte[]{1});
			}

		}
	}

	private boolean [] checkHardLimit(SimpleEvent simpleEvent){
		boolean [] accessDBallowed = {true,true,true};
		LegalAspect legalAspect = em.createNamedQuery("LegalAspect.findAll", LegalAspect.class).getSingleResult();
		int numberOfdaysAllowedToStoreDataInKRS = legalAspect.getKRS_hard_limit();
		int numberOfdaysAllowedToStoreDataInEP = legalAspect.getEP_hard_limit();
		int numberOfdaysAllowedToStoreDataInRT2AE = legalAspect.getRT2AE_hard_limit();
		Date now = new Date();
		int dayInBetween = (int)( (simpleEvent.getDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
		if (dayInBetween > numberOfdaysAllowedToStoreDataInKRS) accessDBallowed[0]=false;
		else accessDBallowed[0]=true;
		if (dayInBetween > numberOfdaysAllowedToStoreDataInEP) accessDBallowed[1]=false;
		else accessDBallowed[1]=true;
		if (dayInBetween > numberOfdaysAllowedToStoreDataInRT2AE) accessDBallowed[2]=false;
		else accessDBallowed[2]=true;
		return accessDBallowed;
	}

	
}
