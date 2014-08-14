package eu.musesproject.server.scheduler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.server.connectionmanager.SessionHandler;
import eu.musesproject.server.entity.LegalAspect;
import eu.musesproject.server.entity.SimpleEvent;

public class SchedulerImpl implements Scheduler {

	private final EntityManagerFactory emf;
	private final EntityManager em;
	private Logger logger = Logger.getLogger(SessionHandler.class.getName());
	private static final String MUSES_TAG = "MUSES_TAG";
	
	public SchedulerImpl() {
		logger = Logger.getRootLogger();
		BasicConfigurator.configure();
		logger.setLevel(Level.INFO);
	    emf = Persistence.createEntityManagerFactory("server"); // FIXME change to muses
	    em = emf.createEntityManager();		
	}
	
	@Override
	public void erase() {
		List<SimpleEvent> simpleEvents = em.createNamedQuery("SimpleEvent.findAll",SimpleEvent.class).getResultList();
		for (SimpleEvent event: simpleEvents){
			boolean [] results = checkHardLimit(event);
			boolean isKRSAllowed = results[0];
			boolean isEPAllowed = results[1];
			boolean isRT2AEAllowed = results[2];
			if (!isKRSAllowed && !isEPAllowed && !isRT2AEAllowed)  {
				try {
					logger.log(Level.INFO, MUSES_TAG + " Hard limit expired for all allowed components, cleaning up the event:" + event.getEventId());
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
				logger.log(Level.INFO, MUSES_TAG + " Update component mask for event:" + event.getEventId());
				event.setKRS_can_access(isKRSAllowed?new byte[]{1}:new byte[]{0});
				event.setEP_can_access(isKRSAllowed?new byte[]{1}:new byte[]{0});
				event.setRT2AE_can_access(isKRSAllowed?new byte[]{1}:new byte[]{0});
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
		long timeDiff = now.getTime() - simpleEvent.getDate().getTime();
		long daysInBetween = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
		if (daysInBetween > numberOfdaysAllowedToStoreDataInKRS) {
			accessDBallowed[0]=false;
		} else {
			accessDBallowed[0]=true;
		}
		if (daysInBetween > numberOfdaysAllowedToStoreDataInEP) {
			accessDBallowed[1]=false;
		} else {
			accessDBallowed[1]=true;
		}
		if (daysInBetween > numberOfdaysAllowedToStoreDataInRT2AE) {
			accessDBallowed[2]=false;
		} else {
			accessDBallowed[2]=true;
		}
		return accessDBallowed;
	}

	
}
