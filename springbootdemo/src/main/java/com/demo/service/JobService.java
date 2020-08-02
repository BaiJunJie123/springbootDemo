package com.demo.service;

import com.demo.model.Job;
import com.demo.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

	@Autowired
	private JobRepository jobRepository;

	/***
	 *  1 当前任务减库存
	 * @param jobId
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void jianJobokNumberByjobId(int jobId) {
		int id = 0;
		final Job job = jobRepository.getOne(jobId);
		if (job != null) {
			int newOkNumber = job.getOkNumber() - 1;
			if (jobRepository.updateByjJobId(newOkNumber, job.getVersion(), jobId) > 0) {
				id = 1;
			}else{
				id = 0;
			}

		}

		if(id == 0){
			throw  new RuntimeException("当前任务减库存失败！");
		}
	}
}
