package com.demo.service;

import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.RoleRepository;
import com.demo.repository.UserRepository;
import com.demo.security.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * 通过这个来查用户信息 根据手机号，/id
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = userRepository.findTopByUsername(s);
		if (user == null) {
			throw new UsernameNotFoundException("User " + s + " was not found in db");
			//这里找不到必须抛异常
		}
		JwtUser jwtUser = getDetails(user);
		return jwtUser;
	}

	public JwtUser loadUserById(int id) {
		User user = userRepository.findOneById(id);
		if (user != null) {
			return getDetails(user);
		}
		return null;

	}


	private JwtUser getDetails(User user) {
		List<Role> list = roleRepository.findAllByUserId(user.getId());
		user.setRoles(list);
		return new JwtUser(user);
	}
}
