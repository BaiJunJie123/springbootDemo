package com.demo.form;

import com.demo.model.Job;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobForm {

	private Double money;
	private Double Bossmoney;
	private int number;
	private String jobName;
	private String jobUrl;
	private int JobType;

	public Job toJob(){
		Job job = new Job();
		job.setMoney(money);
		job.setUrl(jobUrl);
		job.setType(JobType);
		job.setName(jobName);
		job.setBossMoney(Bossmoney);
		job.setNumber(number);
		job.setOkNumber(number);
		return job;
	}
}


