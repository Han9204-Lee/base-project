package com.example.job;

import java.time.LocalDateTime;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.service.LogCopyService;

@Component
public class LogCopyJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(LogCopyJob.class);
	@Autowired
    private LogCopyService logCopyService;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	logger.info("⚙️ LogCopyJob 실행 - " + LocalDateTime.now());
    	logCopyService.runClusterTask();
    }
}