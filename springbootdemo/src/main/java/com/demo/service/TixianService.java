package com.demo.service;

import com.demo.form.TiXianCheckAdmin;
import com.demo.model.Tixian;
import com.demo.model.User;
import com.demo.repository.TixianRepository;
import com.demo.repository.UserRepository;
import com.demo.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TixianService {

	@Autowired
	private TixianRepository tixianRepository;

	@Autowired
	private UserRepository userRepository;


	/***
	 * 管理员审核提现
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void adminCheckTiXian(TiXianCheckAdmin tiXianCheckAdmin) {
		if (tiXianCheckAdmin.getId() != 0 && tiXianCheckAdmin.getUserId() != 0 && tiXianCheckAdmin.getMoney() != 0) {
			// 1 通过 把当前提现状态改为 1 已打款 mall_tixian
			// 2 驳回  提现金额添加到mall_user 当前用户的money中 把当前提现状态改为 2 已驳回 mall_tixian
			Tixian tixian = tixianRepository.getOne(tiXianCheckAdmin.getId());
			if (tiXianCheckAdmin.getCheckStatus() == 1) {
				tixian.setCheckStatus(1);
				tixian.setUpdateAt(new Date());
				tixianRepository.save(tixian);
			} else if (tiXianCheckAdmin.getCheckStatus() == 2) {
				// 驳回
				User user = userRepository.getOne(tiXianCheckAdmin.getUserId());
				Double oldMoney = user.getMoney();
				Double newMoney = oldMoney+tiXianCheckAdmin.getMoney();
				user.setUpdateDate(new Date());
				user.setMoney(newMoney);
				userRepository.save(user);
				tixian.setCheckStatus(2);
				tixian.setUpdateAt(new Date());
				tixianRepository.save(tixian);
			} else {
				log.info("TixianService:提现审核参数错误" + tiXianCheckAdmin.getCheckStatus());
				throw new RuntimeException("提现审核参数错误" + tiXianCheckAdmin.getCheckStatus());
			}
		} else {
			log.info("TixianService:提现审核参数不全" + tiXianCheckAdmin);
			throw new RuntimeException("提现审核参数不全");
		}
	}

	/***
	 * 用户总收入
	 * @return
	 */
	public Double getUserAllMoney() {
		return tixianRepository.findAllUserMoney(Auth.id);
	}

	/***
	 * 用户提现记录
	 *
	 */
	public List<Object> findUserTiXianInfo() {
		return tixianRepository.findUserTiXianInfo(Auth.id);
	}

	/***
	 * 管理员审核提现  数据分页
	 */
	public List<Tixian> findAllUserTiXianPage(int checkStatus, int limitStart, int countPerpage) {
		return tixianRepository.findAllUserTiXianPage(checkStatus, limitStart, countPerpage);
	}

	/**
	 * 管理员审核提现  总条数
	 */
	public int findAllUserTiXianPageCount(int checkStatus) {
		return tixianRepository.findAllUserTiXianPageCount(checkStatus);
	}
}
