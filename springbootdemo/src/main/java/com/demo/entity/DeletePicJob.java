package com.demo.entity;

import com.demo.model.File;
import com.demo.repository.FileRepository;
import com.demo.repository.FinishJobRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 白俊杰
 * @Date 2019/11/30
 * @Description
 **/
@PersistJobDataAfterExecution //多次调用job 确保对Job的持久化 保存一个数据信息  即有状态任务
@DisallowConcurrentExecution  //确保当前任务完再执行下一个任务
@Component
public class DeletePicJob extends QuartzJobBean {

	@Autowired
	private FinishJobRepository finishJobRepository;

	@Autowired
	private FileRepository fileRepository;

	/***
	 * 此处执行任务
	 * @param jobExecutionContext
	 * @throws JobExecutionException
	 */
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		List<Integer> files = finishJobRepository.findFileIdByFindishJob();
		List<Long> ids = new ArrayList<>();
		if (files.size() > 0) {
			for (Integer s : files) {
				ids.add(Long.valueOf(s));
			}
			List<File> file = fileRepository.findAllByIdIn(ids);
			for (File f : file) {
				java.io.File dfile = new java.io.File(f.getPath());
				if (dfile.exists()) {
					dfile.delete();
				}
				if (dfile.getParentFile() != null && dfile.getParentFile().length() == 0) {
					java.io.File ffile = dfile.getParentFile();
					ffile.delete();
				}
			}
		}
		System.out.println("======每小时执行一次============");
	}
}
