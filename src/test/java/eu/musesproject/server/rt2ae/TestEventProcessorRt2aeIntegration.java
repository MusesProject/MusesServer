package eu.musesproject.server.rt2ae;

import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.contextdatareceiver.JSONManager;
import eu.musesproject.server.contextdatareceiver.UserContextEventDataReceiver;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.global.Rt2aeGlobal;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;

public class TestEventProcessorRt2aeIntegration extends TestCase{

	private final String defaultSessionId = "DFOOWE423423422H23H";
	private final String testFullCycleWithClues = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894993,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"online_decision\"}";
	
	private final String testSecurityDeviceStateStep1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894992,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Kaspersky Mobile Security, com.kaspersky.mobile.security;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"insensitive\"}},\"requesttype\":\"online_decision\"}";
	private final String testSecurityDeviceStateStep2 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894993,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"insensitive\"}},\"requesttype\":\"online_decision\"}";
	
	private final String testUserAction = "{\"behavior\":{\"action\":\"cancel\"},\"requesttype\":\"user_behavior\"}";
	
	public final void testFullCycleWithClues(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testFullCycleWithClues, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testSecurityDeviceStateChange(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list1 = JSONManager.processJSONMessage(testSecurityDeviceStateStep1, "online_decision");
		List<ContextEvent> list2 = JSONManager.processJSONMessage(testSecurityDeviceStateStep2, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testCluesDeviceStateSecurityIncident(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testFullCycleWithClues, "online_decision");
		List<ContextEvent> list1 = JSONManager.processJSONMessage(testSecurityDeviceStateStep1, "online_decision");
		List<ContextEvent> list2 = JSONManager.processJSONMessage(testSecurityDeviceStateStep2, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testSecurityIncident(){
		
		User user = new User();
		UserTrustValue value = new UserTrustValue();
		value.setValue(1);
		user.setUsertrustvalue(value);
		
		SecurityIncident securityIncident = new SecurityIncident();
		securityIncident.setAssetid(1);
		securityIncident.setCostBenefit(1); //Included in the UI
		securityIncident.setDecisionid(0);
		securityIncident.setDescription("");
		securityIncident.setProbability(0.5);
		securityIncident.setUser(user);
		Probability probability = new Probability();
		probability.setValue(0.5);
		
		Rt2aeGlobal.notifySecurityIncident(probability, securityIncident);
		assertNotNull(user.getUsertrustvalue());
		
	}
	
	
	public final void testUserAction(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUserAction, "user_behavior");
		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		
	}
}
