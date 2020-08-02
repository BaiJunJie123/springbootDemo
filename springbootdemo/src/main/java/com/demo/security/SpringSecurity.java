package com.demo.security;

import com.demo.service.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurity extends WebSecurityConfigurerAdapter {

	// 没有凭证
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.and()
				.authorizeRequests()
				.antMatchers("/mall/login").permitAll() //登录
				.antMatchers("/mall/register").permitAll() //注册
				.antMatchers("/mall/getCode").permitAll()
				.antMatchers("/mall/targerPassWord").permitAll()
				.antMatchers("/mall/user/findCountUser/*").permitAll() // 判断手机号是否注册
				.antMatchers(HttpMethod.OPTIONS, "/mall/**").anonymous()
				.anyRequest().authenticated()    // 剩下所有的验证都需要验证
				.and()
				.logout().logoutUrl("/mall/signout").disable()
				.csrf().disable()           // 禁用 Spring Security 自带的跨域处理
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// 定制我们自己的 session 策略：调整为让 Spring Security 不创建和使用 session
		http.addFilterBefore(jwtAuthorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

		// 禁用缓存
		http.headers().cacheControl();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
