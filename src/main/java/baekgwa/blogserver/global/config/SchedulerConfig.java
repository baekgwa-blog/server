package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : SchedulerConfig
 * Author      : Baekgwa
 * Date        : 25. 11. 15.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 15.     Baekgwa               Initial creation
 */
@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(5);
		scheduler.setThreadNamePrefix("Scheduler-");
		scheduler.initialize();

		taskRegistrar.setTaskScheduler(scheduler);
	}
}
