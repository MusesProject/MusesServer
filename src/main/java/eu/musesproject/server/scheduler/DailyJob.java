package eu.musesproject.server.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DailyJob implements Job{

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		Scheduler scheduler = new SchedulerImpl();
		scheduler.erase();
	}

}
