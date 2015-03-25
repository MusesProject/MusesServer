package eu.musesproject.server.eventprocessor.correlator.model.owl;

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

import eu.musesproject.client.model.contextmonitoring.BluetoothState;

public class ConnectivityEvent extends Event {
	
	private int id;
	private String type;
	private long timestamp;
	private boolean mobileConnected;
	private boolean wifiEnabled;
	private boolean wifiConnected;
	private int wifiNeighbors;
	private boolean hiddenSSID;
	private String bssid;
	private String networkId;
	private String bluetoothConnected;
	private String wifiEncryption;
	private boolean airplaneMode;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isMobileConnected() {
		return mobileConnected;
	}
	public void setMobileConnected(boolean mobileConnected) {
		this.mobileConnected = mobileConnected;
	}
	public boolean isWifiEnabled() {
		return wifiEnabled;
	}
	public void setWifiEnabled(boolean wifiEnabled) {
		this.wifiEnabled = wifiEnabled;
	}
	public boolean isWifiConnected() {
		return wifiConnected;
	}
	public void setWifiConnected(boolean wifiConnected) {
		this.wifiConnected = wifiConnected;
	}
	public int getWifiNeighbors() {
		return wifiNeighbors;
	}
	public void setWifiNeighbors(int wifiNeighbors) {
		this.wifiNeighbors = wifiNeighbors;
	}
	public boolean isHiddenSSID() {
		return hiddenSSID;
	}
	public void setHiddenSSID(boolean hiddenSSID) {
		this.hiddenSSID = hiddenSSID;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	public String getBluetoothConnected() {
		return bluetoothConnected;
	}
	public void setBluetoothConnected(String bluetoothConnected) {
		this.bluetoothConnected = bluetoothConnected;
	}
	public boolean isAirplaneMode() {
		return airplaneMode;
	}
	public void setAirplaneMode(boolean airplaneMode) {
		this.airplaneMode = airplaneMode;
	}
	public String getWifiEncryption() {
		return wifiEncryption;
	}
	public void setWifiEncryption(String wifiEncryption) {
		this.wifiEncryption = wifiEncryption;
	}
	

	

}
