package at.qe.sepm.skeleton.logic;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Class for calling a function periodically while keeping track of the delta time.
 */
public class Timer
{
	private long startTime;
	private long lastTime;
	ScheduledFuture<?> sFuture;
	
	/**
	 * Creates a new Timer to call action every timeStep ms. Uses scheduler to start the thread.
	 *
	 * @param scheduler
	 *         Scheduling service to use for the thread.
	 * @param action
	 *         Action to be executed.
	 * @param timeStep
	 *         Time between executions.
	 */
	public Timer(ThreadPoolTaskScheduler scheduler, ITimedAction action, long timeStep)
	{
		startTime = new Date().getTime();
		lastTime = startTime;
		
		sFuture = scheduler.scheduleAtFixedRate(() -> {
				long now = new Date().getTime();
				long delta = now - lastTime;
				lastTime = now;
				
				action.onTimeUpdate(delta);
			}, timeStep);
	}
	
	/**
	 * @return The total elapsed time.
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