package com.demo.config;

import com.demo.entity.DeletePicJob;
import com.demo.util.QuartzUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BeforeModel  implements ApplicationRunner {

	@Autowired
	private QuartzUtils quartzUtils;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		/*获取任务状态
		 * 		NONE: 不存在
		 * 		NORMAL: 正常
		 * 		PAUSED: 暂停
		 * 		COMPLETE:完成
		 * 		ERROR : 错误
		 * 		BLOCKED : 阻塞
		 */
		if(quartzUtils == null){
			throw new RuntimeException("quartzUtils 是 null的");
		}
		String status =quartzUtils.getTriggerState("cronDeletePic","cronPic");
		switch (status){
			case "NONE":
				quartzUtils.addJob("JobDeletePic", "JobPic", "cronDeletePic","cronPic", DeletePicJob.class, "0 0 * * * ? *","delete pic job", null);
				System.out.println("========================NONE");
				break;
			case "PAUSED":
				quartzUtils.resumeJob("JobDeletePic", "JobPic");
				System.out.println("========================PAUSED");
				break;
			case "ERROR":
				throw new RuntimeException("quartz任务中名称为：JobDeletePic的任务 状态为错误！");
			case "BLOCKED":
				System.out.println("========================BLOCKED");
				break;
			default:
				System.out.println("========================"+status);
				break;

		}
	}
}
