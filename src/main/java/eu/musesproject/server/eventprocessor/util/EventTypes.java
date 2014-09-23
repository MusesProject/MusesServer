package eu.musesproject.server.eventprocessor.util;

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

public class EventTypes {
	//public final static String FILEOBSERVER = "CONTEXT_SENSOR_FILEOBSERVER";
	public final static String FILEOBSERVER = "ACTION_REMOTE_FILE_ACCESS";
	public final static String CONNECTIVITY = "CONTEXT_SENSOR_CONNECTIVITY";
	public final static String DEVICE_PROTECTION = "CONTEXT_SENSOR_DEVICE_PROTECTION";
	public final static String APPOBSERVER = "ACTION_APP_OPEN";
	public final static String USERBEHAVIOR = "USER_BEHAVIOR";
	public final static String SEND_MAIL = "ACTION_SEND_MAIL";
	public final static String VIRUS_FOUND = "VIRUS_FOUND";
	public final static String CHANGE_SECURITY_PROPERTY = "security_property_changed";
	public final static String SAVE_ASSET = "SAVE_ASSET";
}
