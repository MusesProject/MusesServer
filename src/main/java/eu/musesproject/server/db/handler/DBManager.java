package eu.musesproject.server.db.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.AdditionalProtection;
import eu.musesproject.server.entity.Asset;
import eu.musesproject.server.entity.Clue;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.Device;
import eu.musesproject.server.entity.Domain;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.Outcome;
import eu.musesproject.server.entity.RefinedSecurityRule;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.Role;
import eu.musesproject.server.entity.SecurityIncident;
import eu.musesproject.server.entity.SecurityRule;
import eu.musesproject.server.entity.SimpleEvent;
import eu.musesproject.server.entity.Threat;
import eu.musesproject.server.entity.ThreatClue;
import eu.musesproject.server.entity.User;
import eu.musesproject.server.entity.UserAuthorization;
import eu.musesproject.server.scheduler.ModuleType;

public class DBManager {
	
	private EntityManagerFactory emf;
	private EntityManager em;
	ModuleType module;
	
	public DBManager(ModuleType module) {
		this.module = module;
	}
	
	public void open(){
	    emf = Persistence.createEntityManagerFactory("server"); // FIXME change to muses
	    em = emf.createEntityManager();	
	}
	
	
	public void close() {
		if (emf != null) emf.close();
		if (em != null) em.close();
	}
	
	
	public void inform(SimpleEvent event) {
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			if (module.equals(ModuleType.KRS)){
				event.setKRS_can_access(new byte[]{0});
			}
			if (module.equals(ModuleType.EP)){
				event.setKRS_can_access(new byte[]{0});
			}

			if (module.equals(ModuleType.RT2AE)){
				event.setKRS_can_access(new byte[]{0});
			}
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
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
		List<SimpleEvent> simpleEvents = em.createNamedQuery("SimpleEvent.findAll",SimpleEvent.class).getResultList();
		List<SimpleEvent> allowedEvents = new ArrayList<SimpleEvent>();
		for (SimpleEvent event : simpleEvents) {
			if (event.getKRS_can_access()[0] == 1){
				allowedEvents.add(event);
			}
		}
		return allowedEvents;
	}
	
	
	
	// Complex DB method provided by Partners
	
	/**
	 * Get user by username
	 * @param username
	 * @return User
	 */
	
	public User getUserByUsername(String username) {
		List<User> userList = em.createNamedQuery("User.findByUsername",User.class)
				.setParameter("username", username)
				.setMaxResults(5)
				.getResultList();
		for (User u: userList){
			if (u.getUsername().equals(username)) {
				return u;
			}
		}
		return null;
	}
	
	/**
	 * Saves the user object in DB
	 * @param user
	 */
	
