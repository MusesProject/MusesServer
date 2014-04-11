/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.knowledgecompiler;

import eu.musesproject.server.knowledgerefinementsystem.model.Message;
import eu.musesproject.server.knowledgerefinementsystem.model.Pattern;

/**
 * Class KnowledgeCompiler
 * 
 * @author Sergio Zamarripa (S2)
 * @version Oct 7, 2013
 */

public class KnowledgeCompiler {
	
	/**
	 * Info DM
	 * 
	 * This method starts the process of compilation of new rules with the new mined patterns (changes of environment), based on 
	 * current rules.
	 * 
	 *
	 * 
	 * @param void (it retrieves previously mined patterns, already notified during the mining process)
	 * 
	 * @return void
	 */
	
	public void compileNewRules(){

	}	
	
	/**
	 * Info DM
	 * 
	 * This method is used by the data miner to notify the finding of a new pattern associated to a clue pattern (sequence of events
	 * associated to a security incident)
	 * 
	 *
	 * 
	 * @param pattern
	 * 
	 * @return void
	 */
	
	public void notifyPattern(Pattern pattern){

	}
	
	/**
	 * Info KN
	 * 
	 * This method is used by the knowledge compiler to log the processing results and other feedback to be available for the CSO
	 * 
	 *
	 * 
	 * @param message
	 * 
	 * @return void
	 */
	
	public void logProcessingResult(Message message){

	}
	

}
