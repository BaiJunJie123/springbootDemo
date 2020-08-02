package com.demo.entity;

import java.util.Date;

/**
 * @author 白俊杰
 * @Date 2019/12/26
 * @Description
 **/
public class SchedulingJob {

    private String jobName;
    private String jobGroup;
    private String jobNote;
    private String schedulingForString;
    private String cron;
    private String useTimeAgo;
    private String useTimeNext;
    private String status;
    private String TriggerName;
    private String TriggerGroup;

    public SchedulingJob() {
    }

    public String getTriggerName() {
        return TriggerName;
    }

    public void setTriggerName(String triggerName) {
        TriggerName = triggerName;
    }

    public String getTriggerGroup() {
        return TriggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        TriggerGroup = triggerGroup;
    }

    public SchedulingJob(String jobName, String jobGroup, String jobNote, String schedulingForString, String cron, String useTimeAgo, String useTimeNext, String status, String triggerName, String triggerGroup) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.jobNote = jobNote;
        this.schedulingForString = schedulingForString;
        this.cron = cron;
        this.useTimeAgo = useTimeAgo;
        this.useTimeNext = useTimeNext;
        this.status = status;
        TriggerName = triggerName;
        TriggerGroup = triggerGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobNote() {
        return jobNote;
    }

    public void setJobNote(String jobNote) {
        this.jobNote = jobNote;
    }

    public String getSchedulingForString() {
        return schedulingForString;
    }

    public void setSchedulingForString(String schedulingForString) {
        this.schedulingForString = schedulingForString;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUseTimeAgo() {
        return useTimeAgo;
    }

    public void setUseTimeAgo(String useTimeAgo) {
        this.useTimeAgo = useTimeAgo;
    }

    public String getUseTimeNext() {
        return useTimeNext;
    }

    public void setUseTimeNext(String useTimeNext) {
        this.useTimeNext = useTimeNext;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
