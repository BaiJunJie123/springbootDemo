package com.demo.controller;

import com.demo.http.Response;
import com.demo.model.FinishJob;
import com.demo.service.FinishJobService;
import com.demo.util.Auth;
import com.demo.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FinishJobController extends BaseController {

	@Autowired
	private FinishJobService finishJobService;

	@GetMapping("finishJob/{checkstatus}/{curentPageIndex}")
	public Response<Page> index(@PathVariable("checkstatus") int checkstatus, @PathVariable("curentPageIndex") int curentPageIndex) {
		final int countNum = finishJobService.findCount(checkstatus, curentPageIndex);
		Page<FinishJob> page = new Page<>(countNum, curentPageIndex);
		final List<FinishJob> finishJobList = finishJobService.findFinishJobByPage(checkstatus, Auth.id, page.getLimitStart(), page.getCountPerpage());
		page.setList(finishJobList);
		return new Response(page);
	}
}
