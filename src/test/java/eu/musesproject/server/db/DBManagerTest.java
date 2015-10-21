package eu.musesproject.server.db;

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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.contextdatareceiver.JSONManager;
import eu.musesproject.server.contextdatareceiver.UserContextEventDataReceiver;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.DecisionTrustvalues;
import eu.musesproject.server.entity.Outcome;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.RiskCommunication;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.RiskTreatment;
import eu.musesproject.server.entity.Roles;
import eu.musesproject.server.entity.SecurityViolation;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.SystemLogKrs;
import eu.musesproject.server.entity.Threat;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.eventprocessor.util.EventTypes;
import eu.musesproject.server.scheduler.ModuleType;

public class DBManagerTest {
	
	static DBManager dbmanager = null;

	
	@BeforeClass
	public  static void setUpBeforeClass() throws Exception {
		ModuleType module = null;
		dbmanager = new DBManager(module);

	}
	
	@AfterClass
	public  static void setUpAfterClass() throws Exception {
		ModuleType module = null;
		//dbmanager.close();

	}

	@Test
	public void testGetUsers() {
		List<Users> List = dbmanager.getUsers();
		if (List.size()>0){
			Iterator<Users> i = List.iterator();
			while(i.hasNext()){
				Users user = i.next();
				assertNotNull(user);
			}
		}else{
			fail("There is not any User in the database,please first try to store User in the database");
		}			
		
	}

	@Test
	public void testSetUsers() {
		List<Users> list = new ArrayList<Users>();
		Users user = new Users();
		user.setEmail("user@muse.eu");
		user.setName("Pinkman");
		user.setSurname("Jesse");
		SecureRandom random = new SecureRandom();

		user.setUsername(new BigInteger(30, random).toString(5));
		user.setEmail("jesse.pinkman@muses.eu");
		user.setPassword("walterwhite");
		user.setTrustValue(0.9999);
		user.setRoleId(0);
		user.setEnabled(0);
		list.add(user);
		dbmanager.setUsers(list);
		
		/*List<User> listusers = dbmanager.findUserByUsername(user.getUsername());

		if(listusers.size()>0)
			assertTrue(true);
		else
			fail("The User was not inserted in the database");*/
		
	}

	@Test
	public void testGetAssets() {
		List<Assets> List = dbmanager.getAssets();
		if (List.size()>0){
			Iterator<Assets> i = List.iterator();
			while(i.hasNext()){
				Assets asset = i.next();
				assertNotNull(asset);
			}
		}else{
			fail("There is not any Asset in the database,please first try to store Asset in the database");
		}			
		
	}

	@Test
	public void testFindAssetByTitle() {
		String title ="Patent";
		List<Assets> assets = dbmanager.findAssetByTitle(title);
		if(assets.size()>0)
			assertTrue(true);
		else
			fail("There is not any Asset in the database with this title,please first try with another title");


	}

	@Test
	public void testSetAssets() {
		List<Assets> list = new ArrayList<Assets>();
		Assets asset = new Assets();
		asset.setDescription("Asset_Unige");
		asset.setConfidentialLevel("PUBLIC");
		asset.setTitle("Patent");
		asset.setValue(0);
		asset.setLocation("unige/patent/list/UNIGEPatent");
		list.add(asset);
		dbmanager.setAssets(list);	
		
		/*List<Asset> listassets = dbmanager.findAssetByTitle(asset.getTitle());

		if(listassets.size()>0)
			assertTrue(true);
		else
			fail("The Asset was not inserted in the database");*/
		
	}

	
	@Test
	public void testGetDecisions() {
		List<Decision> List = dbmanager.getDecisions();
		if (List.size()>0 || List == null){
			Iterator<Decision> i = List.iterator();
			while(i.hasNext()){
				Decision decision = i.next();
				assertNotNull(decision);
			}
		}else{
			fail("There is not any Decision in the database,please first try to store Decision in the database");
		}			
		
	}
	
