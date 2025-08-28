package com.example.config;

import java.util.List;

import javax.sql.DataSource;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import com.example.job.ClusteredJob;
import com.example.job.LogCopyJob;

@Configuration
public class QuartzConfig {
	
    /*
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, List<JobDetail> jobDetails, List<Trigger> triggers) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource); // DB 기반 클러스터링을 위해 DataSource 지정
        factory.setJobDetails(jobDetails.toArray(new JobDetail[0]));
        factory.setTriggers(triggers.toArray(new Trigger[0]));
        return factory;
    }*/
    
	/*
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, ApplicationContext applicationContext) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        // JobDetail/Trigger는 Bean으로 이미 등록되어 있으므로 자동 인식됨
        return factory;
    }
    */
	
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            DataSource dataSource,
            List<JobDetail> jobDetails,
            List<Trigger> triggers,
            ApplicationContext applicationContext) {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJobDetails(jobDetails.toArray(new JobDetail[0]));
        factory.setTriggers(triggers.toArray(new Trigger[0]));

        // ✅ Autowired 지원 JobFactory 등록
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);

        return factory;
    }

    /**
     * Spring이 관리하는 Bean을 Quartz Job에서도 사용할 수 있게 해주는 JobFactory
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(ApplicationContext context) {
            this.beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);  // ✅ @Autowired 적용
            return job;
        }
    }

    @Bean
    public JobDetail clusteredJobDetail() {
        return JobBuilder.newJob(ClusteredJob.class)
                .withIdentity("clusteredJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger clusteredJobTrigger(@Qualifier("clusteredJobDetail") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("clusteredJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */5 * * * ?"))
                .build();
    }
    
    /**
     * 파일로부터 읽어서 저장하는 Job
     * @return
     */
    @Bean
    public JobDetail logCopyJobDetail() {
        return JobBuilder.newJob(LogCopyJob.class)
                .withIdentity("logCopyJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger logCopyJobTrigger(@Qualifier("logCopyJobDetail") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("logCopyJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * * * ?")) // 매 1분마다 실행
                .build();
    }
}