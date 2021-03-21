package com.module.system.authority.controller;


import com.helei.api.common.ImplBase;
import com.helei.api.util.ReqToMapUtil;
import com.util.ControllerBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.awt.*;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@RequestMapping(value = "/authority")
@RestController
public class AuthorityRestController extends ImplBase {

	@Autowired
	private com.module.system.authority.service.ApiService ApiService;

	@RequestMapping(value = "/getUserGroup", method = RequestMethod.GET)
	public Map getUserGroup(HttpServletRequest req, Model model){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getUserGroup(map);
	}

	/**
	 * 获取菜单树
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getMenu", method = RequestMethod.POST)
	public Map getMenu(HttpServletRequest req, Model model){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getMenu(map);
	}

	/**
	 * 更新菜单权限
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/UpdateAuthority",method = RequestMethod.POST)
	public Map UpdateAuthority(HttpServletRequest req, Model model){
		Map map= ReqToMapUtil.getParameterMap(req);
		return   ApiService.UpdateAuthority(map);
	}
	/**
	 * 更新菜单权限
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getMenulist",method = RequestMethod.GET)
	public List getMenulist(HttpServletRequest req, Model model){
		return  ApiService.getMenulist();
	}

	/**
	 * 锁定菜单
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/setMenuLock",method = RequestMethod.POST)
	public Map setMenuLock(HttpServletRequest req, Model model){
			Map map= ReqToMapUtil.getParameterMap(req);
			return ApiService.setMenuLock(map);
	};

	/**
	 * 删除菜单
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/delMenu",method = RequestMethod.POST)
	public Map delMenu(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.delMenu(map);
	}

	/**
	 * 删除用户组
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/DeleteGroup",method = RequestMethod.POST)
	public Map DeleteAuthority(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.DeleteGroup(map);
	}

	/**
	 * 添加用户组
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/addGroup",method = RequestMethod.POST)
	public Map addGroup(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.addGroup(map);
	}
	/**
	 * 获取用户组信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getGroup",method = RequestMethod.POST)
	public Map getGroup(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getGroup(map);
	}
	/**
	 * 修改用户组信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/updateGroup",method = RequestMethod.POST)
	public Map updateGroup(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.updateGroup(map);
	}
	/**
	 * 锁定用户组
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/setGroupLock",method = RequestMethod.POST)
	public Map setGroupLock(HttpServletRequest req, Model model){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.setGroupLock(map);
	};

	/**
	 * 添加菜单
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/addMenu",method = RequestMethod.POST)
	public Map addMenu(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.addMenu(map);
	}

	/**
	 * 修改菜单
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/updateMenu",method = RequestMethod.POST)
	public Map updateMenu(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.updateMenu(map);
	}
	/**
	 * 获取所有父级菜单
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getParentMenu",method = RequestMethod.POST)
	public Map getParentMenu(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getParentMenu(map);
	}

	/**
	 * 获取所有用户组
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getUsergroupAll",method = RequestMethod.POST)
	public Map getUsergroupAll(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getUsergroupAll(map);
	}

	/**
	 * 获取菜单信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getMenuInfo",method = RequestMethod.POST)
	public Map getMenuInfo(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getMenuInfo(map);
	}

	/**
	 * 获取配置信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getConfig",method = RequestMethod.POST)
	public Map getConfig(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getConfig();
	}

	/**
	 * 获取配置信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getEmail",method = RequestMethod.GET)
	public Map getEmail(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getEmail(map);
	}
	/**
	 * 设置发件邮箱配置信息
	 * @param req map Host username password type
	 * @return map
	 */
	@RequestMapping(value = "/setupEmail",method = RequestMethod.POST)
	public Map setupEmail(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.setupEmail(map);
	}

	/**
	 * 根据ID 查询邮箱配置
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getEmailbyID",method = RequestMethod.POST)
	public Map getEmailbyID(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getEmailbyID(map);
	}

	/**
	 * 根据ID 删除邮箱配置
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/delEmail",method = RequestMethod.POST)
	public Map delEmail(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.delEmail(map);
	}

	/**
	 * 获取模板列表
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getEmailTemplate",method = RequestMethod.GET)
	public Map getEmailTemplate(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getEmailTemplate(map);
	}

	/**
	 * 获取模板内容
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getTemplatecontent",method = RequestMethod.POST)
	public Map getTemplatecontent(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getTemplatecontent(map);
	}

	/**
	 * 保存模板数据
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/preserve_Templatecontent",method = RequestMethod.POST)
	public Map preserve_Templatecontent(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.preserve_Templatecontent(map);
	}

	/**
	 * 删除模板
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/delete_Templatecontent",method = RequestMethod.POST)
	public Map delete_Templatecontent(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.delete_Templatecontent(map);
	}

	/**
	 * 解除表数据编辑锁定
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/delete_Templatecontent_lock",method = RequestMethod.POST)
	public Map delete_Templatecontent_lock(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.delete_Templatecontent_lock(map);
	}

	/**
	 * 启用模板
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/opt_Templatecontent",method = RequestMethod.POST)
	public Map opt_Templatecontent(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.opt_Templatecontent(map);
	}
	@RequestMapping(value = "/getTemplatecontentType",method = RequestMethod.POST)
	public Map getTemplatecontentType(HttpServletRequest req){
		Map map= ReqToMapUtil.getParameterMap(req);
		return ApiService.getTemplatecontentType(map);
	}

}
