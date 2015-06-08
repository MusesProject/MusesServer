package eu.musesproject.server.knowledgecompiler;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 UGR
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

import junit.framework.TestCase;
import eu.musesproject.server.knowledgerefinementsystem.model.Pattern;

public class TestKnowledgeCompiler extends TestCase {

	
	/**
	  * testComputePolicyBasedOnDecisions - JUnit test case whose aim is to test the notification from the data miner component of a new detected pattern, as result of data mining
	  *
	  * @param pattern - Pattern built by the data mining component 
	  * 
	  */
	public final void testNotifyPattern(Pattern pattern) {
		assertTrue(true);
	}

	/**
	  * testCompileNewRules - JUnit test case whose aim is to test the compilation of new rules from notified patterns
	  *
	  * @param none 
	  * 
	  */
	public final void testCompileNewRules() {
		assertTrue(true);
	}

}
