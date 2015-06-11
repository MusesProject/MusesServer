package eu.musesproject.server.scheduler;

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


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.musesproject.server.connectionmanager.ComMainServlet;

public class DailyJob implements Job{

	private static final String MUSES_TAG = "MUSES_TAG";
	private static Logger logger = Logger.getLogger(ComMainServlet.class.getName());

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		Scheduler scheduler = new SchedulerImpl();
		logger.log(Level.INFO, MUSES_TAG + "  Daily Job called..");
		scheduler.erase();
	}

}