	public void saveUser(User user) {
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(user);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
	
	/**
	 * Get device object by IMEI number
	 * @param imei
	 * @return Device
	 */
	
	public Device getDeviceByIMEI(String imei){
		List<Device> deviceList = em.createNamedQuery("Device.findByIMEI",Device.class)
				.setParameter("imei", imei)
				.setMaxResults(5)
				.getResultList();
		for (Device d: deviceList) {
			if (d.getImei().equals(imei)){
				return d;
			}
		}
		return null;
	}
	
	/**
	 * Save device object in DB
	 * @param device
	 */
	
	public void saveDevice(Device device) {
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(device);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
	}
	
	/**
	 * Get role object by name
	 * @param name
	 * @return Role
	 */
	
	public Role getRoleByName(String name){
		List<Role> roleList = em.createNamedQuery("Role.findByName",Role.class)
				.setParameter("name", name)
				.setMaxResults(5)
				.getResultList();
		for (Role r: roleList) {
			if (r.getName().equals(name)){
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Get domain object by name 
	 * @param name
	 * @return Domain
	 */
	
	public Domain getDomainByName(String name){
		List<Domain> domainList = em.createNamedQuery("Domain.findByName",Domain.class)
				.setParameter("name", name)
				.setMaxResults(5)
				.getResultList();
		for (Domain d: domainList) {
			if (d.getName().equals(name)){
				return d;
			}
		}
		return null;
	}
	
	/**
	 * Get asset object by location
	 * @param location
	 * @return Asset
	 */
	
	public Asset getAssetByLocation(String location) {
		List<Asset> assetList = em.createNamedQuery("Asset.findByLocation",Asset.class)
				.setParameter("location", location)
				.setMaxResults(5)
				.getResultList();
		for (Asset a: assetList) {
			if (a.getLocation().equals(location)){
				return a;
			}
		}
		return null;
	}
	
	/**
	 * Get UserAuthorization object by userId of User object
	 * @param userId
	 * @return UserAuthorization
	 */
	
	public UserAuthorization getUserAuthByUserId(int userId) {
		List<UserAuthorization> userAuthorizationsList = em.createNamedQuery("UserAuthorization.findByUserID",UserAuthorization.class)
				.setParameter("userId", userId)
				.setMaxResults(5)
				.getResultList();
		for (UserAuthorization u: userAuthorizationsList) {
			if (u.getUserId() == userId){
				return u;
			}
		}
		return null;
	}
	
	/**
	 * Saves event object in the DB 
	 * @param event
	 */
	
	public void saveSimpleEvent(SimpleEvent event){
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(event);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

	}
	
	/**
	 * Save AccessRequest object in the DB 
	 * @param request
	 */
	
	public void saveAccessRequest(AccessRequest request){
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(request);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

	}
	
	/**
	 * Saves ThreatClue in the DB
	 * @param clue
	 */
	
    public void saveThreatClue(ThreatClue clue){
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(clue);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

    } 
    
    /**
     * Saves ThreatClue in the DB 
     * @param addProtection
     */
    
    public void saveAdditionalProtection(AdditionalProtection addProtection){
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(addProtection);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

    }
    
    /**
     * Save SecurityIncident in the DB 
     * @param secIncident
     */
    
    public void saveSecurityIncident(SecurityIncident secIncident){
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(secIncident);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

    }
    
    /**
     * Save EventType in the DB
     * @param type
     */
    
    public void saveEventType(EventType type) {
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(type);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

    }
    
    /**
     * Get EventType object by key 
     * @param key
     * @return EventType
     */
    
    public List<EventType> getEventTypeByKey(String key) {
		List<EventType> eventTypeList = em.createNamedQuery("EventType.findByKey",EventType.class)
				.setParameter("eventTypeKey", key)
				.setMaxResults(5)
				.getResultList(); 
		return eventTypeList;
    }
    
    /**
     * Save SecurityRule object in DB
     * @param rule
     */
    
    public void saveSecurityRule(SecurityRule rule) {
		try {
			EntityTransaction entityTransaction = em.getTransaction();
			entityTransaction.begin();
			em.persist(rule);
			entityTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
    }
    
    /**
     * Get SecurityRules list by status 
     * @param status
     * @return List<SecurityRule>
     */
    
    public List<SecurityRule> getSecurityRulesByStatus(String status) {
    	List<SecurityRule> securityRuleList = em.createNamedQuery("SecurityRule.findByStatus",SecurityRule.class)
				.setParameter("status", status)
				.getResultList();
		return securityRuleList;
    }

    /**
     * Get Decision by access request Id of AccessRequest object  
     * @param accessRequestId
     * @return List<Decision>
     */

    public List<Decision> getDecisionByAccessRequestId(int accessRequestId) {
    	AccessRequest accessRequest = em.createNamedQuery("AccessRequest.findById",AccessRequest.class)
    			.setParameter("accessRequestId", accessRequestId)
    			.getSingleResult();
    	return accessRequest.getDecisions();
    }
    
    /**
     * Get RefinedSecurityRule list by status
     * @param status
     * @return List<RefinedSecurityRule>
     */
    
    public List<RefinedSecurityRule> getRefinedSecurityRulesByStatus(String status) {
    	List<RefinedSecurityRule> refinedSecurityRuleList = em.createNamedQuery("RefinedSecurityRule.findByStatus",RefinedSecurityRule.class)
				.setParameter("status", status)
				.getResultList();
    	List<RefinedSecurityRule> foundList = new ArrayList<RefinedSecurityRule>();
		for (RefinedSecurityRule r: refinedSecurityRuleList){
			if (r.getStatus().equals(status)){
				foundList.add(r);
			}
		}
		return foundList;
    }
    
//  public List<BlackList> getFullBlacklist() {  // FIXME no black list table
//	
//}

    			
    	/**----------------------------------------------------------------**/
    
    					/*** START RT2AE DB METHODS***/
    
     /**----------------------------------------------------------------**/
   
    
    
    /**
     * Get Users list 
     * @return List<User>
     */
	public List<User> getUsers() {	
		List<User> users = em.createNamedQuery("User.findAll",User.class).getResultList();
		return users;
	}
    
	/**
     * Find User list by username 
     * @param username
     */
	public List<User> findUserByUsername(String username) {
		List<User> users = em.createNamedQuery("User.findByUsername",User.class)
				.setParameter("username", username)
				.getResultList();
	
		return users;
	}
	
	/**
     * Save User list in the DB 
     * @param User
     */
	public void setUsers(List<User> users) {
		Iterator<User> i = users.iterator();
		while(i.hasNext()){
			User user = i.next();
			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				System.out.println("ok");
				em.persist(user);
				entityTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//em.close();
			}
		}

	}
	
	/**
     * Get Device list by id 
     * @param device_id
     * @return List<Device>
     */
	public  List<Device> findDeviceById(int device_id) {
  		
		List<Device> devices = em.createNamedQuery("Device.findById",Device.class)
				.setParameter("device_id", device_id)
				.getResultList();
		return devices;		
	}
	
	/**
     * Get Device list by id 
     * @param device_id
     * @return List<Device>
     */
	public  void merge(Device  device) {
  		
		EntityTransaction entityTransaction = em.getTransaction();
		entityTransaction.begin();
		em.persist(device);
		entityTransaction.commit();
	}
	
	 /**
     * Get Asset list
     * @return List<Asset>
     */
    public List<Asset> getAssets() {
    	
    	
		List<Asset> assets = em.createNamedQuery("Asset.findAll",Asset.class).getResultList();
		

		return assets;		
	}
    
    /**
     * Get Asset list by title
     * @param title
     * @return List<Asset>
     */
    public List<Asset> findAssetByTitle(String title) {
    
		List<Asset> assets = em.createNamedQuery("Asset.findByTitle",Asset.class)
							.setParameter("title", title)
							.getResultList();

		return assets;		
	}
    
    /**
     * Delete Asset by description 
     * @param title
     */
	public void deleteAssetByTitle(String title) {
				
		em.createNamedQuery("Asset.deleteAssetByTitle",Asset.class)
				.setParameter("title", title)
				.getResultList();
				
	}
      
    /**
     * Save Asset list in the DB 
     * @param Asset
     */
	public void setAssets(List<Asset> assets) {
		Iterator<Asset> i = assets.iterator();
		while(i.hasNext()){
			Asset asset = i.next();
			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				em.persist(asset);
				entityTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//em.close();
			}
		}
	}
	
	
	/*public List<ThreatClue> getClues() {
			List<ThreatClue> clues = em.createNamedQuery("ThreatClue.findAll",ThreatClue.class).getResultList();
			return clues;		
		}
	
	
	public void setThreatClues(List<ThreatClue> clues) {
		Iterator<ThreatClue> i = clues.iterator();
		while(i.hasNext()){
			ThreatClue clue = i.next();
			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				em.persist(clue);
				entityTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				em.close();
			}
		}
	}*/
	
	 /**
     * Get Clue list
     * @return List<Clue>
     */
	public List<Clue> getClues() {
			
		List<Clue> clues = em.createNamedQuery("Clue.findAll",Clue.class).getResultList();
		
		return clues;		
	}

	
	/**
     * Get Clue list by value
     * @param value
     * @return List<Clue>
     */
    public List<Clue> findClueByValue(String value) {
    	
		List<Clue> clues = em.createNamedQuery("Clue.findByValue",Clue.class)
							.setParameter("value", value)
							.getResultList();

		return clues;		
	}
    
    /**
     * Delete Clue by value 
     * @param value
     */
	public void deleteClueByValue(String value) {
				
		em.createNamedQuery("Asset.deleteClueByValue",Clue.class)
				.setParameter("value", value)
				.getResultList();
				
	}

	/**
     * Save Clue list in the DB 
     * @param Clue
     */
	public void setClues(List<Clue> clues) {
		Iterator<Clue> i = clues.iterator();
		while(i.hasNext()){
			Clue clue = i.next();
			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				em.persist(clue);
				entityTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//em.close();
			}
		}
	}
	
	/**
     * Get Threat list
     * @return List<Threat>
     */
	public List<Threat> getThreats() {
		

				
		List<Threat> threats = em.createNamedQuery("Threat.findAll",Threat.class).getResultList();
		
		return threats;		
	}
	
	 /**
     * Get Threat list by description
     * @param description
     * @return List<Threat>
     */
	public List<Threat> findThreatbydescription(String description) {
				
		List<Threat> threat = em.createNamedQuery("Threat.findThreatbyDescription",Threat.class)
				.setParameter("description", description)
				.getResultList();
		return threat;		
	}
	
	/**
     * Get Threat list by id
     * @param id
     * @return List<Threat>
     */
	public List<Threat> findThreatById(Threat threat_id) {
				
		List<Threat> threat = em.createNamedQuery("Threat.findThreatById",Threat.class)
				.setParameter("threat_id", threat_id)
				.getResultList();
		return threat;		
	}
	
	/**
     * Save Threat list in the DB 
     * @param Threat
     */
	public void setThreats(List<Threat> threats) {
		
		Iterator<Threat> i = threats.iterator();
		while(i.hasNext()){
			Threat threat = i.next();
			Threat threat1 = new Threat();

		
			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				if (this.findThreatbydescription(threat.getDescription()).size()>0){
					List<Threat>	listtThreats = this.findThreatbydescription(threat.getDescription());
					listtThreats.get(0).setOccurences(threat.getOccurences());
					listtThreats.get(0).setProbability(threat.getProbability());
					listtThreats.get(0).setBadOutcomeCount(threat.getBadOutcomeCount());
					listtThreats.get(0).setDescription(threat.getDescription());
					em.merge(listtThreats.get(0));
					entityTransaction.commit();


				}else{
					threat1.setDescription(threat.getDescription());
					threat1.setProbability(threat.getProbability());
					threat1.setBadOutcomeCount(threat.getBadOutcomeCount());
					threat1.setOccurences(threat.getOccurences());
					em.persist(threat1);
					entityTransaction.commit();

					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//em.close();
			}
			
			//this.setOutcomes(threat.getOutcomes());
			Iterator<Outcome> o = threat.getOutcomes().iterator();
			while(o.hasNext()){
				Outcome outcome = o.next();

				try {
					EntityTransaction entityTransaction = em.getTransaction();
					entityTransaction.begin();
					List<Threat> t = this.findThreatbydescription(threat.getDescription());
					outcome.setThreat(t.get(0));
					em.persist(outcome);
					entityTransaction.commit();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					//em.close();
				}
				
			}
		}
	}
	
	 /**
     * Delete Threat by description 
     * @param descritpion
     */
	public void deletefThreatByDescription(String description) {
				
		em.createNamedQuery("Threat.deleteContentOfThreatTable",Threat.class)
				.setParameter("description", description)
				.getResultList();
				
	}
	
	/**
     * Get RiskPolicy list
     * @return List<RiskPolicy>
     */
	public List<RiskPolicy> getRiskPolicies() {
			
		List<RiskPolicy> riskpolicy = em.createNamedQuery("RiskPolicy.findAll",RiskPolicy.class).getResultList();

		return riskpolicy;				
	}


	/**
     * Save RiskPolicy list in the DB 
     * @param riskPolicies
     */
	public void setRiskPolicies(List<RiskPolicy> riskPolicies) {
		Iterator<RiskPolicy> i = riskPolicies.iterator();
		while(i.hasNext()){
			RiskPolicy riskpolicy = i.next();
			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				em.persist(riskpolicy);
				entityTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//em.close();
			}
		}
	}
	
	/**
     * Get Outcome list
     * @return List<Outcome>Outcomes
     */
	public List<Outcome> getOutcomes() {
			
		List<Outcome> outcome = em.createNamedQuery("Outcome.findAll",Outcome.class).getResultList();

		return outcome;				
	}


	
	
	/**
     * Get AccessRequest list
     * @return List<AccessRequest>
     */
	public List<AccessRequest> getAccessRequests() {
		
			
		List<AccessRequest> accesrequests = em.createNamedQuery("AccessRequest.findAll",AccessRequest.class).getResultList();

		return accesrequests;
	}
	
	/**
     * Save AccessRequest list in the DB 
     * @param accessRequests
     */
	public void setAccessRequests(List<AccessRequest> accessRequests) {
				
		Iterator<AccessRequest> i = accessRequests.iterator();
		while(i.hasNext()){
			AccessRequest accessrequest = i.next();
			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				em.persist(accessrequest);
				entityTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//em.close();
			}
		}
	}
	
