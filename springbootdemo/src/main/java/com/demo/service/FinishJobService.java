package com.demo.service;

import com.demo.model.FinishJob;
import com.demo.repository.FinishJobRepository;
import com.demo.util.Auth;
import com.demo.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinishJobService {

	@Autowired
	private FinishJobRepository finishJobRepository;


	public List<Integer> findFileIdBystatus(){
		return finishJobRepository.findFileIdByFindishJob();
	}

	/***
	 *  添加一个任务  添加到当前用户的已做任务表
	 * @param jobId int  任务id
	 * @param jobType int  任务类型
	 * @return int
	 */
	public int addFinishJob(int jobId, int jobType,int fileId) {
		if (jobId == 0) {
			throw new RuntimeException("jobdId 不能为0");
		}
		final FinishJob finishJob = new FinishJob();
		finishJob.setCheckStatus(0);
		finishJob.setJobId(jobId);
		finishJob.setUserId(Auth.id);
		finishJob.setJobType(jobType);
		finishJob.setFileId(fileId);
		if (finishJobRepository.save(finishJob) == null) {
			throw new RuntimeException("添加到当前用户的已做任务表失败！");
		} else {
			return 1;
		}
	}

	/***
	 * 按类型和用户id查询审核列表
	 * @return
	 */
	public List<FinishJob> findFinishJob(int jobType, int userId) {
		return finishJobRepository.findAllByJobTypeAndUserId(jobType, userId);
	}

	/***
	 * 查总行 按类型和用户id查询审核列表
	 */
	public int findCount(int checkstatus, int userId){
		return  finishJobRepository.findCount(checkstatus,userId);
	}

	/***
	 * 分页查询 按类型和用户id查询审核列表
	 */
	public List<FinishJob> findFinishJobByPage(int checkstatus, int userId, int limitStart, int countPerpage) {
		return finishJobRepository.findAllByPage(checkstatus, userId, limitStart, countPerpage);
	}
}
