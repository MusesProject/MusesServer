package eu.musesproject.server.db.handler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Applications;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.Clue;
import eu.musesproject.server.entity.ConnectionConfig;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.Domains;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.MusesConfig;
import eu.musesproject.server.entity.Outcome;
import eu.musesproject.server.entity.RefinedSecurityRules;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.Roles;
import eu.musesproject.server.entity.SecurityRules;
import eu.musesproject.server.entity.SecurityViolation;
import eu.musesproject.server.entity.SensorConfiguration;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.Sources;
import eu.musesproject.server.entity.Threat;
import eu.musesproject.server.entity.UserAuthorization;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.scheduler.ModuleType;

public class DBManager {
	
	ModuleType module;
	private static SessionFactory sessionFactory = null;
	private static ServiceRegistry serviceRegistry;
	private static final String MUSES_TAG = "MUSES_TAG";
	private static Logger logger = Logger.getLogger(DBManager.class.getName());;
	public DBManager(ModuleType module) {
		this.module = module;
	}

	private SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			Configuration configuration = new Configuration();
			configuration.configure();
			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
					configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			
		}
		return sessionFactory;
	}
	
	
	
	public void persist(Object transientInstance) {
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
			Session session=getSessionFactory().openSession();
		    Transaction trans=session.beginTransaction();
		    session.save(transientInstance);
		    trans.commit();
		    session.close();
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	public void inform(SimpleEvents event) {
		try {
			Session session=getSessionFactory().openSession();
		    Transaction trans=session.beginTransaction();
			if (module.equals(ModuleType.KRS)){
				event.setKRS_can_access(0);
			}
			if (module.equals(ModuleType.EP)){
				event.setKRS_can_access(0);
			}

			if (module.equals(ModuleType.RT2AE)){
				event.setKRS_can_access(0);
			}
			session.save(event);
			trans.commit();
			session.close();
		} catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage());
		} 
	}

	
	public List<SimpleEvents> getEvent(){
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("SimpleEvents.findAll");
	    List<SimpleEvents> simpleEvents = query.list();
		List<SimpleEvents> allowedEvents = new ArrayList<SimpleEvents>();
		for (SimpleEvents event : simpleEvents) {
			if (event.getKRS_can_access() == 1){
				allowedEvents.add(event);
			}
		}
		session.close();
		return allowedEvents;
	}
	
	
	
	// Complex DB method provided by Partners
	
	/**
	 * Get user by username
	 * @param username
	 * @return User
	 */
	
	public Users getUserByUsername(String username) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Users.findByUsername").setString("username", username);;
	    List<Users> userList = query.list();
	    
		for (Users u: userList){
			if (u.getUsername().equals(username)) {
				return u;
			}
		}
		session.close();
		return null;
	}
	
	
	/**
	 * Get device object by IMEI number
	 * @param imei
	 * @return Device
	 */
	
	public Devices getDeviceByIMEI(String imei){
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Devices.findByIMEI").setString("imei", imei);
	    List<Devices> deviceList = query.list();
	    
		for (Devices d: deviceList) {
			if (d.getImei().equals(imei)){
				return d;
			}
		}
		session.close();
		return null;
	}
	
	
	/**
	 * Get role object by name
	 * @param name
	 * @return Role
	 */
	
	public Roles getRoleByName(String name){
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Roles.findByName").setString("name", name);
	    List<Roles> roleList = query.list();		
	    
		for (Roles r: roleList) {
			if (r.getName().equals(name)){
				return r;
			}
		}
		session.close();
		return null;
	}
	
	/**
	 * Get domain object by name 
	 * @param name
	 * @return Domain
	 */
	
	public Domains getDomainByName(String name){
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Domains.findByName").setString("name", name);
	    List<Domains> domainList = query.list();
	    
		for (Domains d: domainList) {
			if (d.getName().equals(name)){
				return d;
			}
		}
		session.close();
		return null;
	}
	
	/**
	 * Get asset object by location
	 * @param location
	 * @return Asset
	 */
	
	public Assets getAssetByLocation(String location) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Assets.findByLocation").setString("location", location);
	    List<Assets> assetList = query.list();
	    
		for (Assets a: assetList) {
			if (a.getLocation().equals(location)){
				return a;
			}
		}
		session.close();
		return null;
	}
	
	/**
	 * Get UserAuthorization object by userId of User object
	 * @param userId
	 * @return UserAuthorization
	 */
	
	public UserAuthorization getUserAuthByUserId(BigInteger userId) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("UserAuthorization.findByUserId").setBigInteger("user_id", userId);
	    List<UserAuthorization> userAuthorizationsList = query.list();
	    
		for (UserAuthorization u: userAuthorizationsList) {
			if (u.getUserId() == userId){
				return u;
			}
		}
		session.close();
		return null;
	}

    
    /**
     * Get EventType object by key 
     * @param key
     * @return EventType
     */
    
    public List<EventType> getEventTypeListByKey(String key) {
    	Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("EventType.findByKey").setString("event_type_key", key);
	    List<EventType> eventTypeList = query.list();
	    session.close();
		return eventTypeList;
    }
    
    /**
     * Get EventType object by key 
     * @param key
     * @return EventType
     */
    
    public EventType getEventTypeByKey(String key) {
    	Session session=getSessionFactory().openSession();
    	Query query = session.getNamedQuery("EventType.findByKey").setString("eventTypeKey", key);
	    EventType eventType = (EventType) query.uniqueResult();
	    session.close();
		return eventType;
    }
    
   
    /**
     * Get SecurityRules list by status 
     * @param status
     * @return List<SecurityRule>
     */
    
    public List<SecurityRules> getSecurityRulesByStatus(String status) {
    	Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("SecurityRule.findByStatus").setString("status", status);
	    List<SecurityRules> securityRuleList = query.list();
	    session.close();
		return securityRuleList;
    }

    /**
     * Get Decision by access request Id of AccessRequest object  
     * @param accessRequestId
     * @return List<Decision>
     */

    public List<Decision> getDecisionByAccessRequestId(String accessRequestId) {
    	Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("AccessRequest.findById").setString("access_request_id", accessRequestId);
	    
	    AccessRequest accessRequest = (AccessRequest) query.uniqueResult();
	    session.close();
    	return accessRequest.getDecisions();
    }
    
    /**
     * Get RefinedSecurityRule list by status
     * @param status
     * @return List<RefinedSecurityRule>
     */
    
    public List<RefinedSecurityRules> getRefinedSecurityRulesByStatus(String status) {
    	Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("RefinedSecurityRules.findByStatus").setString("status", status);
	    List<RefinedSecurityRules> refinedSecurityRuleList = query.list();
	    
    	List<RefinedSecurityRules> foundList = new ArrayList<RefinedSecurityRules>();
		for (RefinedSecurityRules r: refinedSecurityRuleList){
			if (r.getStatus().equals(status)){
				foundList.add(r);
			}
		}
		session.close();
		return foundList;
    }

    /**----------------------------------------------------------------**/
    
    					/*** START RT2AE DB METHODS***/
    
     /**----------------------------------------------------------------**/
   
    
    
    /**
     * Get Users list 
     * @return List<User>
     */
	public List<Users> getUsers() {	
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Users.findAll");
		List<Users> users = query.list();
		session.close();
		return users;
	}
    
	/**
     * Find User list by username 
     * @param username
     */
	public List<Users> findUserByUsername(String username) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Users.findByUsername").setString("username", username);
	    List<Users> users = query.list();
	    session.close();
		return users;
	}
	
	
	/**
     * Get Device list by id 
     * @param device_id
     * @return List<Device>
     */
	public List<Devices> findDeviceById(String deviceId) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Devices.findById").setString("device_id", deviceId);
		List<Devices> devices = query.list();
		session.close();
		return devices;		
	}

	/**
     * Get Asset list
     * @return List<Asset>
     */
    
	public List<Assets> getAssets() {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Assets.findAll");
	    List<Assets> assets = query.list();
	    session.close();
		return assets;		
	}
    
    /**
     * Get Asset list by title
     * @param title
     * @return List<Asset>
     */
    public List<Assets> findAssetByTitle(String title) {
    	Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Assets.findByTitle").setString("title", title);
		List<Assets> assets = query.list();
		session.close();
		return assets;		
	}
    
    /**
     * Delete Asset by description 
     * @param title
     */
	
    public void deleteAssetByTitle(String title) {
    	Session session=getSessionFactory().openSession();
	    session.getNamedQuery("Assets.deleteAssetByTitle").setString("title", title);
	    session.close();
	}
      
	/**
     * Get Clue list
     * @return List<Clue>
     */
	
    public List<Clue> getClues() {
    	Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Clue.findAll");
		List<Clue> clues = query.list();
		session.close();
		return clues;		
	}

	
	/**
     * Get Clue list by value
     * @param value
     * @return List<Clue>
     */
    public List<Clue> findClueByValue(String value) {
    	Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Clue.findByValue").setString("value", value);
		List<Clue> clues = query.list();
		session.close();
		return clues;		
	}
    
    /**
     * Delete Clue by value 
     * @param value
     */
	public void deleteClueByValue(String value) {
		Session session=getSessionFactory().openSession();
	    session.getNamedQuery("Clue.deleteClueByValue").setString("value", value);
	    session.close();
	}
	
	/**
     * Get Threat list
     * @return List<Threat>
     */
	public List<Threat> getThreats() {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Threat.findAll");
		List<Threat> threats = query.list();
		session.close();
		return threats;		
	}
	
	 /**
     * Get Threat list by description
     * @param description
     * @return List<Threat>
     */
	public List<Threat> findThreatbydescription(String description) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Threat.findThreatbyDescription").setString("description", description);
		List<Threat> threats = query.list();
		session.close();
		return threats;		
	}
	
	/**
     * Get Threat list by id
     * @param id
     * @return List<Threat>
     */
	public List<Threat> findThreatById(Threat threatId) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Threat.findThreatById").setString("threat_id", threatId.getThreatId());
		List<Threat> threats = query.list();
		session.close();
		return threats;		
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
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				if (this.findThreatbydescription(threat.getDescription()).size()>0){
					List<Threat>	listtThreats = this.findThreatbydescription(threat.getDescription());
					listtThreats.get(0).setOccurences(threat.getOccurences());
					listtThreats.get(0).setProbability(threat.getProbability());
					listtThreats.get(0).setBadOutcomeCount(threat.getBadOutcomeCount());
					listtThreats.get(0).setDescription(threat.getDescription());
				    session.merge(listtThreats.get(0));
				    trans.commit();
				}else{
					threat1.setDescription(threat.getDescription());
					threat1.setProbability(threat.getProbability());
					threat1.setBadOutcomeCount(threat.getBadOutcomeCount());
					threat1.setOccurences(threat.getOccurences());
				    session.save(threat1);
				    trans.commit();
				    
				}
				session.close();
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			} 
			
			//this.setOutcomes(threat.getOutcomes());
			Iterator<Outcome> o = threat.getOutcomes().iterator();
			while(o.hasNext()){
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				Outcome outcome = o.next();
				try {
					List<Threat> t = this.findThreatbydescription(threat.getDescription());
					if(t!=null){
						outcome.setThreat(t.get(0));
					}
				    session.save(outcome);
				    trans.commit();
				    
				} catch (Exception e) {
					logger.log(Level.ERROR, e.getMessage());
				}
				session.close();
			}
		}
	}

	/**
     * Save Users list in the DB 
     * @param List<Users> users
     */
	public void setUsers(List<Users> users) {
		
		Iterator<Users> i = users.iterator();
		while(i.hasNext()){
			try {
				Users user = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(user);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			} 
		}
	}
	
	
	/**
     * Save Assets list in the DB 
     * @param List<Assets> users
     */
	public void setAssets(List<Assets> assets) {
		
		Iterator<Assets> i = assets.iterator();
		while(i.hasNext()){
			try {
				Assets asset = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(asset);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			} 
		}
	}
	
	/**
     * Save Assets list in the DB 
     * @param List<Assets> users
     */
	public void setClues(List<Clue> clues) {
		
		Iterator<Clue> i = clues.iterator();
		while(i.hasNext()){
		    
			try {
				Clue clue = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(clue);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			} 
		}
	}
	
	
	/**
     * Save RiskPolicy list in the DB 
     * @param List<RiskPolicy> users
     */
	public void setRiskPolicies(List<RiskPolicy> riskPolicies) {
		
		Iterator<RiskPolicy> i = riskPolicies.iterator();
		while(i.hasNext()){
			try {
				RiskPolicy riskPolicy = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(riskPolicy);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			} 
		}
	}
	
	
	/**
     * Save AccessRequest list in the DB 
     * @param List<AccessRequest> users
     */
	public void setAccessRequests(List<AccessRequest> accessRequests) {
		
		Iterator<AccessRequest> i = accessRequests.iterator();
		while(i.hasNext()){
			try {
				AccessRequest accessrequest = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(accessrequest);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			} 
		}
	}
	
	
	
	 /**
     * Delete Threat by description 
     * @param descritpion
     */
	public void deletefThreatByDescription(String description) {
		Session session=getSessionFactory().openSession();
	    session.getNamedQuery("Threat.deleteContentOfThreatTable"); // FIXME not implemented
	    session.close();
	}
	
	/**
     * Get RiskPolicy list
     * @return List<RiskPolicy>
     */
	public List<RiskPolicy> getRiskPolicies() {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("RiskPolicy.findAll");
		List<RiskPolicy> riskpolicy = query.list();
		session.close();
		return riskpolicy;				
	}

	/**
     * Get Outcome list
     * @return List<Outcome>Outcomes
     */
	public List<Outcome> getOutcomes() {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("Outcome.findAll");
		List<Outcome> outcome = query.list();
		session.close();
		return outcome;				
	}


	
	
	/**
     * Get AccessRequest list
     * @return List<AccessRequest>
     */
	public List<AccessRequest> getAccessRequests() {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("AccessRequest.findAll");
		List<AccessRequest> accesrequests = query.list();
		session.close();
		return accesrequests;
	}
	
	
	/**
     * Get AccessRequest list by date and threat 
     * @param modification
     * @param threat_id
     * @return List<AccessRequest>
     */
	public  List<AccessRequest> findAccessrequestbyTimestampandThreat(Date modification,Threat threatid) {
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("AccessRequest.findAccessrequestbyTimestampandThreat"); // FIXME not implemented
		List<AccessRequest> accessrequests = query.list();
		session.close();
		return accessrequests;		
	}
	
	/**
     * Anonymize AccessRequest list  
     * @param accessRequests
     */
	public void anonymizeAccessRequests(List<AccessRequest> accessRequests) {
//		Iterator<AccessRequest> i = accessRequests.iterator();
//		while(i.hasNext()){
//			AccessRequest accessrequest = i.next();
//
//			try {
//				Session session=getSessionFactory().openSession();
//				Transaction trans=session.beginTransaction();
//				if(findAccessrequestbyTimestampandThreat(accessrequest.getModification(), accessrequest.getThreat()).size()>0){
//					List<AccessRequest> listaccessrequest = findAccessrequestbyTimestampandThreat(accessrequest.getModification(),accessrequest.getThreat());
//					listaccessrequest.get(0).setSolved((short) 1);
//					List<Threat> threats = findThreatById(accessrequest.getThreat());
//					String description = threats.get(0).getDescription();
//					String text = description.replace(accessrequest.getUser().getName(), "");
//					threats.get(0).setDescription(text);
//					accessrequest.setUser(null);
//					//listaccessrequest.get(0).merge();
//					session.merge(threats.get(0));
//					session.merge(accessrequest);
//
//					}else{
//						session.save(accessrequest);
//
//						//access.persist();
//					}	
//				
//			trans.commit();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				//em.close();
//			}
//			
//		}
		
//	}
		
}

	
	
	
	
					/**----------------------------------------------------------------**/
				    
									/*** END RT2AE DB METHODS***/
				
				/**----------------------------------------------------------------**/
	
	
	

	public void setSimpleEvents(List<SimpleEvents> list) {
		Iterator<SimpleEvents> i = list.iterator();
		while(i.hasNext()){
			try {
				SimpleEvents event = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(event);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			} 
		}
		
	}

	public Applications getApplicationByName(String name) {
		Session session=getSessionFactory().openSession();
    	Query query = session.getNamedQuery("Applications.findAppByName").setString("name", name);
    	Applications app = (Applications) query.uniqueResult();
	    session.close();
		return app;
	}

	public Sources getSourceByName(String name) {
		Session session=getSessionFactory().openSession();
    	Query query = session.getNamedQuery("Sources.findByName").setString("name", name);
	    Sources source = (Sources) query.uniqueResult();
	    session.close();
		return source;
	}
	
    /**
     * Get MUSES Config
     * @return MusesConfig
     */
	public MusesConfig getMusesConfig() {
		MusesConfig musesConfig = null;
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("MusesConfig.findAll");
	    
		List<MusesConfig> configList = query.list();
		for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
			musesConfig = (MusesConfig) iterator.next();
			
		}
		session.close();
		return musesConfig;
	}
	
    /**
     * Get Sensor Configuration
     * @return List<SensorConfiguration>
     */
	public List<SensorConfiguration> getSensorConfiguration() {	
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("SensorConfiguration.findAll");
	    List<SensorConfiguration> sensorConfigList = query.list();
		session.close();
		return sensorConfigList;
	}
	
    /**
     * Get Connection Config
     * @return ConnectionConfig
     */
	public ConnectionConfig getConnectionConfig() {	
		ConnectionConfig connConfig = null;
		Session session=getSessionFactory().openSession();
	    Query query = session.getNamedQuery("ConnectionConfig.findAll");
		List<ConnectionConfig> configList = query.list();
		for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
			connConfig = (ConnectionConfig) iterator.next();
			
		}
		session.close();
		return connConfig;

	}

	public void setSecurityViolation(SecurityViolation securityViolation) {

		Session session=getSessionFactory().openSession();
		Transaction trans=session.beginTransaction();
		session.save(securityViolation);
		trans.commit();
		session.close();				
	}

}
	