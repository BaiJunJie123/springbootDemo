package com.demo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCheckForm {

	private int userId;
	private int vipType;
	private int orderId;
	private int checkStatus;
	private String userName;

}
