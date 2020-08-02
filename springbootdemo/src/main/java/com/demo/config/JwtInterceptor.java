package com.demo.config;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class JwtInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
		// 从 http 请求头中取出 token
		String token = httpServletRequest.getHeader("Authorization");

		// 如果不是映射到方法直接通过
		if(!(object instanceof HandlerMethod)){
			return true;
		}
		HandlerMethod handlerMethod=(HandlerMethod)object;
		Method method=handlerMethod.getMethod();

		// OPTIONS请求类型直接返回不处理
		if ("OPTIONS".equals(httpServletRequest.getMethod())){
			return false;
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest,
						   HttpServletResponse httpServletResponse,
						   Object o, ModelAndView modelAndView) throws Exception {

	}
	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest,
								HttpServletResponse httpServletResponse,
								Object o, Exception e) throws Exception {
	}

}
