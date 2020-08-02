package com.demo.util;

import com.demo.entity.SchedulingJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.quartz.JobBuilder.newJob;

/**
 * @author 白俊杰
 * @Date 2019/12/3
 * @Description
 **/
@Component
public class QuartzUtils {

    @Autowired
    @Qualifier("schedulerFactoryBean")
    private SchedulerFactoryBean schedulerFactoryBean;
    //删除jobdeilter
    public  void deleteJob(String jobName,String JobGropName,String triggerName,String triggerGroupName){
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName, JobGropName);
        Scheduler scheduler = schedulerFactoryBean.getObject();
        Trigger trigger = null;
        try {
            trigger = scheduler.getTrigger(triggerKey);
            if(trigger == null){
                System.out.println("没有trigger......");
                return;
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        try {
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey); // 移除触发器
            scheduler.deleteJob(jobKey); // 删除任务
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
    //暂停一个任务
    public  void  stopJob(String jobName,String JobGropName){
        try {
            JobKey jobKey = new JobKey(jobName,JobGropName);

            schedulerFactoryBean.getObject().pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    //恢复一个任务
    public  void  resumeJob(String jobName,String JobGropName){

        JobKey jobKey = new JobKey(jobName,JobGropName);
        try {
            schedulerFactoryBean.getObject().resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    //启动所有的定时任务
    public void  startAllJob(){
        try {
            schedulerFactoryBean.getObject().start();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    public  boolean isStarted(){
        boolean pan = true;

        Scheduler scheduler =schedulerFactoryBean.getObject();
        try {
            pan = scheduler.isStarted();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return  pan;
    }
    //判断schedulerfactory的状态
    public boolean isShutduow(){
        boolean pan = true;
        try {
            pan =  schedulerFactoryBean.getObject().isShutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return  pan;
    }
    // 暂停调度器
    public void stopScheduler(){
        try {
            schedulerFactoryBean.getObject().standby();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    //关闭调度器
    public void closeScheduler(){

        try {
            schedulerFactoryBean.getObject().shutdown(true);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
    /*获取任务状态
	 * 		NONE: 不存在
	 * 		NORMAL: 正常
	 * 		PAUSED: 暂停
	 * 		COMPLETE:完成
	 * 		ERROR : 错误
	 * 		BLOCKED : 阻塞
	 */
    public  String getTriggerState(String tirggerName,String tirggerGroup){
        TriggerKey triggerKey = TriggerKey.triggerKey(tirggerName, tirggerGroup);
        Scheduler scheduler = schedulerFactoryBean.getObject();
        String name = null;
        try {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            name = triggerState.name();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return name;
    }

    //修改任务的定时时间
    public void  updateCronTime(String tirggerName,String tirggerGroup, String newCron){

        TriggerKey key = TriggerKey.triggerKey(tirggerName,tirggerGroup);
        try {
            CronTrigger trigger = (CronTrigger) schedulerFactoryBean.getObject().getTrigger(key);
          String odrTime =  trigger.getCronExpression();
          if(!odrTime.equals(newCron)){
              CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(newCron);

              // 按新的cronExpression表达式重新构建trigger
              trigger = trigger.getTriggerBuilder().withIdentity(key).withSchedule(scheduleBuilder).build();
              // 按新的trigger重新设置job执行
              schedulerFactoryBean.getObject().rescheduleJob(key, trigger);

          }else{

          }

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
    //从数据库中得到当前cron的值
    public String getCron(String tirggerName,String tirggerGroupName){
        TriggerKey triggerKey = TriggerKey.triggerKey(tirggerName, tirggerGroupName);
        Scheduler scheduler = schedulerFactoryBean.getObject();
        String name = null;
        try {
          CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
         name = trigger.getCronExpression();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return name;

    }
    // 添加一个任务
    public Object addJob(String jobName, String jobGroupName, String triggerName,String triggerGroupName, Class jobClass, String cron,String note, Object...objects){
        Scheduler scheduler = schedulerFactoryBean.getObject();
        JobDetail jobDetail=newJob()
                .ofType(jobClass) //引用Job Class
                .withIdentity(jobName, jobGroupName) //设置name/group
                .withDescription(note) //设置描述
                .build();

        //JobDetail jobDetail = newJob(jobClass).withDescription(note).withIdentity(jobName, jobGroupName).build();
       if(objects != null){
           for(int i =0;i<objects.length;i++){
               jobDetail.getJobDataMap().put("data"+i, objects[i]);
           }
       }

       TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName,jobGroupName);
        try {
            boolean flag = scheduler.checkExists(triggerKey);
            boolean flag2 = scheduler.checkExists(jobKey);
             if(flag || flag2){
                 return null;
             }
                TriggerBuilder<Trigger> triggerTriggerBuilder = TriggerBuilder.newTrigger();
                triggerTriggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerTriggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                 CronTrigger trigger  = (CronTrigger) triggerTriggerBuilder.build();
                try {
                    scheduler.scheduleJob(jobDetail,trigger);
                    //  scheduler.standby(); // 不会立即执行 挂起 ?
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }

        } catch (SchedulerException e) {
            e.printStackTrace();
        }




       //triggerTriggerBuilder.startNow();
       // triggerTriggerBuilder.endAt()
        return  "ok";

    }
    //获取全部任务
    public List<SchedulingJob> getAllJob(){
        Scheduler scheduler = schedulerFactoryBean.getObject();
        try {
            if(scheduler.isShutdown()){
                return null;
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        List<SchedulingJob> jobList = new ArrayList<>();
        try {
         List<String> list =   scheduler.getJobGroupNames();
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         for(String groupName : list){
             for(JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))){
                 if(jobKey.getGroup().equals("RECOVERING_JOBS")){
                     continue;
                 }
                List<Trigger> list1 = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                for (Trigger s : list1){
                    TriggerKey triggerKey =   s.getKey();
                    if(triggerKey.getGroup().equals("RECOVERING_JOBS")){
                        continue;
                    }

                    SchedulingJob var1 = new SchedulingJob();
                    var1.setJobName(jobKey.getName());
                    var1.setJobGroup(jobKey.getGroup());
                    var1.setTriggerName(triggerKey.getName());
                    var1.setTriggerGroup(triggerKey.getGroup());
                    var1.setStatus(getTriggerState(triggerKey.getName(),triggerKey.getGroup()));
                    var1.setCron(getCron(triggerKey.getName(),triggerKey.getGroup()));
                    var1.setJobNote(scheduler.getJobDetail(jobKey).getDescription());
                        var1.setUseTimeNext(sim.format(s.getNextFireTime()));
                        var1.setUseTimeAgo(s.getPreviousFireTime() == null ? "" : sim.format(s.getPreviousFireTime()));


                    var1.setSchedulingForString(scheduler.getJobDetail(jobKey).getJobClass().getName());
                    jobList.add(var1);


                }
             }

         }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return  jobList;
    }
    //暂停全部任务
    public void stopAllJob(){
        Scheduler scheduler = schedulerFactoryBean.getObject();

        try {
            List<String> list =   scheduler.getJobGroupNames();
            for(String groupName : list){
                for(JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))){
                    if(jobKey.getGroup().equals("RECOVERING_JOBS")){
                        continue;
                    }
                    String jobName = jobKey.getName();
                    String jobgroupName = jobKey.getGroup();
                    List<Trigger> list1 = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    for (Trigger s : list1){
                        TriggerKey triggerKey =   s.getKey();
                        if(triggerKey.getGroup().equals("RECOVERING_JOBS")){
                            continue;
                        }
                        //得到trigger的状态
                        String status = getTriggerState(triggerKey.getName(),triggerKey.getGroup());
                        if(!status.equals("PAUSED")){
                            stopJob(jobName,jobgroupName);
                        }
                    }
                }

            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
