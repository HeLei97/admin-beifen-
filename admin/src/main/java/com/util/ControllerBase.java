package com.util;

import com.helei.api.common.ImplBase;

import com.module.app.service.DbService;
import org.apache.shiro.util.AntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class ControllerBase extends ImplBase {

    @Autowired
    DbService dbService;

    @ModelAttribute
    protected void setBaseModel(Model model, HttpServletRequest request) {
        Map operator = (Map) request.getSession().getAttribute("user");
        if (operator != null)
            model.addAttribute("user", operator);
        Map dept = (Map) request.getSession().getAttribute("dept");
        if (dept != null)
            model.addAttribute("dept", dept);
    }

    //String[] arr = {"/", "/index", "/console", "/login", "/404", "/register", "/Message/index", "/Message/detail","/wx/confirmlogin","/user/info","/user/wxbinding","/wx/binding","/wx/confirmbinding","/user/password","/user/operationPwd"};
    String[] arr = {"/", "/index", "/console", "/login", "/404", "/register", "/Message/*","/wx/*","/user/wxbinding","/user/*"};
    /**
     * 页面访问权限控制,无权限则跳转到404页面(需定义公共页面,否则会被拦截)
     *
     * @param request
     * @param response
     */
    @ModelAttribute
    protected void intercept(HttpServletRequest request, HttpServletResponse response) {
        String Path = request.getServletPath();
        //公共页面跳过拦截
        boolean flag = Arrays.asList(arr).contains(Path);
        // /*/*通配符允许访问
        AntPathMatcher matcher = new AntPathMatcher();
        for (String str:arr) {
            if (matcher.match(str,Path)){
                flag=true;
            }
        }
        if (!flag) {
            try {
                List<Map> menu_list = (List<Map>) request.getSession().getAttribute("menu");
                Boolean Jumpornot = true;
                for (Map mm : menu_list) {
                    if (menu_list != null && menu_list.size() > 0) {
                        for (Map m : (List<Map>) mm.get("Submenu")) {
                            String sql = "SELECT * FROM menu WHERE parent_id ="+m.get("ID")+" AND url IS NOT NULL AND url !='' AND state=0";
                            List<Map> list = dbService.querySql(sql);
                            if (list.size() > 0) {
                                for (Map d : list) {
                                    if (Path.equals(d.get("url"))) {
                                        Jumpornot = false;
                                    }
                                }
                            }
                            if (Path.equals(m.get("url"))) {
                                Jumpornot = false;
                            }
                        }
                        if (!Jumpornot) {
                            continue;
                        }
                    }
                }
                if (Jumpornot) {
                    System.out.print(Path+" 无权限访问,公共面请在intercept方法添加公共页面");
                    response.sendRedirect("/404");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询出页面按钮权限是否存在
     * @param model
     * @param request
     */
    @ModelAttribute
    public void  Button_permissions(Model model, HttpServletRequest request) {

        String Path=request.getServletPath();
        System.out.println(Path);
        boolean flag = Arrays.asList(arr).contains(Path);
        String id=request.getParameter("pid");
        if (!flag && !isnull(id)){
            Map user = (Map) request.getSession().getAttribute("user");
            String sql="SELECT * FROM menu WHERE FIND_IN_SET(id,(SELECT menu_id FROM `user` u ,authority auth where u.id="+user.get("ID")+" AND u.grouping_id=auth.grouping_id)) AND type=2 AND parent_id="+id;
            List<Map> list=dbService.querySql(sql);
            for (Map<String,String> map:list) {
                if (isnull(map.get("BM"))){
                    return;
                }
                model.addAttribute(map.get("BM").toString(),map);
                System.out.println(toJson(map));
            }
        }
    }
}
