package eu.musesproject.server.db.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.AdditionalProtection;
import eu.musesproject.server.entity.Asset;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.Device;
import eu.musesproject.server.entity.Domain;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.RefinedSecurityRule;
import eu.musesproject.server.entity.Role;
import eu.musesproject.server.entity.SecurityIncident;
import eu.musesproject.server.entity.SecurityRule;
import eu.musesproject.server.entity.SimpleEvent;
import eu.musesproject.server.entity.ThreatClue;
import eu.musesproject.server.entity.User;
import eu.musesproject.server.entity.UserAuthorization;
import eu.musesproject.server.scheduler.ModuleType;

public class DBManager {
	
	private final EntityManagerFactory emf;
	private final EntityManager em;
	ModuleType module;
	
	public DBManager(ModuleType module) {
		this.module = module;
	    emf = Persistence.createEntityManagerFactory("server"); // FIXME change to muses
	    em = emf.createEntityManager();	
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
    
    public List<EventType> getEventTypeByKey(String key) {
		List<EventType> eventTypeList = em.createNamedQuery("EventType.findByKey",EventType.class)
				.setParameter("eventTypeKey", key)
				.setMaxResults(5)
				.getResultList(); 
		return eventTypeList;
    }
    
    
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
    
    public List<SecurityRule> getSecurityRulesByStatus(String status) {
    	List<SecurityRule> securityRuleList = em.createNamedQuery("SecurityRule.findByStatus",SecurityRule.class)
				.setParameter("status", status)
				.getResultList();
		return securityRuleList;
    }
    
    public List<Decision> getDecisionByAccessRequestId(int accessRequestId) {
    	AccessRequest accessRequest = em.createNamedQuery("AccessRequest.findById",AccessRequest.class)
    			.setParameter("accessRequestId", accessRequestId)
    			.getSingleResult();
    	return accessRequest.getDecisions();
    }
    
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
    
//    public List<BlackList> getFullBlacklist() {  // FIXME no black list table
//    	
//    }
	
}
