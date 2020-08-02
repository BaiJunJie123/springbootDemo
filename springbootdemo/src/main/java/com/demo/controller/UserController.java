package com.demo.controller;

import com.demo.form.MoneyTiXianForm;
import com.demo.form.OrderCheckForm;
import com.demo.form.ShenheForm;
import com.demo.form.UserForm;
import com.demo.http.Response;
import com.demo.model.Order;
import com.demo.model.User;
import com.demo.repository.FinishJobRepository;
import com.demo.repository.OrderRepository;
import com.demo.security.JwtTokenUtil;
import com.demo.service.TixianService;
import com.demo.service.UserSerive;
import com.demo.util.Auth;
import com.demo.util.Page;
import com.demo.util.RedisUtils;
import com.demo.util.Sms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class UserController extends BaseController {

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserSerive userSerive;

	@Autowired
	private RedisUtils redisUtil;

	@Autowired
	private TixianService tixianService;

	@Autowired
	private FinishJobRepository finishJobRepository;

	@Autowired
	private OrderRepository orderRepository;


	/***
	 * 用户余额体现
	 * @param moneyTiXianForm
	 * @return
	 */
	@PostMapping("user/tixian")
	public Response tiXianMoney(@RequestBody MoneyTiXianForm moneyTiXianForm) {
		// 用户减去相应余额
		// 添加体现记录到体现表
		// 管理员审核 通过则打款,不通过则退回体现余额到用户账户
		Response response;
		if (moneyTiXianForm == null) {
			response = new Response().NO();
			throw new RuntimeException("参数类不能为空");
		}
		userSerive.updateUserMoney(moneyTiXianForm);
		response = new Response().OK();
		return response;
	}

	/***
	 * 查用户总收入
	 */
	@GetMapping("user/all/count")
	public Response allMoney() {
		Response response = new Response();
		Double money = tixianService.getUserAllMoney();
		response.setData(money);
		return response;
	}

	/***
	 * 查用户上级
	 */
	@GetMapping("user/user/UpId")
	public Response findUpId() {
		Response response = new Response();
		User user = userSerive.findUserUpId();
		response.setData(user);
		return response;
	}

	/**
	 * 退出登录
	 *
	 * @param
	 * @return
	 */
	@GetMapping("signout")
	public Response<String> signout() {
		//redisUtil.evict("jobNum_" + Auth.userName);
		return new Response().OK();
	}


	/***
	 * 进入用户主页
	 * @return
	 */
	@GetMapping("user/info")
	public Response<User> index() {
		// 查与用户信息
		// 查余额
		// 查
		Response<User> response = new Response<>();
		User user = userSerive.findUserForId();
		Object num = redisUtil.getforString("jobNum_" + Auth.userName);
		if (num != null) {
			response.setMsg(num.toString());
		} else {
			redisUtil.puts("jobNum_" + Auth.userName,user.getJobNum(),86400L);
			response.setMsg(String.valueOf(user.getJobNum()));
		}
		response.setData(user);
		return response;
	}

	/***
	 * 用户登录
	 * @param user
	 * @return
	 */
	@PostMapping("login")
	public Response<String> login(@RequestBody User user) {
		Response<String> response = new Response<>();
		try {
			final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
			if (userDetails != null && userDetails.getPassword().equals(user.getPassword())) {
				final String token = jwtTokenUtil.generateToken(userDetails);
				response.setCodes(200);
				response.setData(token);
			} else {
				response.setCodes(403);
				response.setMsg("用户名或密码错误");
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			response.setCodes(403);
			response.setMsg("用户不存在或密码错误");
			return response;
		}
	}

	/***
	 * 用户注册
	 * @param userForm
	 * @return
	 */
	@PostMapping("register")
	public Response<String> register(@RequestBody UserForm userForm) {
		Response response = new Response();
		String code = userForm.getCode();
		if (code.isEmpty()) {
			response.setCodes(404);
			throw new RuntimeException("验证码不能为null");
		}
		if (redisUtil.get(userForm.getUserName()) == null) {
			response.setCodes(404);
			throw new RuntimeException("验证码已经过期");
		}
		if (redisUtil.getforString(userForm.getUserName()).toString().equals(code)) {
			response.setCodes(200);
			userSerive.save(userForm.toUser());
		}
		return response;
	}

	/***
	 * 找回密码 / 忘记密码
	 *
	 */
	@PostMapping("targerPassWord")
	public Response<String> targerPassWord(@RequestBody UserForm userForm) {
		Response<String> response = new Response<>();
		// 进去之前先发送验证码
		// 然后得到验证码
		if (userForm == null) {
			response.setCodes(500);
			response.setMsg("信息不完善");
			return response;
		}
		if (redisUtil.getforString(userForm.getUserName()).equals(userForm.getCode())) {
			// 1 根据手机号找到该用户
			User user = userSerive.findUser(userForm);
			if (user == null) {
				response.setCodes(500);
				response.setMsg("没有此用户");
				return response;
			}
			user.setPassword(userForm.getPassword());
			user.setUpdateDate(new Date());
			userSerive.updateUser(user);
			response.setCodes(200);
			response.setMsg("新密码设置成功！");
			return response;
		} else {
			response.setCodes(500);
			response.setMsg("验证码已经过期");
		}
		return response;

	}

	/***
	 * 发送验证码 通过手机号
	 * @return
	 */
	@GetMapping("getCode")
	public Response<String> getCodeForPhone(@RequestParam("phone") String phone) {
		Response<String> response = new Response<>();
		if (phone == null || phone.length() != 11) {
			response.setMsg("手机号格式不正确");
			response.setCodes(500);
			return response;
		}
		int code = (int) ((Math.random() * 9 + 1) * 1000);
		if (Sms.sendCode(phone, code).equals("OK")) {
			redisUtil.puts(phone, String.valueOf(code), 300L);
			return new Response(String.valueOf(code));
		} else {
			response.setMsg("验证码服务器错误");
			response.setCodes(500);
			return response;
		}

	}

	/***
	 * admin 用户审核任务  查出所有用户所作的任务 分页
	 * @param
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("user/checkJob/find/{curentPageIndex}")
	public Response<Page> checkJobFind(@PathVariable("curentPageIndex") int curentPageIndex) {
		// 查出所有用户所作的任务 分页
		int count = finishJobRepository.findFinishJob(0);
		Page<Map<String, Object>> page = new Page<>(count, curentPageIndex);
		List<Map<String, Object>> list = finishJobRepository.findcheckJobPage(0, page.getLimitStart(), page.getCountPerpage());
		page.setList(list);
		return new Response(page);
	}

	/***
	 * 审核某一个个任务
	 * @param
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("user/checkJob/check")
	public Response checkJob(@RequestBody ShenheForm shenheForm) {
		if (shenheForm == null) {
			return new Response().NO();
		}
		userSerive.checkJob(shenheForm);
		return new Response().OK();
	}

	/***
	 * admin 用户审核充值  查出所有用户充值 分页
	 * @param
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("user/chongzhi/find/{curentPageIndex}")
	public Response<Page> checkchongzhiFind(@PathVariable("curentPageIndex") int curentPageIndex) {
		// 查出所有用户所作的任务 分页
		int count = orderRepository.findcheckOrderCount(0);
		Page<Order> page = new Page<>(count, curentPageIndex);
		List<Order> list = orderRepository.findAllOrdercheck(0, page.getLimitStart(), page.getCountPerpage());
		page.setList(list);
		return new Response(page);
	}

	/***
	 * 审核某个充值
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("user/checkChognzhi/chongzhi")
	public Response checkChognzhi(@RequestBody OrderCheckForm orderCheckForm) {
		if (orderCheckForm == null) {
			return new Response().NO();
		}
		userSerive.checkOrder(orderCheckForm);
		return new Response().OK();
	}


	/***
	 * 审核全部任务
	 * @param
	 * @return
	 */
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("user/checkJob/checkAll")
	public Response checkJobAll() {
		// 查出所有用户所作的任务 分页
		return null;
	}


	@GetMapping("user/findCountUser/{username}")
	public Response<String> findCountUser(@PathVariable("username") String username) {
		if (userSerive.findCountUser(username) == 0) {
			return new Response().OK();
		} else {
			return new Response().NO();
		}
	}
}
