package at.qe.sepm.skeleton.logic;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Timer implements Runnable
{
	@Autowired
	ConcurrentTaskScheduler concurrentTaskScheduler;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Timer.class);
	
	private ITimedAction action;
	private long timeStep;
	private long startTime;
	private long lastTime;
	ScheduledFuture<?> sFuture;
	
	public Timer(ITimedAction action, long timeStep)
	{
		startTime = new Date().getTime();
		this.action = action;
		this.timeStep = timeStep;
		
		sFuture = concurrentTaskScheduler.scheduleAtFixedRate(this, timeStep);
	}
	
	/**
	 * Gets called every timeStep ms.
	 */
	@Override
	public void run()
	{
		long now = new Date().getTime();
		long delta = now - lastTime;
		lastTime = now;
		
		if (delta > 2 * timeStep)
		{
			LOGGER.debug("large delay in timer call (" + delta + "ms)");
		}
		
		action.onTimeUpdate(delta);
	}
	
	/**
	 * Returns the total elapsed time.
	 * 
	 * @return
	 */
	public long getElapsedTime()
	{
		return (new Date().getTime()) - startTime;
	}
	
	/**
	 * Stops the timer.
	 */
	public void stop()
	{
		sFuture.cancel(false);
	}
	
}