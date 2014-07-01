package eu.musesproject.server.eventprocessor.correlator.global;

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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class StatusGlobal {
	
	private String status = null;
	private static volatile String flags = "";
	private final String SEPARATOR = ";";
	
	private Logger logger = Logger.getLogger(StatusGlobal.class.getName());

	public void setStatus(String st) {
		status = st;
	}

	public String getStatus() {
		return status;
	}
	
	public void log(String message){		
		logger.log(Level.INFO, message);
	}
	
	public void addFlag(String flag){
		
		//logger.log(Level.INFO, "Adding flag:" + flag);
		StatusGlobal.flags += flag + SEPARATOR;
		//logger.log(Level.INFO, "Current flags:" + StatusGlobal.flags);
	}
	
	public static boolean containsFlag(String flag){
		return StatusGlobal.flags.contains(flag);
	}
	
	public static String getFlags(){
		return StatusGlobal.flags;
	}
	
	
}
