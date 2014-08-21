package eu.musesproject.server.db.shield;

import java.sql.Time;
import java.util.Date;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Application;
import eu.musesproject.server.entity.Asset;
import eu.musesproject.server.entity.Device;
import eu.musesproject.server.entity.DeviceType;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.SimpleEvent;
import eu.musesproject.server.entity.User;
import eu.musesproject.server.scheduler.ModuleType;

public class TestClass {
	
	private DBManager db;
	
	public void test() {
		db = new DBManager(ModuleType.KRS);
		db.getUserByUsername("joe");
		db.getDeviceByIMEI("545");
		db.getRoleByName("role");	
		db.getDomainByName("domain");
		db.getAssetByLocation("Sweden");
		db.getUserAuthByUserId(200);
		db.getEventTypeByKey("key");
		db.getSecurityRulesByStatus("VALIDATED");
		db.getDecisionByAccessRequestId(80);
		db.getRefinedSecurityRulesByStatus("VALIDATED");
	}
	public void saveData(){
		db = new DBManager(ModuleType.KRS);
		SimpleEvent event = new SimpleEvent();
		EventType eventType = new EventType();
		eventType.setEventTypeId(100);
		eventType.setEventTypeKey("some-key");
		eventType.setEventLevel("some-level");
		event.setEventType(eventType);
		User user = new User();
		user.setUserId(112);
		user.setName("John");
		user.setSurname("Doe");
		user.setUsername("johndoe");
		user.setPassword("password");
		user.setEnabled(new byte[]{1});
		user.setRoleId(100);
		event.setUser(user);
		Device device = new Device();
		device.setDeviceId(120);
		device.setName("device-1");
		DeviceType deviceType = new DeviceType();
		deviceType.setDeviceTypeId(100);
		deviceType.setType("android");;
		device.setDeviceType(deviceType);
		event.setDevice(device);
		event.setDate(new Date());
		//event.getAccessRequests().add(new AccessRequest());
		//event.getAdditionalProtections().add(new AdditionalProtection());
		Application application = new Application();
		application.setAppId(101);
		application.setName("muses");
		application.setIs_MUSES_aware(new byte[]{1});
		event.setApplication(application);
		Asset asset = new Asset();
		asset.setAssetId(18);
		asset.setTitle("title");
		asset.setValue(2000);
		asset.setConfidentialLevel("HIGH");
		asset.setLocation("Sweden");
		event.setAsset(asset);
		//event.getRiskInformations().add(new RiskInformation());
		event.setData("some data");
		event.setDuration(1000);
		event.setTime(new Time(System.currentTimeMillis()));
		event.setEP_can_access(new byte[]{1});
		event.setKRS_can_access(new byte[]{1});
		event.setRT2AE_can_access(new byte[]{1});
		db.insert(event);
	}
	
	public void getData() {
		db = new DBManager(ModuleType.KRS);
		
	}
	
}
