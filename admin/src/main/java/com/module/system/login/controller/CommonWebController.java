package com.module.system.login.controller;

import com.shiro.ShiroSessionListener;
import com.util.ControllerBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller
public class CommonWebController extends ControllerBase {
	@Autowired
	ShiroSessionListener shiroSessionListener;
	/*
	 * 请求登录页面
	 */
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String login(Model model) {
		return "/login";
	}
	
	/**
	 * 首页
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/console" })
	public String console(Model model) {
		model.addAttribute("count",shiroSessionListener.getSessionCount());
		return "/console";
	}

	/**
	 * 请求主页
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/", "/index" })
	public String index(Model model) {
		return "/index";
	}

	/**
	 * 请求登录页面
	 * @return
	 */
	@RequestMapping(value ="/register")
	public String register(Model model){
		return "/register";
	}

	/**
	 * 404 报错页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/404" }, method = RequestMethod.GET)
	public String Error(Model model) {
		return "/404";
	}

	/**
	 * 踢出登录显示页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/kickout" }, method = RequestMethod.GET)
	public String kickout(Model model) {
		return "/kickout";
	}
}
