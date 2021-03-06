package com.module.system.login.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.helei.api.common.ImplBase;
import com.module.common.CommonMethod;
import com.module.system.login.service.ApiService;
import com.helei.api.util.PwdUtil;
import com.helei.api.util.ReqToMapUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@RequestMapping(value = { "/","/login" })
@RestController
public class CommonRestController extends ImplBase {
	@Autowired
	private ApiService ApiService;
	@Autowired
	private CommonMethod commonMethod;
	/**
	 * 登录
	 * @param req
	 * @param resp
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Map<String, Object> login(HttpServletRequest req, HttpServletResponse resp, Model model) {
		return new HashMap() {
			{
				String un = req.getParameter("username");
				String pwd = PwdUtil.encodePwd(req.getParameter("password"));
				Boolean rememberMe= Boolean.valueOf(req.getParameter("rememberMe"));
				UsernamePasswordToken token = new UsernamePasswordToken(un, pwd,rememberMe);
				Subject subject = SecurityUtils.getSubject();
				try {
					subject.login(token);
					getLogger().info("对用户：[" + un + "]进行登录验证,验证通过!");
				} catch (UnknownAccountException e) {
					subject.logout();
					getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：未知账号");
					put("retcode", 0);
					put("status", "Erro");
					put("responseText", "用户名或密码错误,请重新登录");
				} catch (IncorrectCredentialsException e) {
					subject.logout();
					getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：密码错误");
					put("retcode", 0);
					put("status", "Erro");
					put("responseText", "用户名或密码错误,请重新登录");
				} catch (LockedAccountException e) {
					subject.logout();
					getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：账号被锁定");
					put("retcode", 0);
					put("status", "Erro");
					put("responseText", "该账户账号已锁定");
				} catch (ExcessiveAttemptsException e) {
					subject.logout();
					getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：错误次数过多");
					put("retcode", 0);
					put("status", "Erro");
					put("responseText", "该账户账号已锁定");
				} catch (AuthenticationException e) {
					subject.logout();
					getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：用户名或密码不正确");
					put("retcode", 0);
					put("status", "Erro");
					put("responseText", "用户名或密码错误,请重新登录");
				} catch (Exception e){
					subject.logout();
					getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：" + e.getMessage());
					put("retcode", 0);
					put("status", "Erro");
					put("responseText", "用户名或密码错误,请重新登录");
				}
				if (subject.isAuthenticated()) {
					put("retcode", 1);
					put("status", "ok");
					put("responseText", "登录成功<br /><br />欢迎回来");
					Map user = (Map)subject.getPrincipal();
					Object a=subject.getSession();
					req.getSession().setAttribute("user", user);
				}
			}
		};
	}

	/**
	 * 获取菜单
	 * @param req
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/menutree" })
	public Map<String, Object> menutree(HttpServletRequest req, Model model) throws Exception {
		Map user = (Map)(req.getSession().getAttribute("user"));
		return ApiService.queryMenuTree(user);
	}
	// 新增

	/**
	 * 退出登录
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Map logout() {
		return new HashMap() {
			{
				try {
					Subject subject = SecurityUtils.getSubject();
					subject.logout();
					put("code", "0");
					put("msg", "Success");
				}catch(Exception e) {
					put("code", "-1");
					put("msg", e.getMessage());
				}
			}
		};
	}

	/**
	 * 微信扫码注册绑定会员,普通注册会员
	 * @param req
	 * @param model
	 * @return Map
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Map Register(HttpServletRequest req, Model model){
		Map map=ReqToMapUtil.getParameterMap(req);
		return ApiService.register(map);
	}

	/**
	 * 微信扫码登录 绑定会员
	 * @param req
	 * @param resp
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/wxlogin", method = RequestMethod.POST)
	public Map<String, Object> wxlogin(HttpServletRequest req, HttpServletResponse resp, Model model) {
		Map map=ReqToMapUtil.getParameterMap(req);
		return  ApiService.wxlogin(map);
	}

	/**
	 * 获取用户头像
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getimg")
	public Map getimg(HttpServletRequest req, Model model) {

		return  ApiService.getimg(commonMethod.getuserinfo(new String[]{"BM"}));
	}
	@RequestMapping(value = "/getEmailCode",method = RequestMethod.POST)
	public Map getEmailCode(HttpServletRequest req){
		Map map=ReqToMapUtil.getParameterMap(req);
		return ApiService.getEmailCode(map);
	}
}
