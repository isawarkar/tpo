package tpo.admin.backup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import tpo.beans.Parent;

@Configuration
public class QuartzConfiguration extends Parent {

	@Bean
	public MethodInvokingJobDetailFactoryBean doBackup() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
		obj.setTargetBeanName("collegeConnectBackUp");
		obj.setTargetMethod("doBackup");
		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean checkUserLicence() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
		obj.setTargetBeanName("collegeConnectBackUp");
		obj.setTargetMethod("checkUserLicence");
		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean sendBirthDayEmail() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
		obj.setTargetBeanName("collegeConnectBackUp");
		obj.setTargetMethod("sendBirthDayEmail");
		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean sendFeePayReminder() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
		obj.setTargetBeanName("collegeConnectBackUp");
		obj.setTargetMethod("sendFeePayReminder");
		return obj;
	}

	// Job is scheduled after every 1 minute

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBeanDoBackup() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
		stFactory.setJobDetail(doBackup().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("cronTriggerFactoryBeanDoBackup");
		stFactory.setGroup("mygroup");
		stFactory.setCronExpression(cronExpression);
		return stFactory;
	}

	// Job is scheduled after every 1 minute

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBeanCheckUserLicence() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
		stFactory.setJobDetail(checkUserLicence().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("cronTriggerFactoryBeanCheckUserLicence");
		stFactory.setGroup("mygroup");
		stFactory.setCronExpression(cronExpression);
		return stFactory;
	}

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBeanSendBirthDayEmail() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
		stFactory.setJobDetail(sendBirthDayEmail().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("cronTriggerFactoryBeanSendBirthDayEmail");
		stFactory.setGroup("mygroup");
		stFactory.setCronExpression(cronExpression);
		return stFactory;
	}

	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBeanSendFeePayReminder() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
		stFactory.setJobDetail(sendFeePayReminder().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("cronTriggerFactoryBeanSendFeePayReminder");
		stFactory.setGroup("mygroup");
		stFactory.setCronExpression(cronExpression);
		return stFactory;
	}

	
}