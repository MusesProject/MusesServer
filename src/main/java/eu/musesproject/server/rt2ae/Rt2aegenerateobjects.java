package eu.musesproject.server.rt2ae;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import eu.musesproject.server.risktrust.DeviceSecurityState;
import eu.musesproject.server.risktrust.SecurityIncident;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 UNIGE
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

public class Rt2aegenerateobjects {
	
	
	
	public Rt2aegenerateobjects() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SecurityIncident generateSecurityIncident(){
		
		double lower = 0;
		double higher = 10000;
		double random = (double)(Math.random() * (higher-lower)) + lower;
		
		int lower2 = 1;
		int higher2 = 100;
		int random2 = (int)(Math.random() * (higher2-lower2)) + lower2;

		double random1 = Math.random();
		
		SecurityIncident var = new SecurityIncident();
		
		var.setCostBenefit(random);
		var.setDescription("Something happens on the asset, the value of the asset has been decrease because of this security incident");
		var.setDeviceid(random2);
		var.setProbability(random1);
		return var;
	}
	
	public List<SecurityIncident> generateListSecurityIncident(){
		List<SecurityIncident> var = new ArrayList<SecurityIncident>(); 
		for (int i = 0; i < 5; i++) {
			var.add(this.generateSecurityIncident());
		}
		return var;
	}
	
	
	public DeviceSecurityState generateDeviceSecurityState(){
			
		return null;
	}

	  /*public static void main (String[] args){
		  
		 Rt2aegenerateobjects generator = new Rt2aegenerateobjects();
		 SecurityIncident var = generator.generateSecurityIncident();
		 List<SecurityIncident> vars = generator.generateListSecurityIncident();
		 for (int i = 0; i < vars.size(); i++) {
				System.out.println("costbenefit: "+vars.get(i).getCostBenefit()+" description: "+vars.get(i).getDescription()+ " Probability: "+vars.get(i).getProbability()+" deviceId: "+vars.get(i).getDeviceid());
				System.out.println("\n");
		}
		System.out.println("costbenefit: "+var.getCostBenefit()+" description: "+var.getDescription()+ " Probability: "+var.getProbability()+" deviceId: "+var.getDeviceid());
	  
	  }*/
}
