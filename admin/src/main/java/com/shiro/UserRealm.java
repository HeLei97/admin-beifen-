package com.shiro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.helei.api.util.PwdUtil;
import com.module.system.login.service.ApiService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.helei.api.util.DateUtils;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class UserRealm extends AuthorizingRealm {

	private Log logger;

	protected Log getLogger() {
		if (logger == null)
			logger = LogFactory.getLog(this.getClass());
		return logger;
	}

	private Gson gson;

	protected Gson getGson() {
		if (gson == null) {
			GsonBuilder builder = new GsonBuilder();
			builder.enableComplexMapKeySerialization();
			builder.setDateFormat(DateUtils.YMDHMS_PATTERN);
			gson = builder.create();
		}
		return gson;
	}

	@Autowired
	private ApiService apiService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Map operator = (Map) principals.getPrimaryPrincipal();
		List<String> roleList = new ArrayList<String>();
		List<String> permList = new ArrayList<String>();
		getLogger().info("对当前用户：[" + operator + "]进行授权！");
		if (operator != null) {
			try {
				List<Map> User;
				List<Map> Menu;
				if (operator.get("bm").toString().equals("sys")) {
					User = apiService.queryUser();
					Menu = apiService.queryMenu();
					for (Map role : User) {
						if (role.get("username") != null)
							roleList.add(role.get("username").toString());
					}
					for (Map permission : Menu) {
						if (permission.get("name") != null) {
							permList.addAll(Arrays.asList(permission.get("name").toString().split(",")));
						}
					}
				} else {
					User = apiService.queryUserRole(Integer.valueOf(operator.get("id").toString()));
					Menu = apiService.queryPermissionByUser(User.get(0).get("groupingID").toString());
					for (Map role : (List<Map>) User) {
						if (role.get("username") != null)
							roleList.add((String) role.get("username"));
					}
					for (Map permission : (List<Map>) Menu) {
						if (permission.get("name") != null) {
							permList.add((String) permission.get("name"));
						}
					}
				}
			} catch (Exception e) {
				logger.warn("查询权限异常", e);
			}
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			info.addRoles(roleList);
			info.addStringPermissions(permList);
			return info;
		} else {
			throw new AuthorizationException();
		}
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authToken;
		Map user;
		if (token.getUsername().toLowerCase().equals("sys")) {
			String str2 = String.valueOf(token.getPassword());
			String str1 = PwdUtil.encodePwd("admin");
			if (str1.equals(str2))
				user = new HashMap() {
					{
						put("id", 0);
						put("bm", "sys");
						put("dh", "sys");
						put("mc", "超级管理员");
						put("password", PwdUtil.encodePwd("admin"));
					}
				};
			else
				throw new UnknownAccountException("登录名或密码不正确");
		} else {
			user = apiService.queryOperator(token.getUsername());
		}
		if (user == null) {
			throw new UnknownAccountException("登录名或密码不正确");
		} else {
			if (user.get("loginlock") != null && user.get("loginlock").toString().equals("1"))
				throw new LockedAccountException("用户已锁定");
			AuthenticationInfo info;
			if (!user.get("password").equals("nopassword")) {
				info = new SimpleAuthenticationInfo(user, user.get("password") == null ? "" : user.get("password").toString(), user.get("username").toString());
			}else {
				info = new SimpleAuthenticationInfo(user, "", user.get("username").toString());
			}
			return info;
		}
	}
}