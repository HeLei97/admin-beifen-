package com.module.system.authority.controller;

import com.module.system.authority.service.ApiService;
import com.util.ControllerBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class AuthorityWebController extends ControllerBase {
	@Autowired
	private ApiService apiService;
	/**
	 * 用户组管理
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/AuthorityManagement" }, method = RequestMethod.GET)
	public String AuthorityManagement(Model model){
		return "/sys/AuthorityManagement";
	}

	/**
	 * 菜单管理
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/MenuManagement" }, method = RequestMethod.GET)
	public String MenuManagement(Model model){

		return "/sys/MenuManagement";
	}

	/**
	 * 新增菜单
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/AddMenu",method = RequestMethod.GET)
	public String AddMenu(Model model){
		return "/sys/AddMenu";
	}

	/**
	 * 新增用户组
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/AddAuthority",method = RequestMethod.GET)
	public String AddAuthority(Model model){
		return "/sys/AddAuthority";
	}

	/**
	 * 系统配置管理
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/ConfigManagement",method = RequestMethod.GET)
	public String ConfigManagement(Model model){
		return "/sys/ConfigManagement";
	}

	/**
	 * 发件账号配置管理
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/EmailManagement",method = RequestMethod.GET)
	public String EmailManagement(Model model){
		return "/sys/EmailManagement";
	}

	/**
	 * 添加邮件,修改邮件
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/AddEmail",method = RequestMethod.GET)
	public String AddEmail(Model model){
		return "/sys/AddEmail";
	}

	/**
	 * 邮件模板设置
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/EmailTemplate",method = RequestMethod.GET)
	public String EmailTemplate(Model model){
		return "/sys/EmailTemplateManagement";
	}

	/**
	 * 邮件模板编辑
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/EditTemplate",method = RequestMethod.GET)
	public String EditTemplate(Model model){
		return "/sys/EditTemplate";
	}
}
