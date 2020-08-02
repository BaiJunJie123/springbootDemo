package com.demo.service;

import com.demo.form.MoneyTiXianForm;
import com.demo.form.OrderCheckForm;
import com.demo.form.ShenheForm;
import com.demo.form.UserForm;
import com.demo.model.FinishJob;
import com.demo.model.Order;
import com.demo.model.Tixian;
import com.demo.model.User;
import com.demo.repository.FinishJobRepository;
import com.demo.repository.OrderRepository;
import com.demo.repository.TixianRepository;
import com.demo.repository.UserRepository;
import com.demo.util.Auth;
import com.demo.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSerive {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TixianRepository tixianRepository;

	@Autowired
	private FinishJobRepository finishJobRepository;

	@Autowired
	private RedisUtils redisUtil;

	@Autowired
	private OrderRepository orderRepository;

	private final static int isAdmin = 1; // 默认不是管理员

	@Value("${proxy.YK}")
	private int YK;
	@Value("${proxy.VIP}")
    private int VIP;

	@Value("${proxy.SVIP}")
	private int SVIP;

	@Value("${proxy.SSVIP}")
	private int SSVIP;

	public void save(User user) {
		if (user != null) {
			user.setIsAdmin(isAdmin);
			user.setMoney(0.00);
			user.setJobNum(YK);
			userRepository.saveAndFlush(user);
		}

	}

	/***
	 * 根据用户上级Id 获取上级用户
	 */

	public User findUserUpId() {
		return userRepository.findOneById(Auth.parentId);
	}

	/***
	 * 根据用户Id 获取User
	 */

	public User findUserForId() {
		return userRepository.findOneById(Auth.id);
	}

	/***
	 * 根据username find User
	 * @param userForm
	 * @return
	 */
	public User findUser(UserForm userForm) {
		return userRepository.findTopByUsername(userForm.getUserName());
	}

	/***
	 * 修改用户密码
	 */
	public void updateUser(User user) {
		userRepository.save(user);
	}

	/***
	 * 修改用户金额总调度
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateUserMoney(MoneyTiXianForm moneyTiXianForm) {
		// 用户减去相应余额
		// 添加体现记录到体现表
		// 管理员审核 通过则打款,不通过则退回体现余额到用户账户
		updateMoney(moneyTiXianForm.getMoney());
		Tixian tixian = new Tixian();
		tixian.setCheckStatus(0);
		tixian.setUserId(Auth.id);
		tixian.setUserName(Auth.userName);
		tixian.setZfbAccount(moneyTiXianForm.getZfbAccount());
		tixian.setMoney(moneyTiXianForm.getMoney());
		tixianRepository.save(tixian);
	}

	/***
	 *  用户体现 修改用户余额
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMoney(Double money) {
		User user = userRepository.getOne(Auth.id);
		if (user != null) {
			Double oldMoney = user.getMoney();
			Double newMoney = oldMoney - money;
			user.setMoney(newMoney);
			userRepository.save(user);
		} else {
			throw new RuntimeException("用户不存在");
		}
	}

	/***
	 * 审核一个任务
	 * @param
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void  checkJob(ShenheForm shenheForm){
		// 修改当前任务的审核状态
		if(shenheForm.getTypeStatus() == 1){
			FinishJob finishJob = finishJobRepository.getOne(shenheForm.getId());
			finishJob.setCheckStatus(shenheForm.getTypeStatus());
			finishJobRepository.save(finishJob);
			// 给用户余额添加金额
			User user = userRepository.getOne(shenheForm.getUserId());
			Double oldMoney = user.getMoney();
			Double newMoney = oldMoney+shenheForm.getMoney();
			user.setMoney(newMoney);
			userRepository.save(user);
		}else if(shenheForm.getTypeStatus() == 2){
			FinishJob finishJob = finishJobRepository.getOne(shenheForm.getId());
			finishJob.setCheckStatus(shenheForm.getTypeStatus());
			finishJobRepository.save(finishJob);
		}
	}

	/***
	 * 审核某个充值
	 * @param
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void  checkOrder(OrderCheckForm orderCheckForm){
		if(orderCheckForm.getCheckStatus() == 1){
			// 通过
			// 修改用户每天可做任务数 及 vip级别
			//修改redis 每日任务数
			// 修改订单状态
			User user = userRepository.getOne(orderCheckForm.getUserId());
			user.setVipType(orderCheckForm.getVipType());
			int newNum = 0;
			if(orderCheckForm.getVipType() == 1){
				newNum = VIP;
			}else if(orderCheckForm.getVipType() == 2){
				newNum = SVIP;
			}else if(orderCheckForm.getVipType() == 3){
				newNum = SSVIP;
			}
			user.setJobNum(newNum);
			userRepository.save(user);
			if(redisUtil.getforString("jobNum_" + orderCheckForm.getUserName()) == null ){
				redisUtil.puts("jobNum_" + orderCheckForm.getUserName(),newNum,86400L);
			}else{
				Long times = redisUtil.getKeyTime("jobNum_" + orderCheckForm.getUserName());
				redisUtil.puts("jobNum_" + orderCheckForm.getUserName(),newNum,times);
			}
			Order order = orderRepository.getOne(orderCheckForm.getOrderId());
			order.setCheckStatus(1);
			orderRepository.save(order);

		}else{
			// 驳回
			Order order = orderRepository.getOne(orderCheckForm.getOrderId());
			order.setCheckStatus(2);
			orderRepository.save(order);
		}
	}



	public int findCountUser(String username) {
		return userRepository.findCount(username);
	}
}
