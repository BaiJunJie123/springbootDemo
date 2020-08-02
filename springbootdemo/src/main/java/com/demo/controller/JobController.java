package com.demo.controller;

import com.demo.form.JobForm;
import com.demo.form.OrderCheckForm;
import com.demo.http.Response;
import com.demo.model.Job;
import com.demo.repository.JobRepository;
import com.demo.util.Auth;
import com.demo.util.Page;
import com.demo.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobController extends BaseController {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private RedisUtils redisUtil;

	/***
	 *
	 * @param curentPageIndex 当前页
	 * @param type 任务类型
	 * @return Response(page) 分页类
	 */
	@GetMapping("job/findListByPage/{type}/{curentPageIndex}")
	public Response<Page> findListByPage(@PathVariable("curentPageIndex") int curentPageIndex, @PathVariable("type") int type) {
		// 当前任务类型的总页
		int recordCount = jobRepository.findCount(type);
		Page page = new Page<Job>(recordCount, curentPageIndex);
		page.setList(jobRepository.findAllByPage(type, page.getLimitStart(), page.getCountPerpage()));
		return new Response(page);
	}

	/***
	 * 是否还可以继续再做任务
	 * @return
	 */
	@GetMapping("job/isOk")
	public Response<String> isOk() {
		Object obj = redisUtil.getforString("jobNum_" + Auth.userName);
		if (obj == null) {
			redisUtil.puts("jobNum_" + Auth.userName, Auth.jobNum, 86400L); // 24H
		}
		if (obj != null && Integer.parseInt(obj.toString()) != 0) {
			return new Response<String>().OK();
		} else {
			return new Response<String>().NO();
		}
	}

	/**
	 * 管理员发布任务
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("job/admin/save")
	public Response checkChognzhi(@RequestBody JobForm jobForm) {
		if (jobForm == null) {
			return new Response().NO();
		}
		Job job = jobForm.toJob();
		jobRepository.save(job);
		return new Response().OK();
	}
}
