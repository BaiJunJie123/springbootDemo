package com.demo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/***
 * admin 提现审核form
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiXianCheckAdmin {

	private int id;

	private int userId;

	private Double money;

	private int checkStatus;
}
