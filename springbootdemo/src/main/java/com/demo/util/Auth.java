package com.demo.util;

import com.demo.security.JwtUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class Auth {

	public static JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	public static  int id = jwtUser.getId();

	public static String userName = jwtUser.getUsername();

	public static int isAdmin = jwtUser.getIsAdmin();

	public static int vipType = jwtUser.getVipType();

	public static int parentId = jwtUser.getParentId();

	public static int jobNum = jwtUser.getJobNum();

}
