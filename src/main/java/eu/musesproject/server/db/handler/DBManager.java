package eu.musesproject.server.db.handler;

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
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Applications;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.Clue;
import eu.musesproject.server.entity.ConnectionConfig;
import eu.musesproject.server.entity.CorporatePolicies;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.DecisionTrustvalues;
import eu.musesproject.server.entity.DefaultPolicies;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.Domains;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.ListOfpossibleRisktreatment;
import eu.musesproject.server.entity.MusesConfig;
import eu.musesproject.server.entity.Outcome;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.RefinedSecurityRules;
import eu.musesproject.server.entity.RiskCommunication;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.RiskTreatment;
import eu.musesproject.server.entity.Roles;
import eu.musesproject.server.entity.SecurityIncident;
import eu.musesproject.server.entity.SecurityRules;
import eu.musesproject.server.entity.SecurityViolation;
import eu.musesproject.server.entity.SensorConfiguration;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.Sources;
import eu.musesproject.server.entity.SystemLogKrs;
import eu.musesproject.server.entity.Threat;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.entity.Zone;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.model.owl.SecurityIncidentEvent;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;
import eu.musesproject.server.scheduler.ModuleType;

public class DBManager {
	
	ModuleType module;
	public static SessionFactory sessionFactory = null;
	private static ServiceRegistry serviceRegistry;
	private static final String MUSES_TAG = "MUSES_TAG";
	private static Logger logger = Logger.getLogger(DBManager.class.getName());;
	public DBManager(ModuleType module) {
		this.module = module;
	}

	// the creation of singleton must be thread-safe
//	private SessionFactory getSessionFactory() {
//		if (sessionFactory == null) {
//			Configuration configuration = new Configuration();
//			configuration.configure();
//			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
//					configuration.getProperties()).build();
//			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//			
//		}
//		return sessionFactory;
//	}
	
