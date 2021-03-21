package com.module.system.user.controller;

import com.module.system.user.service.ApiService;
import com.util.ControllerBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;


@Controller
public class UserWebController extends ControllerBase {
	@Autowired
	ApiService apiService;
    /**
     * 请求用户管理页面
	 * @param model
     * @return
     */
	@RequestMapping(value = "/UserManagement")
	public String UserManagement(Model model){
		return "/sys/UserManagement";
	}

	@RequestMapping(value = "/UserUpdate")
	public String UserUpdate(Model model){
		return "/sys/UserUpdate";
	}

	/**
	 * 消息列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/Message/index")
	public String Message(Model model){
		apiService.Receiving();
		Map map=apiService.getNumber_of_unread_messages();
		if (!isnull(map.get("allNum"))){
			model.addAttribute("allNum",map.get("allNum"));
		}
		if (!isnull(map.get("noticeNum"))){
			model.addAttribute("noticeNum",map.get("noticeNum"));
		}
		if (!isnull(map.get("newsNum"))){
			model.addAttribute("newsNum",map.get("newsNum"));
		}
		return "/message/index";
	}

	/**
	 * 消息显示
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/Message/detail")
	public String Detail(String id,Model model){
		Map map=apiService.getNews(id);
		if (map.get("code_type").equals("0")){
			Map data=(Map) map.get("data");
			model.addAttribute("title",data.get("title"));
			model.addAttribute("time",data.get("time"));
			model.addAttribute("content",data.get("content"));
			model.addAttribute("Publisher","管理员");
		}
		System.out.println(toJson(map));
		return "/message/detail";
	}

	/**
	 * 用户信息
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/info")
	public String userinfo(Model model){
		return "/user/info";
	}

	/**
	 * 微信绑定
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/wxbinding")
	public String wxbinding(Model model){
		return "/user/wxbinding";
	}

	/**
	 * 修改密码
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/password")
	public String password(Model model){
		return "/user/password";
	}

	/**
	 * 修改操作密码
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/operationPwd")
	public String operationPwd(Model model){
		return "/user/operationPwd";
	}
}
