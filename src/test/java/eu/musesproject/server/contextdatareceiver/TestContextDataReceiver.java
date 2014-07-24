package eu.musesproject.server.contextdatareceiver;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 S2 Grupo
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.TestCase;
import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.client.model.RequestType;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.connectionmanager.StubConnectionManager;
import eu.musesproject.server.contextdatareceiver.stub.ContextEventFactory;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;

public class TestContextDataReceiver extends TestCase {
	
	private final String defaultSessionId = "DFOOWE423423422H23H";
	private final String testJSONLoginMessage = "{\"requesttype\":\"login\",\"username\":\"muses\",\"password\":\"muses\", \"device_id\":\"fsdfsd2123123123\"}";
	private final String testJSONBlacklistApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"name\":\"\",\"timestamp\":1400581297192,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.google.process.gapps, com.google.android.gms.drive, com.connected.watch, com.samsung.android.app.storyalbumwidget, com.android.bluetooth, com.sec.android.app.keyguard, com.android.bluetooth, com.android.phone, com.google.process.location, com.att.android.digitallocker:loadingProcess, com.smlds, com.google.android.googlequicksearchbox:search, com.att.android.digitallocker:loadingProcess, com.sec.knox.seandroid, com.google.process.location, com.sec.esdk.elm, com.android.bluetooth, com.google.process.gapps, com.vlingo.midas, com.android.phone, com.google.process.location, com.google.process.location, com.google.process.location, com.google.android.gms, com.android.bluetooth, com.android.bluetooth, com.sec.knox.eventsmanager, eu.musesproject.client, com.vlingo.midas, com.google.process.location, com.samsung.android.app.episodes, tv.peel.samsung.app, com.android.bluetooth, system, com.android.phone, com.android.phone, system, com.google.process.location, com.sec.android.widgetapp.ap.hero.accuweather, system, com.google.android.gms, com.android.bluetooth, com.samsung.android.MtpApplication, com.android.phone, com.android.bluetooth, com.sec.android.daemonapp, com.att.android.digitallocker:loadingProcess, com.samsung.android.app.filterinstaller, com.matchboxmobile.wisp, com.connected.watch, system, com.android.bluetooth, com.sec.spp.push, com.android.bluetooth, com.google.process.gapps, com.google.android.talk, system, com.android.bluetooth, com.android.systemui, com.connected.watch, com.samsung.android.app.episodes, com.android.bluetooth, com.samsung.android.app.episodes, com.samsung.android.providers.context, com.android.phone, com.android.systemui, com.android.phone, com.sec.android.inputmethod, com.sec.android.service.health.sensor, android.process.media, com.google.process.location, com.google.android.gms, com.sec.android.app.sns3, com.sec.android.allshare.service.mediashare, com.sec.knox.containeragent, org.simalliance.openmobileapi.service:remote, com.samsung.SMT, eu.musesproject.client, com.sec.android.pagebuddynotisvc, com.android.nfc:handover, com.google.process.location, com.sec.android.app.sns3, com.wssyncmldm, com.google.process.gapps, com.samsung.android.app.gestureservice]\",\"appname\":\"Muses Aware App\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"unknown\",\"timestamp\":1400581292482,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"9\",\"hiddenssid\":\"false\",\"networkid\":\"3\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1400581299619,\"type\":\"open_application\",\"properties\":{\"name\":\"Gmail\",\"package\":\"com.google\",\"version\":\"12.1\"}},\"requesttype\":\"online_decision\"}";
	private final String testJSONBlacklistAppReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401985173371,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1401985165845,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401985173371,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"name\":\"Sweden Connectivity\",\"version\":\"\"}},\"requesttype\":\"local_decision\"}";
	private final String testJSONOpenFileSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"name\":\"office\",\"timestamp\":1400581297192,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.google.process.gapps, com.google.android.gms.drive, com.connected.watch, com.samsung.android.app.storyalbumwidget, com.android.bluetooth, com.sec.android.app.keyguard, com.android.bluetooth, com.android.phone, com.google.process.location, com.att.android.digitallocker:loadingProcess, com.smlds, com.google.android.googlequicksearchbox:search, com.att.android.digitallocker:loadingProcess, com.sec.knox.seandroid, com.google.process.location, com.sec.esdk.elm, com.android.bluetooth, com.google.process.gapps, com.vlingo.midas, com.android.phone, com.google.process.location, com.google.process.location, com.google.process.location, com.google.android.gms, com.android.bluetooth, com.android.bluetooth, com.sec.knox.eventsmanager, eu.musesproject.client, com.vlingo.midas, com.google.process.location, com.samsung.android.app.episodes, tv.peel.samsung.app, com.android.bluetooth, system, com.android.phone, com.android.phone, system, com.google.process.location, com.sec.android.widgetapp.ap.hero.accuweather, system, com.google.android.gms, com.android.bluetooth, com.samsung.android.MtpApplication, com.android.phone, com.android.bluetooth, com.sec.android.daemonapp, com.att.android.digitallocker:loadingProcess, com.samsung.android.app.filterinstaller, com.matchboxmobile.wisp, com.connected.watch, system, com.android.bluetooth, com.sec.spp.push, com.android.bluetooth, com.google.process.gapps, com.google.android.talk, system, com.android.bluetooth, com.android.systemui, com.connected.watch, com.samsung.android.app.episodes, com.android.bluetooth, com.samsung.android.app.episodes, com.samsung.android.providers.context, com.android.phone, com.android.systemui, com.android.phone, com.sec.android.inputmethod, com.sec.android.service.health.sensor, android.process.media, com.google.process.location, com.google.android.gms, com.sec.android.app.sns3, com.sec.android.allshare.service.mediashare, com.sec.knox.containeragent, org.simalliance.openmobileapi.service:remote, com.samsung.SMT, eu.musesproject.client, com.sec.android.pagebuddynotisvc, com.android.nfc:handover, com.google.process.location, com.sec.android.app.sns3, com.wssyncmldm, com.google.process.gapps, com.samsung.android.app.gestureservice]\",\"appname\":\"Muses Aware App\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"WPA2\",\"timestamp\":1400581292482,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"9\",\"hiddenssid\":\"false\",\"networkid\":\"3\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1400581299619,\"type\":\"open_asset\",\"properties\":{\"resourceName\":\"file1.png\",\"resourceType\":\"file\",\"resourcePath\":\"/var/file1.png\"}},\"requesttype\":\"online_decision\"}";
	private final String testJSONOpenFileUnsecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"name\":\"office\",\"timestamp\":1400581297192,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.google.process.gapps, com.google.android.gms.drive, com.connected.watch, com.samsung.android.app.storyalbumwidget, com.android.bluetooth, com.sec.android.app.keyguard, com.android.bluetooth, com.android.phone, com.google.process.location, com.att.android.digitallocker:loadingProcess, com.smlds, com.google.android.googlequicksearchbox:search, com.att.android.digitallocker:loadingProcess, com.sec.knox.seandroid, com.google.process.location, com.sec.esdk.elm, com.android.bluetooth, com.google.process.gapps, com.vlingo.midas, com.android.phone, com.google.process.location, com.google.process.location, com.google.process.location, com.google.android.gms, com.android.bluetooth, com.android.bluetooth, com.sec.knox.eventsmanager, eu.musesproject.client, com.vlingo.midas, com.google.process.location, com.samsung.android.app.episodes, tv.peel.samsung.app, com.android.bluetooth, system, com.android.phone, com.android.phone, system, com.google.process.location, com.sec.android.widgetapp.ap.hero.accuweather, system, com.google.android.gms, com.android.bluetooth, com.samsung.android.MtpApplication, com.android.phone, com.android.bluetooth, com.sec.android.daemonapp, com.att.android.digitallocker:loadingProcess, com.samsung.android.app.filterinstaller, com.matchboxmobile.wisp, com.connected.watch, system, com.android.bluetooth, com.sec.spp.push, com.android.bluetooth, com.google.process.gapps, com.google.android.talk, system, com.android.bluetooth, com.android.systemui, com.connected.watch, com.samsung.android.app.episodes, com.android.bluetooth, com.samsung.android.app.episodes, com.samsung.android.providers.context, com.android.phone, com.android.systemui, com.android.phone, com.sec.android.inputmethod, com.sec.android.service.health.sensor, android.process.media, com.google.process.location, com.google.android.gms, com.sec.android.app.sns3, com.sec.android.allshare.service.mediashare, com.sec.knox.containeragent, org.simalliance.openmobileapi.service:remote, com.samsung.SMT, eu.musesproject.client, com.sec.android.pagebuddynotisvc, com.android.nfc:handover, com.google.process.location, com.sec.android.app.sns3, com.wssyncmldm, com.google.process.gapps, com.samsung.android.app.gestureservice]\",\"appname\":\"Muses Aware App\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"unencrypted\",\"timestamp\":1400581292482,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"9\",\"hiddenssid\":\"false\",\"networkid\":\"3\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1400581299619,\"type\":\"open_asset\",\"properties\":{\"resourceName\":\"file1.png\",\"resourceType\":\"file\",\"resourcePath\":\"/var/file1.png\"}},\"requesttype\":\"online_decision\"}";
	private final String testJSONUpdateContextEvents = "{\"sensor\":{},\"action\":{\"timestamp\":1401881514887,\"type\":\"update\"},\"requesttype\":\"update_context_events\"}";	
	private final String testOpenPublicAsset = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1401986243309,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986259546,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_beer_competition.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"insensitive\"}},\"requesttype\":\"local_decision\"}";
	private final String testOpenConfAssetUnsecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"local_decision\"}";
	private final String testOpenConfAssetSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"local_decision\"}";
	private final String testOpenApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401985173371,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1401985165845,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401985173371,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"name\":\"Sweden Connectivity\",\"version\":\"\"}},\"requesttype\":\"local_decision\"}";
	private final String testOpenBlacklistApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401985173371,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1401985165845,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401985173371,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"name\":\"Gmail\",\"version\":\"\"}},\"requesttype\":\"local_decision\"}";
	