	@Test
	public void testSetDecisions() {
		List<Decision> list = new ArrayList<Decision>();
		Decision decision = new Decision();
		String opensensitivedocumentinunsecurenetwork = "You are trying to open a sensitive document, but you are connected with an unsecured WiFi.\n Other people can observe what you transmit. Switch to a secure WiFi first.";	

		
		decision.setAccessRequest(dbmanager.findAccessRequestById("80").get(0));
		
		ArrayList<RiskCommunication> riskcommunications = new ArrayList<RiskCommunication>();
		
		RiskCommunication riskcommunication = new RiskCommunication();

		riskcommunication.setDescription("JunitTest");
		
		riskcommunications.add(riskcommunication);
		dbmanager.setRiskCommunications(riskcommunication);
		
		//decision.setRiskCommunication(dbmanager.findRiskCommunicationById(900).get(0));
		
		decision.setRiskCommunication(riskcommunication);
		
		List<RiskTreatment> risktreatments = new ArrayList<RiskTreatment>();

		RiskTreatment risktreatment = new RiskTreatment();
		risktreatment.setDescription("Testing RiskTreatment with JunitTest"); 
		risktreatment.setRiskCommunication(riskcommunication);
		risktreatments.add(risktreatment);
		
		dbmanager.setRiskTreatments(risktreatments);

		//riskcommunication.setRiskTreatments(risktreatments);
		//decision.setRiskCommunication(riskcommunication);
		decision.setValue("MAYBE");
		//decision.setInformation("test");
		//decision.setSolvingRisktreatment(2);
		decision.setTime(new Time(new Date().getTime()));
		//decision.setAccessRequest(accessRequest);
		
		
		list.add(decision);
		dbmanager.setDecisions(list);	
		
		/*List<Asset> listassets = dbmanager.findAssetByTitle(asset.getTitle());

		if(listassets.size()>0)
			assertTrue(true);
		else
			fail("The Asset was not inserted in the database");*/
		
	}	
	
	// Removing this function because the clues are storing in threat table
	
	/*@Test
	public void testGetClues() {
		List<Clue> List = dbmanager.getClues();
		if (List.size()>0){
			Iterator<Clue> i = List.iterator();
			while(i.hasNext()){
				Clue clue = i.next();
				assertNotNull(clue);
			}
		}else{
			fail("There is not any Clue in the database,please first try to store Clue in the database");
		}	
		
	}

	@Test
	public void testSetClues() {
		List<Clue> list = new ArrayList<Clue>();
		Clue clue = new Clue();
		clue.setValue("Wi-FI");
	
		
		list.add(clue);
		//dbmanager.setClues(list); FIXME	
		
		
		
	}*/
	
	@Test
	public void testGetOutcomes() {
		List<Outcome> List = dbmanager.getOutcomes();
		if (List.size()>0){
			Iterator<Outcome> i = List.iterator();
			while(i.hasNext()){
				Outcome outcome = i.next();
				assertNotNull(outcome);
			}
		}else{
			fail("There is not any Outcome in the database,please first try to store Outcome in the database");
		}	
		
	}

	

	@Test
	public void testGetThreats() {
		List<Threat> List = dbmanager.getThreats();
		if (List.size()>0){
			Iterator<Threat> i = List.iterator();
			while(i.hasNext()){
				Threat threat = i.next();
				assertNotNull(threat);
			}
		}else{
			fail("There is not any Threat in the database,please first try to store Threat in the database");
		}	
		
	}

	@Test
	public void testFindThreatbydescription() {
		String description ="threat";
		List<Threat> threats = dbmanager.findThreatbydescription(description);
		if(threats.size()>0)
			assertTrue(true);
		else
			fail("There is not any Threat in the database with this description,please first try with another description");	}

	@Test
	public void testSetThreats() {
		Outcome outcome = new Outcome();
		List<Outcome> outcomes = new ArrayList<Outcome>();
		outcome.setCostbenefit(0);
		SecureRandom random = new SecureRandom();
		outcome.setDescription("outcome");
		
		outcomes.add(outcome);
		Threat threat = new Threat();
		threat.setBadOutcomeCount(0);
		threat.setOutcomes(outcomes);
		threat.setDescription("test2");
		threat.setProbability(0);
		
		List<Threat> threats = new ArrayList<Threat>();
		threats.add(threat);
		dbmanager.setThreats(threats);	

		
	}

	@Test
	public void testGetRiskPolicies() {
		List<RiskPolicy> List = dbmanager.getRiskPolicies();
		if (List.size()>0){
			Iterator<RiskPolicy> i = List.iterator();
			while(i.hasNext()){
				RiskPolicy riskpolicy = i.next();
				assertNotNull(riskpolicy);
			}
		}else{
			fail("There is not any RiskPolicy in the database,please first try to store RiskPolicy in the database");
		}
		
	}
	
