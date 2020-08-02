package com.demo.security;

import com.demo.model.Role;
import com.demo.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class JwtUser implements UserDetails {

	private Integer id;
	private String username;
	private String password;
	private int isAdmin;
	private int vipType;
	private int parentId;
	private int jobNum;
	private Collection<GrantedAuthority> authorities;

	public JwtUser() {
	}

	// 写一个能直接使用user创建jwtUser的构造器
	public JwtUser(User user) {
		id = user.getId();
		username = user.getUsername();
		password = user.getPassword();
		isAdmin = user.getIsAdmin();
		vipType = user.getVipType();
		jobNum = user.getJobNum();
		parentId = user.getParentId();
		authorities = new ArrayList<>();
		for (Role s : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(s.getAuthority()));
		}

	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	public int getParentId() {
		return parentId;
	}

	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	public String getUserPhone() {
		return username;
	}

	public int getJobNum() {
		return jobNum;
	}

	public Integer getId() {
		return this.id;
	}

	public int getIsAdmin() {
		return isAdmin;
	}

	public int getVipType() {
		return vipType;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "JwtUser{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", authorities=" + authorities +
				'}';
	}

}