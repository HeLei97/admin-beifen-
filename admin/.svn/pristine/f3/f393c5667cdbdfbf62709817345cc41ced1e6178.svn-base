package com.shiro;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

public class ShiroUserFilter extends UserFilter {

    @Override
    protected void redirectToLogin(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
        // 返回json
        servletResponse.setContentType("application/json; charset=utf-8");

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("code", -1);
        response.put("msg", "未登录或者登录已失效");

        String json = JSON.toJSONString(response);

        servletResponse.getWriter().write(json);
    }


}