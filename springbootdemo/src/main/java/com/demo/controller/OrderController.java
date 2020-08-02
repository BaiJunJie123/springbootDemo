package com.demo.controller;

import com.demo.form.OrderForm;
import com.demo.http.Response;
import com.demo.model.Order;
import com.demo.repository.OrderRepository;
import com.demo.util.Auth;
import org.apache.http.auth.AUTH;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController extends BaseController {

	@Autowired
	private OrderRepository orderRepository;

	@PostMapping("order/ordersave")
	public Response store(@RequestBody OrderForm orderForm) {
		if (orderForm != null) {
			Order order = orderForm.toOrder();
			order.setUserId(Auth.id);
			order.setUserName(Auth.userName);
			order.setCheckStatus(0);
			orderRepository.save(order);
			return new Response().OK();
		}
		return new Response().NO();
	}

	@GetMapping("order/orderInfo")
	public Response<List<Order>> orderInfo() {

		return new Response(orderRepository.findAllByUserIdinfo(Auth.id));
	}
}
