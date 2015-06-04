package eu.musesproject.server.scheduler;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.server.connectionmanager.SessionHandler;
import eu.musesproject.server.entity.LegalAspects;
import eu.musesproject.server.entity.SimpleEvents;

public class SchedulerImpl implements Scheduler {

	private final EntityManagerFactory emf;
	private final EntityManager em;
	private Logger logger = Logger.getLogger(SessionHandler.class.getName());
	private static final String MUSES_TAG = "MUSES_TAG";
	
	public SchedulerImpl() {
	    emf = Persistence.createEntityManagerFactory("server"); // FIXME change to muses
	    em = emf.createEntityManager();		
	}
	
	@Override
	public void erase() {
		List<SimpleEvents> simpleEvents = em.createNamedQuery("SimpleEvent.findAll",SimpleEvents.class).getResultList();
		for (SimpleEvents event: simpleEvents){
			boolean [] results = checkHardLimit(event);
			boolean isKRSAllowed = results[0];
			boolean isEPAllowed = results[1];
			boolean isRT2AEAllowed = results[2];
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			if (!isKRSAllowed && !isEPAllowed && !isRT2AEAllowed)  {
				try {
					logger.log(Level.INFO, MUSES_TAG + " Hard limit expired for all allowed components, cleaning up the event:" + event.getEventId());
					em.remove(event);
					entityTransaction.commit();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					em.close();
				}
			} else {
				logger.log(Level.INFO, MUSES_TAG + " Update component mask for event:" + event.getEventId());
				event.setKRS_can_access(isKRSAllowed?1:0);
				event.setEP_can_access(isEPAllowed?1:0);
				event.setRT2AE_can_access(isRT2AEAllowed?1:0);
				try {
					entityTransaction.commit();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					em.close();
				}
			}

		}
	}

	private boolean [] checkHardLimit(SimpleEvents simpleEvent){
		boolean [] accessDBallowed = {true,true,true};
		LegalAspects legalAspect = em.createNamedQuery("LegalAspect.findAll", LegalAspects.class).getSingleResult();
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