	private SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			synchronized(DBManager.class) {
				if(sessionFactory == null) {
					Configuration configuration = new Configuration();
					configuration.configure();
					ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
							configuration.getProperties()).build();
					sessionFactory = configuration.buildSessionFactory(serviceRegistry);
				}
			}
		}
		return sessionFactory;
	}
	
	public void persist(Object transientInstance) {
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(transientInstance);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	public void inform(SimpleEvents event) {
		try {
			if (module.equals(ModuleType.KRS)){
				event.setKRS_can_access(0);
			}
			if (module.equals(ModuleType.EP)){
				event.setKRS_can_access(0);
			}

			if (module.equals(ModuleType.RT2AE)){
				event.setKRS_can_access(0);
			}
		    Session session = null;
		    Transaction trans = null;
		    try {
		    	session = getSessionFactory().openSession();
		    	trans = session.beginTransaction();
		    	session.save(event);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		} catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage());
		} 
	}
	
	public List<SimpleEvents> getEvent(){
		Query query = null;
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SimpleEvents.findAll");
			if (query != null) {
				List<SimpleEvents> simpleEvents = query.list();
				List<SimpleEvents> allowedEvents = new ArrayList<SimpleEvents>();
				for (SimpleEvents event : simpleEvents) {
					if (event.getKRS_can_access() == 1){
						allowedEvents.add(event);
					}
				}
				return allowedEvents;
			} 
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}
	
	
	
	// Complex DB method provided by Partners
	
	/**
	 * Get user by username
	 * @param username
	 * @return User
	 */
	
	public Users getUserByUsername(String username) {
		Query query = null;
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Users.findByUsername").setString("username", username);
			if (query != null) {
				List<Users> userList = query.list();
				for (Users u: userList){
					if (u.getUsername().equals(username)) {
						return u;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}
	
	
	/**
	 * Get device object by IMEI number
	 * @param imei
	 * @return Device
	 */
	
	public Devices getDeviceByIMEI(String imei){
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Devices.findByIMEI").setString("imei", imei);
			if (query != null) {
				List<Devices> deviceList = query.list();
				for (Devices d: deviceList) {
					if (d.getImei().equals(imei)){
						return d;
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}
	
	
	/**
	 * Get role object by name
	 * @param name
	 * @return Role
	 */
	
	public Roles getRoleByName(String name){
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Roles.findByName").setString("name", name);
			if (query != null) {
				List<Roles> roleList = query.list();		
				
				for (Roles r: roleList) {
					if (r.getName().equals(name)){
						return r;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}
	
	/**
	 * Get domain object by name 
	 * @param name
	 * @return Domain
	 */
	
	public Domains getDomainByName(String name){
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Domains.findByName").setString("name", name);
			if (query != null) {
				List<Domains> domainList = query.list();
				for (Domains d : domainList) {
					if (d.getName().equals(name)) {
						return d;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}
	
	/**
	 * Get asset object by location
	 * @param location
	 * @return Asset
	 */
	
	public Assets getAssetByLocation(String location) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Assets.findByLocation").setString("location", location);
			if (query!=null) {
				List<Assets> assetList = query.list();
				for (Assets a : assetList) {
					if (a.getLocation().equals(location)) {
						return a;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}
	
	    
    /**
     * Get EventType object by key 
     * @param key
     * @return EventType
     */
    
    public List<EventType> getEventTypeListByKey(String key) {
    	Session session = null;
		Query query = null;
		List<EventType> eventTypeList = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("EventType.findByKey").setString("event_type_key", key);
			if (query!=null) {
				eventTypeList = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return eventTypeList;
    }
    
    /**
     * Get EventType object by key 
     * @param key
     * @return EventType
     */
    
    public EventType getEventTypeByKey(String key) {
    	Session session = null;
		Query query = null;
		EventType eventType = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("EventType.findByKey").setString("eventTypeKey", key);
			if (query!=null) {
				eventType = (EventType) query.uniqueResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return eventType;
    }
    
   
    /**
     * Get SecurityRules list by status 
     * @param status
     * @return List<SecurityRule>
     */
    
    public List<SecurityRules> getSecurityRulesByStatus(String status) {
    	Session session = null;
		Query query = null;
		List<SecurityRules> securityRuleList = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SecurityRule.findByStatus").setString("status", status);
			if (query!=null) {
				securityRuleList = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return securityRuleList;
    }
    
    /**
     * Get RefinedSecurityRule list by status
     * @param status
     * @return List<RefinedSecurityRule>
     */
    
    public List<RefinedSecurityRules> getRefinedSecurityRulesByStatus(String status) {
    	Session session = null;
		Query query = null;
		List<RefinedSecurityRules> foundList = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("RefinedSecurityRules.findByStatus").setString("status", status);
			if (query!=null) {
				List<RefinedSecurityRules> refinedSecurityRuleList = query.list();
				foundList = new ArrayList<RefinedSecurityRules>();
				for (RefinedSecurityRules r : refinedSecurityRuleList) {
					if (r.getStatus().equals(status)) {
						foundList.add(r);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
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
		Session session = null;
		Query query = null;
		List<Users> users = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Users.findAll");
			if (query!=null) {
				users = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return users;
	}
    
	/**
     * Find User list by username 
     * @param username
     */
	public List<Users> findUserByUsername(String username) {
		Session session = null;
		Query query = null;
		List<Users> users = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Users.findByUsername").setString("username", username);
			if (query!=null) {
				users = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return users;
	}
	
	
	/**
     * Get Device list by id 
     * @param device_id
     * @return List<Device>
     */
	public List<Devices> findDeviceById(String deviceId) {
		Session session = null;
		Query query = null;
		List<Devices> devices = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Devices.findById").setString("device_id", deviceId);
			if (query!=null) {
				devices = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return devices;		
	}

	/**
     * Get Asset list
     * @return List<Asset>
     */
    
	public List<Assets> getAssets() {
		Session session = null;
		Query query = null;
		List<Assets> assets = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Assets.findAll");
			if (query!=null) {
				assets = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return assets;		
	}
    
    /**
     * Get Asset list by title
     * @param title
     * @return List<Asset>
     */
    public List<Assets> findAssetByTitle(String title) {
    	Session session = null;
		Query query = null;
		List<Assets> assets = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Assets.findByTitle").setString("title", title);
			if (query!=null) {
				assets = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return assets;		
	}
    
    /**
     * Delete Asset by description 
     * @param title
     */
	
    public void deleteAssetByTitle(String title) {
    	Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.getNamedQuery("Assets.deleteAssetByTitle").setString("title", title);
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
	}
      
	/**
     * Get Clue list
     * @return List<Clue>
     */
	
    public List<Clue> getClues() {
    	Session session = null;
		Query query = null;
		List<Clue> clues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Clue.findAll");
			if (query!=null) {
				clues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return clues;		
	}

	
	/**
     * Get Clue list by value
     * @param value
     * @return List<Clue>
     */
    public List<Clue> findClueByValue(String value) {
    	Session session = null;
		Query query = null;
		List<Clue> clues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Clue.findByValue").setString("value", value);
			if (query!=null) {
				clues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return clues;		
	}
    
    /**
     * Delete Clue by value 
     * @param value
     */
	public void deleteClueByValue(String value) {
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.getNamedQuery("Clue.deleteClueByValue").setString("value", value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
	}
	
	/**
     * Get Threat list
     * @return List<Threat>
     */
	public List<Threat> getThreats() {
		Session session = null;
		Query query = null;
		List<Threat> threats = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Threat.findAll");
			if (query!=null) {
				threats = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return threats;		
	}
	
	 /**
     * Get Threat list by description
     * @param description
     * @return List<Threat>
     */
	public List<Threat> findThreatbydescription(String description) {
		Session session = null;
		Query query = null;
		List<Threat> threats = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Threat.findThreatbyDescription").setString("description", description);
			if (query!=null) {
				threats = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return threats;		
	}
	
	/**
     * Get Threat list by id
     * @param id
     * @return List<Threat>
     */
	public List<Threat> findThreatById(String threatId) {
		Session session = null;
		Query query = null;
		List<Threat> threats = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Threat.findThreatById").setString("threat_id", threatId);
			if (query!=null) {
				threats = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return threats;		
	}
	
	
	
	/**
     * Get Decision list by id
     * @param id
     * @return List<Decision>
     */
	public List<Decision> findDecisionById(String decisionId) {
		Session session = null;
		Query query = null;
		List<Decision> decisions = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decision.findDecisionById").setString("decision_id", decisionId);
			if (query!=null) {
				decisions = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return decisions;		
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
			Session session = null;
			Transaction trans = null;
			try {
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				if (this.findThreatbydescription(threat.getDescription()).size()>0){
					List<Threat>	listtThreats = this.findThreatbydescription(threat.getDescription());
					listtThreats.get(0).setOccurences(threat.getOccurences());
					listtThreats.get(0).setProbability(threat.getProbability());
					listtThreats.get(0).setBadOutcomeCount(threat.getBadOutcomeCount());
					listtThreats.get(0).setDescription(threat.getDescription());
				    session.merge(listtThreats.get(0));
					session.flush();

				    trans.commit();
				}else{
					threat1.setDescription(threat.getDescription());
					threat1.setProbability(threat.getProbability());
					threat1.setBadOutcomeCount(threat.getBadOutcomeCount());
					threat1.setOccurences(threat.getOccurences());
				    session.save(threat1);
					session.flush();

				    trans.commit();
				    
				}
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
			
			//this.setOutcomes(threat.getOutcomes());
			Iterator<Outcome> o = threat.getOutcomes().iterator();
			while(o.hasNext()){
				Outcome outcome = o.next();
				try {
					session = getSessionFactory().openSession();
					trans = session.beginTransaction();
					List<Threat> t = this.findThreatbydescription(threat.getDescription());
					if(t!=null){
						outcome.setThreat(t.get(0));
					}
				    session.save(outcome);
				    trans.commit();
				    
				} catch (Exception e) {
					if (trans!=null) trans.rollback();
					logger.log(Level.ERROR, e.getMessage());
				} finally {
					if (session!=null) session.close();
				} 
			}
		}
	}

	
	
	
	/**
     * Save Threat  in the DB 
     * @param Threat
     */
	public String setThreat(Threat threat) {
		
			
			String threatId="";
			Session session = null;
			Transaction trans = null;
			try {
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				if (this.findThreatbydescription(threat.getDescription()).size()>0){
					List<Threat>	listtThreats = this.findThreatbydescription(threat.getDescription());
					listtThreats.get(0).setOccurences(threat.getOccurences()+1);
					listtThreats.get(0).setProbability(threat.getProbability());
					listtThreats.get(0).setBadOutcomeCount(threat.getBadOutcomeCount());
					listtThreats.get(0).setDescription(threat.getDescription());
					session.merge(listtThreats.get(0));
					//threatId = listtThreats.get(0).getThreatId();
					session.flush();
					
				    trans.commit();
				}else{
					/*threat1.setDescription(threat.getDescription());
					threat1.setProbability(threat.getProbability());
					threat1.setBadOutcomeCount(threat.getBadOutcomeCount());
					threat1.setOccurences(threat.getOccurences());*/
				    session.save(threat);
					session.flush();
				    trans.commit();

					threatId = threat.getThreatId();
					
					Iterator<Outcome> o = threat.getOutcomes().iterator();
					session = getSessionFactory().openSession();
					trans = session.beginTransaction();
					Outcome outcome = o.next();
					try {
						
							outcome.setThreat(threat);
						
					    session.save(outcome);
						session.flush();

					    trans.commit();
					    
					} catch (Exception e) {
						if (trans!=null) trans.rollback();
						logger.log(Level.ERROR, e.getMessage());
					} finally {
						if (session!=null) session.close();
					} 
				    //trans.commit();
				    
				}
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
			
			
			
			return threatId;
		}
	
	
	/**
     * Get Decision list
     * @return List<Decision>
     */
    
	public List<Decision> getDecisions() {
		Session session = null;
		Query query = null;
		List<Decision> decisions = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decision.findAll");
			if (query!=null) {
				decisions = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		} 
		return decisions;		
	}
	
	
	
	/**
     * Save Users list in the DB 
     * @param List<Users> users
     */
	public void setUsers(List<Users> users) {
		Iterator<Users> i = users.iterator();
		Session session = null;
		Transaction trans = null;
		while(i.hasNext()){
			try {
				Users user = i.next();
				if(findUserByUsername(user.getUsername()).size()== 0){

					session=getSessionFactory().openSession();
					trans=session.beginTransaction();
					session.save(user);
					trans.commit();
				}else{
					session=getSessionFactory().openSession();
					trans=session.beginTransaction();
					session.merge(user);
					trans.commit();
				}
				
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
	}
	
	
	
	/**
     * Save Device list in the DB 
     * @param List<Devices> devices
     */
	public void setDevices(List<Devices> devices) {
		Iterator<Devices> i = devices.iterator();
		Session session = null;
		Transaction trans = null;
		while(i.hasNext()){
			try {
				Devices device = i.next();
				if(findDecisionById(device.getDeviceId()).size()== 0){

					session=getSessionFactory().openSession();
					trans=session.beginTransaction();
					session.save(device);
					trans.commit();
				}else{
					session=getSessionFactory().openSession();
					trans=session.beginTransaction();
					session.merge(device);
					trans.commit();
				}
				
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
	}
	
	
	
	
	
	
	/**
     * Save trust values for each decision 
     * @param List<DecisionTrustvalues> decisiontrustvalues
     */
	public void setDecisionTrustvalues(List<DecisionTrustvalues> decisiontrustvalues) {
		Iterator<DecisionTrustvalues> i = decisiontrustvalues.iterator();
		Session session = null;
		Transaction trans = null;
		while(i.hasNext()){
			try {
				DecisionTrustvalues decisiontrustvalue = i.next();
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();
				session.save(decisiontrustvalue);
				session.flush();
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
	}
	
	
	
	/**
     * Get DecisionTrustvalues list
     * @return List<DecisionTrustvalues>
     */
    
	public List<DecisionTrustvalues> getDecisionTrusvalues() {
		Session session = null;
		Query query = null;
		List<DecisionTrustvalues> decisionTrustvalues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decision.findAll");
			if (query!=null) {
				decisionTrustvalues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		} 
		return decisionTrustvalues;		
	}
	
	/**
     * Save Decision object in the DB 
     * @param Decision decision
     */
	public String setDecision(Decision decision) {
		Session session = null;
		Transaction trans = null;
			try {
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();

				session.save(decision);
				session.flush();

				trans.commit();
				logger.log(Level.INFO, "Storing decision in the database");

			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
			
			return decision.getDecisionId();
		}
	
	
	
	
	
	
	
	
	
	/**
     * Save AccessRequest object in the DB 
     * @param AccessRequest accessrequest
     */
	public String setAccessRequest(AccessRequest accessrequest) {
		Session session = null;
		Transaction trans = null;
			try {
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();

				session.save(accessrequest);
				session.flush();

				trans.commit();
				logger.log(Level.INFO, "Storing AccessRequest in the database ");

			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
			
			return accessrequest.getAccessRequestId();
		}
	
	/**
     * Save Assets list in the DB 
     * @param List<Assets> users
     */
	public void setAssets(List<Assets> assets) {
		Session session = null;
		Transaction trans = null;
		Iterator<Assets> i = assets.iterator();
		while(i.hasNext()){
			try {
				Assets asset = i.next();
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();
				session.save(asset);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
	}
	
	/**
     * Save Assets list in the DB 
     * @param List<Assets> users
     */
	public String setAsset(Assets asset) {
		Session session = null;
		Transaction trans = null;
		try {
			session=getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.save(asset);
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			logger.log(Level.ERROR, e.getMessage());
		} finally {
			if (session!=null) session.close();
		}
		return asset.getAssetId();
	}
	
	/**
     * Save Assets list in the DB 
     * @param List<Assets> users
     */
	public void setClues(List<Clue> clues) {
		Session session = null;
		Transaction trans = null;
		Iterator<Clue> i = clues.iterator();
		while(i.hasNext()){
			try {
				Clue clue = i.next();
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();
				session.save(clue);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			}  
		}
	}
	
	
	/**
     * Save RiskPolicy list in the DB 
     * @param List<RiskPolicy> users
     */
	public void setRiskPolicies(List<RiskPolicy> riskPolicies) {
		Session session = null;
		Transaction trans = null;
		Iterator<RiskPolicy> i = riskPolicies.iterator();
		while(i.hasNext()){
			try {
				RiskPolicy riskPolicy = i.next();
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();
				session.save(riskPolicy);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			}   
		}
	}
	
	
	/**
     * Save AccessRequest list in the DB 
     * @param List<AccessRequest> users
     */
	public void setAccessRequests(List<AccessRequest> accessRequests) {
		Session session = null;
		Transaction trans = null;
		Iterator<AccessRequest> i = accessRequests.iterator();
		while(i.hasNext()){
			try {
				AccessRequest accessrequest = i.next();
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();
				session.save(accessrequest);
				session.flush();
				trans.commit();
				session.close();				
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
	}
	
	
	
	/**
     * Get AccessRequest list by id
     * @param id
     * @return List<AccessRequest>
     */
	public List<AccessRequest> findAccessRequestById(String accessRequestId) {
		Session session = null;
		Query query = null;
		List<AccessRequest> accessrequests = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("AccessRequest.findById").setString("access_request_id", accessRequestId);
			if (query!=null) {
				accessrequests = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		} 
		return accessrequests;		
	}
	
	/**
     * Get RiskCommunication list by id
     * @param id
     * @return List<RiskCommunication>
     */
	public List<RiskCommunication> findRiskCommunicationById(int riskCommunicationId) {
		Session session = null;
		Query query = null;
		List<RiskCommunication> riskcommunications = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("RiskCommunication.findRiskCommunicationById").setInteger("risk_communication_id", riskCommunicationId);
			if (query!=null) {
				riskcommunications = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return riskcommunications;		
	}
	
	
	 /**
     * Delete Threat by description 
     * @param descritpion
     */
	public void deletefThreatByDescription(String description) {
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.getNamedQuery("Threat.deleteContentOfThreatTable"); // FIXME not implemented
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
	}
	
	/**
     * Get RiskPolicy list
     * @return List<RiskPolicy>
     */
	public List<RiskPolicy> getRiskPolicies() {
		Session session = null;
		Query query = null;
		List<RiskPolicy> riskpolicy = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("RiskPolicy.findAll");
			if (query!=null) {
				riskpolicy = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return riskpolicy;				
	}

	/**
     * Get Outcome list
     * @return List<Outcome>Outcomes
     */
	public List<Outcome> getOutcomes() {
		Session session = null;
		Query query = null;
		List<Outcome> outcome = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Outcome.findAll");
			if (query!=null) {
				outcome = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return outcome;				
	}
	
	
	/**
     * Get all possible risktreatment 
     * @return List<String> risktreatments
     */
	public ListOfpossibleRisktreatment getRisktreatments(int id) {
		Session session = null;
		Query query = null;
		List<ListOfpossibleRisktreatment> list = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ListOfpossibleRisktreatment.findbyId").setInteger("risktreatment_id", id);
			if (query!=null) {
				list = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return list.get(list.size() - 1);			
	}


	
	
	/**
     * Get AccessRequest list
     * @return List<AccessRequest>
     */
	public List<AccessRequest> getAccessRequests() {
		Session session = null;
		Query query = null;
		List<AccessRequest> accesrequests = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("AccessRequest.findAll");
			if (query!=null) {
				accesrequests = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return accesrequests;
	}
	
	
	
	/**
     * Save UserBehaviour  
     * @param UserBehaviour userbehaviour
     */
	public void setUserBehaviour(eu.musesproject.server.entity.UserBehaviour userbehaviour) {
		Session session = null;
		Transaction trans = null;
		try {
			session=getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.save(userbehaviour);
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			logger.log(Level.ERROR, e.getMessage());
		} finally {
			if (session!=null) session.close();
		} 
		
	}
	
	
	
	
	/**
     * Save RiskCommunication list in the DB 
     * @param List<RiskCommunication> users
     */
	public void setRiskCommunications(RiskCommunication riskCommunication) {
		Session session = null;
		Transaction trans = null;
		try {
			session=getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.save(riskCommunication);
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			logger.log(Level.ERROR, e.getMessage());
		} finally {
			if (session!=null) session.close();
		} 
		
	}
	
	/**
     * Save RiskTreatment list in the DB 
     * @param List<RiskTreatment> users
     */
	public void setRiskTreatments(List<RiskTreatment> riskTreatments) {
		Session session = null;
		Transaction trans = null;
		Iterator<RiskTreatment> i = riskTreatments.iterator();
		while(i.hasNext()){
			try {
				RiskTreatment riskTreatment = i.next();
				session=getSessionFactory().openSession();
				trans=session.beginTransaction();
				session.save(riskTreatment);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
	}
	
	

	/**
     * Save Decision list in the DB 
     * @param List<Decision> decisions
     */
	public void setDecisions(List<Decision> decisions) {
		Session session = null;
		Transaction trans = null;
		Iterator<Decision> i = decisions.iterator();
		while(i.hasNext()){
			try {
				Decision decision = i.next();
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				session.save(decision);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
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
		Session session = null;
		Query query = null;
		List<AccessRequest> accessrequests = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("AccessRequest.findAccessrequestbyTimestampandThreat");
			if (query!=null) {
				accessrequests = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return accessrequests;		
	}
	
	public void convertUsertoCommonUser(User user, eu.musesproject.server.entity.Users musesUser){
		
		user.setUserId(musesUser.getUserId());
		user.setEmail(musesUser.getEmail());
		user.setPassword(musesUser.getPassword());
		user.setUsername(musesUser.getUsername());
		UserTrustValue usertrustvalue = new UserTrustValue();
		usertrustvalue.setValue(musesUser.getTrustValue());
		user.setUsertrustvalue(usertrustvalue);
		user.setEnabled(musesUser.getEnabled());
		user.setRoleId(musesUser.getRoleId());
	}
	
	public void convertDevicetoCommonDevice(Device device, eu.musesproject.server.entity.Devices musesDevice){
		
		device.setDeviceId(musesDevice.getDeviceId());
		device.setCertificate(musesDevice.getCertificate());
		DeviceTrustValue devicetrustvalue = new DeviceTrustValue();
		devicetrustvalue.setValue(musesDevice.getTrustValue());
		device.setDevicetrustvalue(devicetrustvalue);
		device.setName(musesDevice.getName());
		device.setImei(musesDevice.getImei());
		
		device.setOS_name(musesDevice.getOS_name());
		device.setOS_version(musesDevice.getOS_version());
		
		
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
		Session session = null;
		Transaction trans = null;
		Iterator<SimpleEvents> i = list.iterator();
		while(i.hasNext()){
			try {
				SimpleEvents event = i.next();
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				session.save(event);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
		
	}

	public Applications getApplicationByName(String name) {
		Session session = null;
		Query query = null;
		Applications app = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Applications.findAppByName").setString("name", name);
			if (query!=null) {
				app = (Applications) query.uniqueResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return app;
	}

	public Sources getSourceByName(String name) {
		Session session = null;
		Query query = null;
		Sources source = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Sources.findByName").setString("name", name);
			if (query!=null) {
				source = (Sources) query.uniqueResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return source;
	}
	
    /**
     * Get MUSES Config
     * @return MusesConfig
     */
	public MusesConfig getMusesConfig() {
		Session session = null;
		Query query = null;
		MusesConfig musesConfig = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("MusesConfig.findAll");
			if (query!=null) {
				List<MusesConfig> configList = query.list();
				for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
					musesConfig = (MusesConfig) iterator.next();
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return musesConfig;
	}
	
    /**
     * Get Sensor Configuration
     * @return List<SensorConfiguration>
     */
	public List<SensorConfiguration> getSensorConfiguration() {	
		Session session = null;
		Query query = null;
		List<SensorConfiguration> sensorConfigList = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SensorConfiguration.findAll");
			if (query!=null) {
				sensorConfigList = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return sensorConfigList;
	}
	
    /**
     * Get Connection Config
     * @return ConnectionConfig
     */
	public ConnectionConfig getConnectionConfig() {	
		Session session = null;
		Query query = null;
		ConnectionConfig connConfig = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ConnectionConfig.findAll");
			if (query!=null) {
				List<ConnectionConfig> configList = query.list();
				for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
					connConfig = (ConnectionConfig) iterator.next();
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return connConfig;

	}

	public void setSecurityViolation(SecurityViolation securityViolation) {
		Session session = null;
		Transaction trans = null;
		try {
			session = getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.save(securityViolation);
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
	}

	public void setDevice(Devices device) {
		Session session = null;
		Transaction trans = null;
		try {
			session = getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.save(device);
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}				
	}
	
    /**
     * Get Device list 
     * @return List<Devices>
     */
	public List<Devices> getDevices() {	
		Session session = null;
		Query query = null;
		List<Devices> devices = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Devices.findAll");
			if (query!=null) {
				devices = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return devices;
	}	
	
    /**
     * Get Zones list 
     * @return List<Zone>
     */
	public List<Zone> getZones() {	
		Session session = null;
		Query query = null;
		List<Zone> zones = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Zone.findAll");
			if (query!=null) {
				zones = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return zones;
	}
	
	public void setSecurityIncident(SecurityIncident securityIncident) {
		Session session = null;
		Transaction trans = null;
		try {
			session = getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.save(securityIncident);
			trans.commit();
			//Insert securityIncident event in the working memory
			insertSecurityIncidentEvent(securityIncident);
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
	}

	private void insertSecurityIncidentEvent(SecurityIncident securityIncident) {
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			des = EventProcessorImpl.getMusesEngineService();
		}
		SecurityIncidentEvent secIncidentEvent = new SecurityIncidentEvent();
		secIncidentEvent.setName(securityIncident.getName());
		secIncidentEvent.setDeviceId(securityIncident.getDevice().getImei());
		secIncidentEvent.setUserId(Integer.valueOf(securityIncident.getUser().getUserId()));
		secIncidentEvent.setTimestamp(new Date().getTime());
		logger.info("Inserting SECURITY INCIDENT...");
		des.insertFact(secIncidentEvent);
		
	}
	
	/**
     * Fills system_log_krs table in database
     * @param logs
     * @return void
     */
	public void setSystemLogKRS(List<SystemLogKrs> logs) {
		Session session = null;
		Transaction trans = null;
		Iterator<SystemLogKrs> i = logs.iterator();
		while(i.hasNext()){
			try {
				SystemLogKrs log = i.next();
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				session.save(log);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}
		
	}

	public List<DefaultPolicies> getDefaultPolicies(String language) {
		Session session = null;
		Query query = null;
		List<DefaultPolicies> policies = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("DefaultPolicies.findAll");
			if (query!=null) {
				policies = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return policies;
	}
	
	/**
     * Get AccessRequest list by event_id
     * @param accessRequestEventId
     * @return List<AccessRequest>
     */
	public List<AccessRequest> findAccessRequestByEventId(String accessRequestEventId) {
		Session session = null;
		Query query = null;
		List<AccessRequest> accessrequests = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("AccessRequest.findByEventId").setString("event_id", accessRequestEventId);
			if (query!=null) {
				accessrequests = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		} 
		return accessrequests;		
	}
	
	/**
     * Get SimpleEvents list by user_id, either the last events the user made or the next one (user behaviour)
     * @param simpleEventUserId
     * @param day
     * @param time
     * @param backwards
     * @return List<SimpleEvents>
     */
	public SimpleEvents findEventsByUserId(String simpleEventUserId, String day, String time, Boolean backwards) {
		Session session = null;
		Query query = null;
		SimpleEvents event = null;
		try {
			if (backwards) {
				session = getSessionFactory().openSession();
				query = session.getNamedQuery("SimpleEvents.findLastByUserId").
						setString("user_id", simpleEventUserId).
						setString("day", day).
						setString("time", time);
				query.setMaxResults(1);
			} else {
				session = getSessionFactory().openSession();
				query = session.getNamedQuery("SimpleEvents.findNextByUserId").
						setString("user_id", simpleEventUserId).
						setString("day", day).
						setString("time", time);
				query.setMaxResults(1);
			}
			if (query!=null) {
				List<SimpleEvents> events = query.list();
				if (events.size() > 0) {
					event = events.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		} 
		return event;		
	}
	
	/**
     * Get SecurityViolations list by event_id
     * @param securityViolationEventId
     * @return List<SecurityViolation>
     */
	public List<SecurityViolation> findSecurityViolationByEventId(String securityViolationEventId) {
		Session session = null;
		Query query = null;
		List<SecurityViolation> securityViolations = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SecurityViolation.findByEventId").setString("event_id", securityViolationEventId);
			if (query!=null) {
				securityViolations = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		} 
		return securityViolations;		
	}

	public SimpleEvents updateSimpleEvent(String eventType, String assetId) {
		Session session = null;
		Transaction trans = null;
		
		EventType type = getEventTypeByKey(eventType);
		
		SimpleEvents event = findLastEventByEventType(type.getEventTypeId());
		if (event != null) {
			Assets asset = findAssetById(assetId);
			event.setAsset(asset);
			try {
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				session.update(event);
				trans.commit();
			} catch (Exception e) {
				if (trans != null)
					trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session != null)
					session.close();
			}
		}
		return event;
		
	}
	
	public SimpleEvents findLastEventByEventType(int eventTypeId) {
		Session session = null;
		Query query = null;
		SimpleEvents event = null;
		List<SimpleEvents> list = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SimpleEvents.findLastEventByEventType").
						setInteger("event_type_id", eventTypeId);
			
			if (query!=null) {
				list = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		} 
		if (list.size()>0){
			event = list.get(list.size() - 1);
		}else{
			logger.info("Error: Query returned empty list");
		}
		return event;		
	}
	
    /**
     * Get Asset by id
     * @param id
     * @return Assets
     */
    public Assets findAssetById(String id) {
    	Session session = null;
		Query query = null;
		List<Assets> assets = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Assets.findById").setString("assetId", id);
			if (query!=null) {
				assets = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		if (assets.size()!=0){
			return assets.get(assets.size() - 1);
		}else{
			return getNoAsset();
		}
				
	}
    
	public Assets getNoAsset() {
		return getAssetByLocation("Nowhere");
	}
    
		
    
    /**
     * Obtains the patterns obtained by the KRS through the Data Mining process
     * @param void
     * @return patterns List of the patterns stored in patterns_krs
     */
    public List<PatternsKrs> getPatternsKRS() {
		Session session = null;
		Query query = null;
		List<PatternsKrs> patterns = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findAll");
			if (query!=null) {
				patterns = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return patterns;
	}
    
    /**
     * Fills system_log_krs table in database
     * @param logs
     * @return void
     */
	public void setPatternsKRS(List<PatternsKrs> patterns) {
		Session session = null;
		Transaction trans = null;
		Iterator<PatternsKrs> i = patterns.iterator();
		while(i.hasNext()){
			try {
				PatternsKrs log = i.next();
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				session.save(log);
				trans.commit();
			} catch (Exception e) {
				if (trans!=null) trans.rollback();
				logger.log(Level.ERROR, e.getMessage());
			} finally {
				if (session!=null) session.close();
			} 
		}		
	}
	
	/**
     * Get DecisionTrustvalues list by Decision id
     * @param decisionId
     * @return List<DecisionTrustvalues>
     */
	public List<DecisionTrustvalues> findDecisionTrustValuesByDecisionId(String decisionId) {
		Session session = null;
		Query query = null;
		List<DecisionTrustvalues> trustValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("DecisionTrustvalues.findByDecisionId").setString("decision_id", decisionId);
			if (query!=null) {
				trustValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return trustValues;		
	}
	
	/**
	 * Get role object by id
	 * @param roleId
	 * @return Role
	 */
	
	public Roles getRoleById(int roleId){
		Session session = null;
		Query query = null;
		Roles role = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Roles.findById").setInteger("role_id", roleId);
			if (query!=null) {
				List<Roles> roleList = query.list();
				if (roleList.size() > 0) {
					role = roleList.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		
		return role;
	}
	
	/**
     * Get all different values of decision_cause in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctDecisionCauses() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctDecisionCause");
			if (query!=null) {
				allDifferentValues = query.list();
				allDifferentValues.remove(allDifferentValues.indexOf(null));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of event_type in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctEventTypes() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctEventTypes");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of event_level in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctEventLevels() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctEventLevel");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of username in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctUsernames() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctUsername");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of user_role in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctUserRoles() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctUserRole");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of device_type in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctDeviceType() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctDeviceType");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of device_OS in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctDeviceOS() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctDeviceOS");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of device_owned_by in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctDeviceOwnedBy() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctDeviceOwnedBy");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of app_name in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctAppName() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctAppName");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of app_vendor in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctAppVendor() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctAppVendor");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of asset_name in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctAssetName() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctAssetName");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of asset_confidential_level in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctAssetConfidentialLevel() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctAssetConfidentialLevel");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of asset_location in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctAssetLocation() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctAssetLocation");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	/**
     * Get all different values of label in patterns_krs table
     * @param void
     * @return List<String>
     */
	public List<String> getDistinctLabels() {
		Session session = null;
		Query query = null;
		List<String> allDifferentValues = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("PatternsKrs.findDistinctLabels");
			if (query!=null) {
				allDifferentValues = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return allDifferentValues;		
	}
	
	public void removeAllCorporatePolicies(){
		Session session = null;
		Transaction trans = null;
		try {
			session = getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.createQuery("delete from CorporatePolicies").executeUpdate();
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}	
	}
	
	public void setCorporatePolicy(CorporatePolicies policy) {
		Session session = null;
		Transaction trans = null;
		try {
			session = getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.save(policy);
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}				
	}
	
	public void removeAllSecurityRules(){
		Session session = null;
		Transaction trans = null;
		try {
			session = getSessionFactory().openSession();
			trans=session.beginTransaction();
			session.createQuery("delete from SecurityRules").executeUpdate();
			trans.commit();
		} catch (Exception e) {
			if (trans!=null) trans.rollback();
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}	
	}
	
	public void setSecurityRule(SecurityRules rule) {
		Session session = null;
		Transaction trans = null;
		SecurityRules existing = getSecurityRuleByName(rule.getName());

		if (existing == null) {
			try {
				session = getSessionFactory().openSession();
				trans = session.beginTransaction();
				session.save(rule);
				trans.commit();
			} catch (Exception e) {
				if (trans != null)
					trans.rollback();
				e.printStackTrace();
			} finally {
				if (session != null)
					session.close();
			}
		}
	}
	
	public SecurityRules getSecurityRuleByName(String name) {
		Query query = null;
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SecurityRules.findByName").setString("name", name);
			if (query != null) {
				List<SecurityRules> list = query.list();
				for (SecurityRules s: list){
					if (s.getName().equals(name)) {
						return s;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}
	
	public static void main (String [] arg){
		
		DBManager dbManager = new DBManager(ModuleType.RT2AE);
		System.out.println("test: "+dbManager.getRisktreatments(1).getDescription());
	}

}
	
	