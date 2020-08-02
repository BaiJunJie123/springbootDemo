package com.demo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShenheForm {

	private int id;
	private int typeStatus;
	private int userId;
	private Double money;
}
