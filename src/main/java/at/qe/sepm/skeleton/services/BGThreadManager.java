package at.qe.sepm.skeleton.services;

import java.util.Arrays;
import java.util.List;

import at.qe.sepm.skeleton.services.DataGeneratorServiceSEPM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Scope("application")
public class BGThreadManager implements ApplicationListener<ContextRefreshedEvent>
{

	
	@Autowired
	DataGeneratorServiceSEPM dataGenService;
	
	private boolean threadStarted = false;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		if (!threadStarted)
		{
			// don't start BGThread if application is being executed as JUnit test
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
	 * @return
	 */
	public static boolean isJUnitTest()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		List<StackTraceElement> list = Arrays.asList(stackTrace);
		for (StackTraceElement element : list)
		{
			if (element.getClassName().startsWith("org.junit."))
			{
				return true;
			}
		}
		return false;
	}
}