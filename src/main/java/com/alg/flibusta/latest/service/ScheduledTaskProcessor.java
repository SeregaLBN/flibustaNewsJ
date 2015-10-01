package com.alg.flibusta.latest.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableAsync
@EnableScheduling
public class ScheduledTaskProcessor {
	private static final Log logger = LogFactory.getLog(ScheduledTaskProcessor.class);

	@Autowired
	Refresher _refresher;

	/*  1. fixedDelay - период между завершением предыдущего запуска и началом следущего запуска задачи в миллисекундах
	 *  2. fixedRate - период между каждым запуском задачи в миллисекундах (считается от старта предыдущей задачи)
	 *  3. cron - указывает cron подобное выражение, в котором можно более точно указать периоды запуска задачи
	 */
	@Scheduled(initialDelay=10000, fixedDelay = 60*60*1000)
//	@Async
	public void doSomething() {
//		System.out.println("something that should execute periodically");
		try {
			_refresher.run();
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

}