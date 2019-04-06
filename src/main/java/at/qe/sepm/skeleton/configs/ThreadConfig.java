package at.qe.sepm.skeleton.configs;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
public class ThreadConfig
{
	@Bean
	public ConcurrentTaskScheduler concurrentTaskExecutor()
	{
		ConcurrentTaskScheduler concurrentTaskScheduler = new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(4));
		
		return concurrentTaskScheduler;
	}
}