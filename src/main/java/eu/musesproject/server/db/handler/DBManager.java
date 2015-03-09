package eu.musesproject.server.db.handler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
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
import eu.musesproject.server.entity.DeviceType;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.Domains;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.MusesConfig;
import eu.musesproject.server.entity.Outcome;
import eu.musesproject.server.entity.RefinedSecurityRules;
import eu.musesproject.server.entity.RiskCommunication;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.RiskTreatment;
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
	
	public void closeSessionFactory(){
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
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
		} finally {
			closeSessionFactory();
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
		} finally {
			closeSessionFactory();
		}
	}

	
	public List<SimpleEvents> getEvent(){
		List<SimpleEvents> allowedEvents = new ArrayList<SimpleEvents>();
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("SimpleEvents.findAll");
			List<SimpleEvents> simpleEvents = query.list();
			for (SimpleEvents event : simpleEvents) {
				if (event.getKRS_can_access() == 1){
					allowedEvents.add(event);
				}
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return allowedEvents;
	}
	
	
	
	// Complex DB method provided by Partners
	
	/**
	 * Get user by username
	 * @param username
	 * @return User
	 */
	
	public Users getUserByUsername(String username) {
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Users.findByUsername").setString("username", username);;
			List<Users> userList = query.list();
			
			for (Users u: userList){
				if (u.getUsername().equals(username)) {
					return u;
				}
			}
			session.close();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return null;
	}
	
	
	/**
	 * Get device object by IMEI number
	 * @param imei
	 * @return Device
	 */
	
	public Devices getDeviceByIMEI(String imei){
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Devices.findByIMEI").setString("imei", imei);
			List<Devices> deviceList = query.list();
			
			for (Devices d: deviceList) {
				if (d.getImei().equals(imei)){
					return d;
				}
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return null;
	}
	
	
	/**
	 * Get role object by name
	 * @param name
	 * @return Role
	 */
	
	public Roles getRoleByName(String name){
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Roles.findByName").setString("name", name);
			List<Roles> roleList = query.list();		
			
			for (Roles r: roleList) {
				if (r.getName().equals(name)){
					return r;
				}
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return null;
	}
	
	/**
	 * Get domain object by name 
	 * @param name
	 * @return Domain
	 */
	
	public Domains getDomainByName(String name){
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Domains.findByName").setString("name", name);
			List<Domains> domainList = query.list();
			
			for (Domains d: domainList) {
				if (d.getName().equals(name)){
					return d;
				}
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return null;
	}
	
	/**
	 * Get asset object by location
	 * @param location
	 * @return Asset
	 */
	
	public Assets getAssetByLocation(String location) {
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Assets.findByLocation").setString("location", location);
			List<Assets> assetList = query.list();
			
			for (Assets a: assetList) {
				if (a.getLocation().equals(location)){
					return a;
				}
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return null;
	}
	
	/**
	 * Get UserAuthorization object by userId of User object
	 * @param userId
	 * @return UserAuthorization
	 */
	
	public UserAuthorization getUserAuthByUserId(BigInteger userId) {
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("UserAuthorization.findByUserId").setBigInteger("user_id", userId);
			List<UserAuthorization> userAuthorizationsList = query.list();
			
			for (UserAuthorization u: userAuthorizationsList) {
				if (u.getUserId() == userId){
					return u;
				}
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return null;
	}

    
    /**
     * Get EventType object by key 
     * @param key
     * @return EventType
     */
    
    public List<EventType> getEventTypeListByKey(String key) {
    	List<EventType> eventTypeList = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("EventType.findByKey").setString("event_type_key", key);
			eventTypeList = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return eventTypeList;
    }
    
    /**
     * Get EventType object by key 
     * @param key
     * @return EventType
     */
    
    public EventType getEventTypeByKey(String key) {
    	EventType eventType = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("EventType.findByKey").setString("eventTypeKey", key);
			eventType = (EventType) query.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return eventType;
    }
    
   
    /**
     * Get SecurityRules list by status 
     * @param status
     * @return List<SecurityRule>
     */
    
    public List<SecurityRules> getSecurityRulesByStatus(String status) {
    	List<SecurityRules> securityRuleList = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("SecurityRule.findByStatus").setString("status", status);
			securityRuleList = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return securityRuleList;
    }

    /**
     * Get Decision by access request Id of AccessRequest object  
     * @param accessRequestId
     * @return List<Decision>
     */

    public List<Decision> getDecisionByAccessRequestId(String accessRequestId) {
    	AccessRequest accessRequest = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("AccessRequest.findById").setString("access_request_id", accessRequestId);
			accessRequest = (AccessRequest) query.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		}  finally {
			closeSessionFactory();
		}
    	return accessRequest.getDecisions();
    }
    
    /**
     * Get RefinedSecurityRule list by status
     * @param status
     * @return List<RefinedSecurityRule>
     */
    
    public List<RefinedSecurityRules> getRefinedSecurityRulesByStatus(String status) {
    	List<RefinedSecurityRules> foundList = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("RefinedSecurityRules.findByStatus").setString("status", status);
			List<RefinedSecurityRules> refinedSecurityRuleList = query.list();
			
			foundList = new ArrayList<RefinedSecurityRules>();
			for (RefinedSecurityRules r: refinedSecurityRuleList){
				if (r.getStatus().equals(status)){
					foundList.add(r);
				}
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
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
		List<Users> users = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Users.findAll");
			users = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		}  finally {
			closeSessionFactory();
		}
		return users;
	}
    
	/**
     * Find User list by username 
     * @param username
     */
	public List<Users> findUserByUsername(String username) {
		List<Users> users = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Users.findByUsername").setString("username", username);
			users = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		}  finally {
			closeSessionFactory();
		}
		return users;
	}
	
	
	/**
     * Get Device list by id 
     * @param device_id
     * @return List<Device>
     */
	public List<Devices> findDeviceById(String deviceId) {
		List<Devices> devices = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Devices.findById").setString("device_id", deviceId);
			devices = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		}  finally {
			closeSessionFactory();
		}
		return devices;		
	}

	/**
     * Get Asset list
     * @return List<Asset>
     */
    
	public List<Assets> getAssets() {
		List<Assets> assets = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Assets.findAll");
			assets = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return assets;		
	}
    
    /**
     * Get Asset list by title
     * @param title
     * @return List<Asset>
     */
    public List<Assets> findAssetByTitle(String title) {
    	List<Assets> assets = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Assets.findByTitle").setString("title", title);
			assets = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		}  finally {
			closeSessionFactory();
		}
		return assets;		
	}
    
    /**
     * Delete Asset by description 
     * @param title
     */
	
    public void deleteAssetByTitle(String title) {
    	try {
			Session session=getSessionFactory().openSession();
			session.getNamedQuery("Assets.deleteAssetByTitle").setString("title", title);
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
	}
      
	/**
     * Get Clue list
     * @return List<Clue>
     */
	
    public List<Clue> getClues() {
    	List<Clue> clues = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Clue.findAll");
			clues = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return clues;		
	}

	
	/**
     * Get Clue list by value
     * @param value
     * @return List<Clue>
     */
    public List<Clue> findClueByValue(String value) {
    	List<Clue> clues = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Clue.findByValue").setString("value", value);
			clues = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return clues;		
	}
    
    /**
     * Delete Clue by value 
     * @param value
     */
	public void deleteClueByValue(String value) {
		try {
			Session session=getSessionFactory().openSession();
			session.getNamedQuery("Clue.deleteClueByValue").setString("value", value);
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
	}
	
	/**
     * Get Threat list
     * @return List<Threat>
     */
	public List<Threat> getThreats() {
		List<Threat> threats = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Threat.findAll");
			threats = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return threats;		
	}
	
	 /**
     * Get Threat list by description
     * @param description
     * @return List<Threat>
     */
	public List<Threat> findThreatbydescription(String description) {
		List<Threat> threats = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Threat.findThreatbyDescription").setString("description", description);
			threats = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return threats;		
	}
	
	/**
     * Get Threat list by id
     * @param id
     * @return List<Threat>
     */
	public List<Threat> findThreatById(Threat threatId) {
		List<Threat> threats = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Threat.findThreatById").setString("threat_id", threatId.getThreatId());
			threats = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
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
				Outcome outcome = o.next();
				try {
					Session session=getSessionFactory().openSession();
					Transaction trans=session.beginTransaction();
					List<Threat> t = this.findThreatbydescription(threat.getDescription());
					if(t!=null){
						outcome.setThreat(t.get(0));
					}
				    session.save(outcome);
				    trans.commit();
				    session.close();
				} catch (Exception e) {
					logger.log(Level.ERROR, e.getMessage());
				} finally {
					closeSessionFactory();
				}
			}
		}
	}

	
	
	
	/**
     * Get Decision list
     * @return List<Decision>
     */
    
	public List<Decision> getDecisions() {
		List<Decision> decisions = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Decision.findAll");
			decisions = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return decisions;		
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
			} finally {
				closeSessionFactory();
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
			}  finally {
				closeSessionFactory();
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
			}  finally {
				closeSessionFactory();
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
			}  finally {
				closeSessionFactory();
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
			}  finally {
				closeSessionFactory();
			}
		}
	}
	
	
	
	/**
     * Get AccessRequest list by id
     * @param id
     * @return List<AccessRequest>
     */
	public List<AccessRequest> findAccessRequestById(String accessRequestId) {
		List<AccessRequest> accessrequests = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("AccessRequest.findById").setString("access_request_id", accessRequestId);
			accessrequests = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return accessrequests;		
	}
	
	/**
     * Get RiskCommunication list by id
     * @param id
     * @return List<RiskCommunication>
     */
	public List<RiskCommunication> findRiskCommunicationById(int riskCommunicationId) {
		List<RiskCommunication> riskcommunications = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("RiskCommunication.findRiskCommunicationById").setInteger("risk_communication_id", riskCommunicationId);
			riskcommunications = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return riskcommunications;		
	}
	
	
	 /**
     * Delete Threat by description 
     * @param descritpion
     */
	public void deletefThreatByDescription(String description) {
		try {
			Session session=getSessionFactory().openSession();
			session.getNamedQuery("Threat.deleteContentOfThreatTable"); // FIXME not implemented
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
	}
	
	/**
     * Get RiskPolicy list
     * @return List<RiskPolicy>
     */
	public List<RiskPolicy> getRiskPolicies() {
		List<RiskPolicy> riskpolicy = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("RiskPolicy.findAll");
			riskpolicy = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return riskpolicy;				
	}

	/**
     * Get Outcome list
     * @return List<Outcome>Outcomes
     */
	public List<Outcome> getOutcomes() {
		List<Outcome> outcome = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Outcome.findAll");
			outcome = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return outcome;				
	}


	
	
	/**
     * Get AccessRequest list
     * @return List<AccessRequest>
     */
	public List<AccessRequest> getAccessRequests() {
		List<AccessRequest> accesrequests = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("AccessRequest.findAll");
			accesrequests = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return accesrequests;
	}
	
	
	/**
     * Save RiskCommunication list in the DB 
     * @param List<RiskCommunication> users
     */
	public void setRiskCommunications(RiskCommunication riskCommunication) {
		try {
			Session session=getSessionFactory().openSession();
			Transaction trans=session.beginTransaction();
			session.save(riskCommunication);
			trans.commit();
			session.close();				
		} catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage());
		}  finally {
			closeSessionFactory();
		}
		
	}
	
	/**
     * Save RiskTreatment list in the DB 
     * @param List<RiskTreatment> users
     */
	public void setRiskTreatments(List<RiskTreatment> riskTreatments) {
		
		Iterator<RiskTreatment> i = riskTreatments.iterator();
		while(i.hasNext()){
		    
			try {
				RiskTreatment riskTreatment = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(riskTreatment);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			}  finally {
				closeSessionFactory();
			}
		}
	}
	
	

	/**
     * Save Decision list in the DB 
     * @param List<Decision> decisions
     */
	public void setDecisions(List<Decision> decisions) {
		
		Iterator<Decision> i = decisions.iterator();
		while(i.hasNext()){
			try {
				Decision decision = i.next();
				Session session=getSessionFactory().openSession();
				Transaction trans=session.beginTransaction();
				session.save(decision);
				trans.commit();
				session.close();				
			} catch (Exception e) {
				logger.log(Level.ERROR, e.getMessage());
			}  finally {
				closeSessionFactory();
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
		List<AccessRequest> accessrequests = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("AccessRequest.findAccessrequestbyTimestampandThreat"); // FIXME not implemented
			accessrequests = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
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
			}  finally {
				closeSessionFactory();
			}
		}
		
	}

	public Applications getApplicationByName(String name) {
		Applications app = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Applications.findAppByName").setString("name", name);
			app = (Applications) query.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return app;
	}

	public Sources getSourceByName(String name) {
		Sources source = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Sources.findByName").setString("name", name);
			source = (Sources) query.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return source;
	}
	
    /**
     * Get MUSES Config
     * @return MusesConfig
     */
	public MusesConfig getMusesConfig() {
		MusesConfig musesConfig = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("MusesConfig.findAll");
			
			List<MusesConfig> configList = query.list();
			for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
				musesConfig = (MusesConfig) iterator.next();
				
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return musesConfig;
	}
	
    /**
     * Get Sensor Configuration
     * @return List<SensorConfiguration>
     */
	public List<SensorConfiguration> getSensorConfiguration() {	
		List<SensorConfiguration> sensorConfigList = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("SensorConfiguration.findAll");
			sensorConfigList = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return sensorConfigList;
	}
	
    /**
     * Get Connection Config
     * @return ConnectionConfig
     */
	public ConnectionConfig getConnectionConfig() {	
		ConnectionConfig connConfig = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("ConnectionConfig.findAll");
			List<ConnectionConfig> configList = query.list();
			for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
				connConfig = (ConnectionConfig) iterator.next();
				
			}
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return connConfig;

	}

	public void setSecurityViolation(SecurityViolation securityViolation) {
		try {
			Session session=getSessionFactory().openSession();
			Transaction trans=session.beginTransaction();
			session.save(securityViolation);
			trans.commit();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}				
	}

	public void setDevice(Devices device) {
		try {
			Session session=getSessionFactory().openSession();
			Transaction trans=session.beginTransaction();
			session.save(device);
			trans.commit();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}				
	}
	
    /**
     * Get Device list 
     * @return List<Devices>
     */
	public List<Devices> getDevices() {	
		List<Devices> devices = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("Devices.findAll");
			devices = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return devices;
	}
	
    /**
     * Get Device type list 
     * @return List<DeviceType>
     */
	public List<DeviceType> getDeviceTypes() {	
		List<DeviceType> types = null;
		try {
			Session session=getSessionFactory().openSession();
			Query query = session.getNamedQuery("DeviceType.findAll");
			types = query.list();
			session.close();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSessionFactory();
		}
		return types;
	}

}
	
	