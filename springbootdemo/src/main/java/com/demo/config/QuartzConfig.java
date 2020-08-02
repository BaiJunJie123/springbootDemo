package com.demo.config;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author 白俊杰
 * @Date 2019/12/3
 * @Description
 **/
@Configuration
public class QuartzConfig {

	@Autowired
	private AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory;

	private static final String QUARTZ_CONFIG = "/quartz.properties";
	// ---------------------------- jobDetail 实列---------------------------

	//配置JobFactory
	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	// --------------------------schedulerFacctoryBean --------------------------------------
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
		SchedulerFactoryBean bean = new SchedulerFactoryBean();
		//自动覆写存在的job
		bean.setOverwriteExistingJobs(true);
		//配置数据源，使用与项目一致的数据源
		bean.setDataSource(dataSource);
		//将spring管理job自定义工厂交由调度器维护
		bean.setJobFactory(autowiringSpringBeanJobFactory);
		//设置调度器自动运行
		bean.setAutoStartup(true);
		//设置定时调度器命名空间  （集群名）
		//  bean.setSchedulerName("MY-QUARTZ-SCHEDULER");
		//设置存储在quartz上文中的Spring应用上下文key
		bean.setApplicationContextSchedulerContextKey("applicationContext");
		try {
			//读取配置文件
			bean.setQuartzProperties(quartzProperties());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bean;
	}

	//------------------------------properties--------------------------------------
	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_CONFIG));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}
}
