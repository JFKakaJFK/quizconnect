package at.qe.sepm.skeleton.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Starts the data generator on server startup if not running as a JUnit test.
 */
@Component
@Scope("application")
public class GeneratorStarter implements ApplicationListener<ContextRefreshedEvent>
{
	@Autowired
	DataGeneratorService dataGenService;
	
	private boolean threadStarted = false;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		if (!threadStarted)
		{
			// don't start generator if application is being executed as JUnit test
			if (!isJUnitTest())
			{
				dataGenService.generateData(); // call data gen
			}
			
			threadStarted = true;
		}
	}
	
	/**
	 * Scans its own stack trace and returns true if its currently running as a
	 * jUnit test.
	 *
	 * @return True if currently running in a JUnit test environment.
	 */
	private static boolean isJUnitTest()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : stackTrace)
		{
			if (element.getClassName().startsWith("org.junit."))
			{
				return true;
			}
		}
		return false;
	}
}