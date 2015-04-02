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


public class DeviceProtectionEvent extends Event {
	
	private int id;
	private String type;
	private long timestamp;
	private boolean isPasswordProtected;
	private boolean isPatternProtected;
	private boolean isTrustedAntivirusInstalled;
	private boolean isRooted;
	private boolean isRootPermissionGiven;
	private int screenTimeoutInSeconds;
	private boolean musesDatabaseExists;
	private String ipaddress;
	

	
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

	public boolean getIsRooted() {
		return isRooted;
	}
	public void setIsRooted(boolean isRooted) {
		this.isRooted = isRooted;
	}
	public boolean getIsPasswordProtected() {
		return isPasswordProtected;
	}
	public void setIsPasswordProtected(boolean isPasswordProtected) {
		this.isPasswordProtected = isPasswordProtected;
	}
	public boolean getIsPatternProtected() {
		return isPatternProtected;
	}
	public void setIsPatternProtected(boolean isPatternProtected) {
		this.isPatternProtected = isPatternProtected;
	}
	public boolean isTrustedAntivirusInstalled() {
		return isTrustedAntivirusInstalled;
	}
	public void setTrustedAntivirusInstalled(boolean isTrustedAntivirusInstalled) {
		this.isTrustedAntivirusInstalled = isTrustedAntivirusInstalled;
	}
	public boolean isRootPermissionGiven() {
		return isRootPermissionGiven;
	}
	public void setRootPermissionGiven(boolean isRootPermissionGiven) {
		this.isRootPermissionGiven = isRootPermissionGiven;
	}
	public int getScreenTimeoutInSeconds() {
		return screenTimeoutInSeconds;
	}
	public void setScreenTimeoutInSeconds(int screenTimeoutInSeconds) {
		this.screenTimeoutInSeconds = screenTimeoutInSeconds;
	}
	public boolean isMusesDatabaseExists() {
		return musesDatabaseExists;
	}
	public void setMusesDatabaseExists(boolean musesDatabaseExists) {
		this.musesDatabaseExists = musesDatabaseExists;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}


}