	private final String testOpenConfAssetUnsecureTweaked = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402042061447,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1402042020465,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402042063528,\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"local_decision\"}";
	
	private final String testDemoMonday = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402302631913,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1402302581898,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402302631913,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"appname\":\"Gmail\",\"version\":\"\"}},\"requesttype\":\"local_decision\"}";
	private final String testDemoMonday1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"appname\":\"Gmail\",\"version\":\"\"}},\"requesttype\":\"local_decision\"}";
	
	private final String testnotBlacklist = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"appname\":\"CorpApp\",\"version\":\"\"}},\"requesttype\":\"local_decision\"}";
	
	private final String testUpdateEvents = "{\"sensor\":{},\"action\":{\"timestamp\":1404830521049,\"type\":\"update\"},\"requesttype\":\"update_context_events\"}";
	
	private final String testEmailWithAttachments = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 2,\"attachmentInfo\": \"name,type,size;name2,type2,size2\"}},\"requesttype\":\"online_decision\"}";
	/**
	  * testStoreEvent - JUnit test case whose aim is to test the storage of an incoming event from the Connection Manager
	  *
	  * @param none
	  * 
	  */
	public final void testStoreEvent() {
		
		UserContextEventDataReceiver receiver = UserContextEventDataReceiver.getInstance();
		StubConnectionManager stubConnectionManager = (StubConnectionManager)receiver.getConnectionManager();
		stubConnectionManager.notifyEvent();
		assertNotNull(receiver.getEventCorrelationData());
	}

