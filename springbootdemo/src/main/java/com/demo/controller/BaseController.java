package com.demo.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/mall/")
public class BaseController {


	@ModelAttribute
	public void common(HttpServletRequest request, HttpServletResponse response) {
		//执行公共的东西
	}
}
