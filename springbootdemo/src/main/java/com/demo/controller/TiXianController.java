package com.demo.controller;

import com.demo.form.TiXianCheckAdmin;
import com.demo.http.Response;
import com.demo.model.Tixian;
import com.demo.service.TixianService;
import com.demo.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TiXianController extends BaseController {

	@Autowired
	private TixianService tixianService;

	@GetMapping("tixian/alltixianinfo")
	public Response<List<Object>> alltixianinfo(){
		return new Response(tixianService.findUserTiXianInfo());
	}

	/***
	 * 提现审核 -- 用户的全部提现申请 page 分页
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("tixian/check/allinfo/{curentPageIndex}")
	public Response<Page> tixinaCheckPage(@PathVariable("curentPageIndex") int curentPageIndex){
       // 查总条数
		int count = tixianService.findAllUserTiXianPageCount(0);
		Page<Tixian> tixianPage = new Page<>(count,curentPageIndex);
		List<Tixian> tixians = tixianService.findAllUserTiXianPage(0,tixianPage.getLimitStart(),tixianPage.getCountPerpage());
		tixianPage.setList(tixians);
		return  new Response(tixianPage);
	}

	/***
	 * 管理员审核 提现
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("tixian/check/admin")
	public Response  adminTiXiancheck(@RequestBody TiXianCheckAdmin tiXianCheckAdmin){
		// 1 通过 把当前提现状态改为 1 已打款 mall_tixian
		// 2 驳回  提现金额添加到mall_user 当前用户的money中 把当前提现状态改为 2 已驳回 mall_tixian
		tixianService.adminCheckTiXian(tiXianCheckAdmin);
		return new Response().OK();
	}

}