	/**
	  * testIsConnectionManagerActive - JUnit test case whose aim is to test the correct activation of the Connection Manager
	  *
	  * @param none
	  * 
	  */
	public final void testIsConnectionManagerActive() {
		UserContextEventDataReceiver receiver = UserContextEventDataReceiver.getInstance();
		assertTrue(receiver.isConnectionManagerActive());
	}
	
	
	
	/**
	  * testProcessEvent - JUnit test case whose aim is to test the redirection of an incoming event to be processed by the CRTEP
	  *
	  * @param none 
	  * 
	  */
	
	 public final void testStartProcessor(){

		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		assertNotNull(des);
	}
	
	/**
	  * testJsonParse - JUnit test case whose aim is to test transformation of a JSON string received from the Connection Manager into the original Context Event
	  *
	  * @param none 
	  * 
	  */
	
	public final void testJsonParse(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testJSONOpenFileSecure);
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

	public final void testLogin() {

		JSONObject root;
		String requestType = null;
		String username = null;
		String password = null;
		String deviceId = null;
		System.out.println(testJSONLoginMessage);
		try {
			root = new JSONObject(testJSONLoginMessage);

			requestType = root.getString(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER);

			if (requestType.equals(RequestType.LOGIN)) {
				System.out.println("LOGIN REQUEST");//TODO Use Logger
				//retrieveCredentials
				username = root.getString(JSONIdentifiers.AUTH_USERNAME);
				password = root.getString(JSONIdentifiers.AUTH_PASSWORD);
				deviceId = root.getString(JSONIdentifiers.AUTH_DEVICE_ID);
				
				System.out.println("Login attempt with credentials: "+username+"-"+password+"-"+deviceId);
				//Authentication 
				if (username.equals("muses")&&(password.equals("muses"))){//TODO Authentication with database
					System.out.println("Authentication successful");
					//Send authentication response with success message
					JSONObject responseObject = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, "SUCCESS", "Successfully authenticated");
	                System.out.println(responseObject.toString());
					//connManager.sendData(sessionId, requestObject.toString());//TODO Store this sessionId as authenticated
				}else{
					System.out.println("Authentication failed");
					//Send authentication response with failure message
					JSONObject responseObject = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, "FAIL", "Incorrect password");
	                System.out.println(responseObject.toString());
					//connManager.sendData(sessionId, requestObject.toString());
				}
				
			} else {
				List<ContextEvent> list = JSONManager
						.processJSONMessage(testJSONLoginMessage);
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					ContextEvent contextEvent = (ContextEvent) iterator.next();
					if (contextEvent.getType() == null) {
						System.out.println("TYPE IS NULL");
					} else {
						System.out.println("TYPE:" + contextEvent.getType());
					}
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public final void testLoginReal() {
	
		ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		
		cb.receiveCb("EIIWJ232", testJSONLoginMessage);
	}
	

	
	
	public final void testOpenPublicAsset(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenPublicAsset, "local_decision");
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
	
	
	public final void testOpenConfAssetSecure(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenConfAssetSecure, "local_decision");
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
	
	public final void testOpenBlacklistApp(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testDemoMonday1, "local_decision");
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
	
	public final void testUpdateEvents(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testUpdateEvents);
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
	
	
	public final void testEmailWithAttachments(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithAttachments, "online_decision");
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
