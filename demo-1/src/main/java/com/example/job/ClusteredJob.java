package com.example.job;

import java.time.LocalDateTime;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClusteredJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(ClusteredJob.class);
	
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	logger.info("⚙️ ClusteredJob 실행 - " + LocalDateTime.now());
    }
}