	/**
     * Get AccessRequest list by date and threat 
     * @param modification
     * @param threat_id
     * @return List<AccessRequest>
     */
	public  List<AccessRequest> findAccessrequestbyTimestampandThreat(Date modification,Threat threatid) {
  		
		List<AccessRequest> accessrequests = em.createNamedQuery("AccessRequest.findAccessrequestbyTimestampandThreat",AccessRequest.class)
				.setParameter("modification", modification)
				.setParameter("threatid", threatid)
				.getResultList();
		return accessrequests;		
	}
	
	/**
     * Anonymize AccessRequest list  
     * @param accessRequests
     */
	public void anonymizeAccessRequests(List<AccessRequest> accessRequests) {
		
		Iterator<AccessRequest> i = accessRequests.iterator();
		while(i.hasNext()){
			AccessRequest accessrequest = i.next();

			try {
				EntityTransaction entityTransaction = em.getTransaction();
				entityTransaction.begin();
				if(findAccessrequestbyTimestampandThreat(accessrequest.getModification(), accessrequest.getThreat()).size()>0){
					List<AccessRequest> listaccessrequest = findAccessrequestbyTimestampandThreat(accessrequest.getModification(),accessrequest.getThreat());
					listaccessrequest.get(0).setSolved((short) 1);
					List<Threat> threats = findThreatById(accessrequest.getThreat());
					String description = threats.get(0).getDescription();
					String text = description.replace(accessrequest.getUser().getName(), "");
					threats.get(0).setDescription(text);
					accessrequest.setUser(null);
					//listaccessrequest.get(0).merge();
					em.merge(threats.get(0));
					em.merge(accessrequest);

					}else{
						em.persist(accessrequest);

						//access.persist();
					}	
				
			entityTransaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//em.close();
			}
			
		}
		
	}
	
	
	
	
					/**----------------------------------------------------------------**/
				    
									/*** END RT2AE DB METHODS***/
				
				/**----------------------------------------------------------------**/


}
