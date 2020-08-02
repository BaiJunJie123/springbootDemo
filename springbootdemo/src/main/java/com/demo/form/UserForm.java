package com.demo.form;

import com.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
	private String userName;
	private String password;
	private String code;
	private int parentId;

	public User toUser(){
		User user = new User();
		user.setUsername(userName);
		user.setPassword(password);
		user.setParentId(parentId);
		return  user;
	}

}
