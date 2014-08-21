package eu.musesproject.server.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.musesproject.server.db.shield.TestClass;

public class DailyJob implements Job{

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		TestClass t = new TestClass();
		t.test();
		Scheduler scheduler = new SchedulerImpl();
		scheduler.erase();
	}

}
