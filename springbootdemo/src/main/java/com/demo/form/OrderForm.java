package com.demo.form;

import com.demo.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderForm {

	private int vipType;
	private Double money;

	public Order toOrder(){

		Order order = new Order();
		order.setVipType(vipType);
		order.setMoney(money);
		return  order;
	}

}
