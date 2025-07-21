package com.example.config;

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.example.job.ClusteredJob;

import org.quartz.JobDetail;

import javax.sql.DataSource;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail clusteredJobDetail() {
        return JobBuilder.newJob(ClusteredJob.class)
                .withIdentity("clusteredJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger sampleJobTrigger(JobDetail clusteredJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(clusteredJobDetail)
                .withIdentity("sampleJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
                .build();
    }
    
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobDetail clusteredJobDetail, Trigger sampleJobTrigger) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource); // DB 기반 클러스터링을 위해 DataSource 지정
        factory.setJobDetails(clusteredJobDetail);
        factory.setTriggers(sampleJobTrigger);
        return factory;
    }
}