	@Test
	public void testGetAccessRequests() {
		List<AccessRequest> List = dbmanager.getAccessRequests();
		if (List.size()>0){
			Iterator<AccessRequest> i = List.iterator();
			while(i.hasNext()){
				AccessRequest accessrequest = i.next();
				assertNotNull(accessrequest);
			}
		}else{
			fail("There is not any AccessRequest in the database,please first try to store AccessRequest in the database");
		}
		
	}

	@Test
	public void testSetRiskPolicies() {
		List<RiskPolicy> list = new ArrayList<RiskPolicy>();
		RiskPolicy riskpolicy = new RiskPolicy();
		riskpolicy.setDescription("myrsikpolicy");
		riskpolicy.setRiskvalue(0);
	
		
		list.add(riskpolicy);
		dbmanager.setRiskPolicies(list);
		
	}
	
	@Test
	public void testSetSimpleEvents() {
		List<SimpleEvents> list = new ArrayList<SimpleEvents>();
		SimpleEvents event = new SimpleEvents();
		event.setEventType(dbmanager.getEventTypeByKey(EventTypes.LOG_IN));
		event.setUser(dbmanager.getUserByUsername("muses"));
		event.setData("jsonstring");
		event.setApplication(dbmanager.getApplicationByName("musesawaew"));
		event.setAsset(dbmanager.getAssetByLocation("Geneva"));
		event.setDate(new Date());
		event.setDevice(dbmanager.getDeviceByIMEI("9aa326e4fd9ccf61"));
		event.setTime(new Time(new Date().getTime()));
		list.add(event);
		dbmanager.setSimpleEvents(list);

	}
	
	@Test
	public void testSetSystemLogKRS() {
		List<SystemLogKrs> list = new ArrayList<SystemLogKrs>();
		SystemLogKrs logEntry = new SystemLogKrs();
		SecureRandom random = new SecureRandom();
		logEntry.setPreviousEventId(new BigInteger(30, random));
		logEntry.setCurrentEventId(new BigInteger(30, random));
		logEntry.setDecisionId(new BigInteger(30, random));
		logEntry.setUserBehaviourId(new BigInteger(30, random));
		logEntry.setSecurityIncidentId(new BigInteger(30, random));
		logEntry.setDeviceSecurityState(new BigInteger(30, random));
		logEntry.setRiskTreatment(null);
		logEntry.setStartTime(new Date());
		logEntry.setFinishTime(new Date());
		
		list.add(logEntry);
		dbmanager.setSystemLogKRS(list);
		
	}
	
	@Test
	public void testFindAccessRequestByEventId() {
		String eventID = "2";
		List<AccessRequest> accessRequests = dbmanager.findAccessRequestByEventId(eventID);
		if(accessRequests.size()>0)
			assertTrue(true);
		else
			fail("There is not any Access Request corresponding to that event_id.");
	}
	
	@Test
	public void testFindSecurityViolationByEventId() {
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		String defaultSessionId = "DFOOWE423423422H23H";
		String testOpenFileInMonitoredFolderInRestrictedZone = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Búsqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicación MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electrónico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Teléfono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Información de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuración de red móvil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador móvil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Búsqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Síntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducción de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Búsqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Cámara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresión móvil,com.sec.android.app.mobileprint,21;Reproductor de música,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de vídeo,com.sec.android.app.ve,4;Reproductor de vídeo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galería,com.sec.android.gallery3d,30682;Comando rápido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analógico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualización de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"open_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenFileInMonitoredFolderInRestrictedZone, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testOpenFileInMonitoredFolderInRestrictedZone);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		SimpleEvents event = dbmanager.findLastEventByEventType(7);
		List<SecurityViolation> secViolations = dbmanager.findSecurityViolationByEventId(event.getEventId());
		if(secViolations.size()>0)
			assertTrue(true);
		else
			fail("There is not any Security Violation corresponding to that event_id.");
	}
	
	@Test
	public void testFindSecurityViolationByDecisionId() {
		String decisionID = "628";
		SecurityViolation secViolation = dbmanager.findSecurityViolationByDecisionId(decisionID);
		if(secViolation != null)
			assertTrue(true);
		else
			System.out.println("There is not any Security Violation corresponding to that decision_id.");
			//fail("There is not any Security Violation corresponding to that decision_id.");
	}
	
