package at.qe.sepm.skeleton.logic;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Timer
{
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private ITimedAction action;
	private long timeStep;
	private long startTime;
	private long lastTime;
	ScheduledFuture<?> sFuture;
	
	/**
	 * Creates a new Timer to call action every timeStep ms. Uses scheduler to start the thread.
	 * 
	 * @param scheduler
	 * @param action
	 * @param timeStep
	 */
	public Timer(ThreadPoolTaskScheduler scheduler, ITimedAction action, long timeStep)
	{
		startTime = new Date().getTime();
		this.action = action;
		this.timeStep = timeStep;
		
		LOGGER.debug("Timer thread start");
		sFuture = scheduler.scheduleAtFixedRate(
				() -> {
					long now = new Date().getTime();
					long delta = now - lastTime;
					lastTime = now;
					
					action.onTimeUpdate(delta);
				}, timeStep);
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