	@Test
	public void testFindEventsByUserId() {
		String userID = "1";
		String day = "2015-01-09";
		String time = "17:00:00";
		/* To test if it looks for last events */
		SimpleEvents event = dbmanager.findEventsByUserId(userID, day, time, Boolean.TRUE);
		/* To test if it looks for next event */
		//List<SimpleEvents> events = dbmanager.findEventsByUserId(userID, day, time, Boolean.FALSE);
		if(event != null)
			assertTrue(true);
		else
			fail("There is not any Simple Events corresponding to that user_id, in those dates.");
	}
	
	@Test
	public void testSetPatternsKRS() {
		List<PatternsKrs> list = new ArrayList<PatternsKrs>();
		PatternsKrs logEntry = new PatternsKrs();
		logEntry.setActivatedAccount(0);
		logEntry.setAppMUSESAware(0);
		logEntry.setAppName("");
		logEntry.setAppVendor("");
		logEntry.setAssetConfidentialLevel("NONE");
		logEntry.setAssetLocation("");
		logEntry.setAssetName("");
		logEntry.setAssetValue(0);
		logEntry.setDecisionCause("");
		logEntry.setDeviceHasAccessibility(0);
		logEntry.setDeviceHasAntivirus(0);
		logEntry.setDeviceHasCertificate(0);
		logEntry.setDeviceHasPassword(0);
		logEntry.setDeviceIsRooted(0);
		logEntry.setDeviceOS("");
		logEntry.setDeviceOwnedBy("");
		logEntry.setDeviceScreenTimeout(BigInteger.ZERO);
		short zero = 0;
		logEntry.setDeviceTrustValue(0);
		logEntry.setDeviceType("");
		logEntry.setEventLevel("");
		logEntry.setEventTime(new Date());
		logEntry.setEventType("");
		logEntry.setLabel("GRANTED");
		logEntry.setLettersInPassword(0);
		logEntry.setMailContainsBCC(0);
		logEntry.setMailContainsCC(0);
		logEntry.setMailHasAttachment(0);
		logEntry.setMailRecipientAllowed(0);
		logEntry.setNumbersInPassword(0);
		logEntry.setPasswdHasCapitalLetters(0);
		logEntry.setPasswordLength(0);
		logEntry.setSilentMode(0);
		logEntry.setUsername("");
		logEntry.setUserRole("");
		logEntry.setUserTrustValue(0);
		logEntry.setWifiEncryption("[WPA2-PSK-TKIP+CCMP][ESS]");
		logEntry.setWifiEnabled(1);
		logEntry.setWifiConnected(1);
		logEntry.setBluetoothConnected(0);
		
		list.add(logEntry);
		dbmanager.setPatternsKRS(list);		
	}
	
	@Test
	public void testGetPatternsKRS() {
		List<PatternsKrs> List = dbmanager.getPatternsKRS();
		if (List.size()>0){
			Iterator<PatternsKrs> i = List.iterator();
			while(i.hasNext()){
				PatternsKrs pattern = i.next();
				assertNotNull(pattern);
			}
		}else{
			fail("There is not any pattern in the database, please start Data Mining process.");
		}			
		
	}
	
	@Test
	public void testFindDecisionTrustValuesByDecisionId() {
		String decisionID = "545";
		List<DecisionTrustvalues> trustValues = dbmanager.findDecisionTrustValuesByDecisionId(decisionID);
		if(trustValues.size()>0)
			assertTrue(true);
		else
			fail("There is not any Decision TrustValue corresponding to that decision_id.");
	}
	
	@Test
	public void testFindDecisionByAccessRequestId() {
		String accessRequestID = "80";
		List<Decision> trustValues = dbmanager.findDecisionByAccessRequestId(accessRequestID);
		if(trustValues.size()>0)
			assertTrue(true);
		else
			fail("There is not any Decision corresponding to that access_request_id.");
	}
	
	@Test
	public void testGetRoleById() {
		int roleID = 145;
		Roles role = dbmanager.getRoleById(roleID);
		if(role != null)
			assertTrue(true);
		else
			fail("There is not any Role corresponding to that role_id.");
	}
	
	@Test
	public void testGetDistinctDeviceOwnedBy() {
		List<String> values = dbmanager.getDistinctDeviceOS();		
		if(values != null)
			assertTrue(true);
		else
			fail("This column is empty");
	}

}
