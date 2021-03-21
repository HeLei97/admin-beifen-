package com.module.weixin.controller;

import com.helei.api.util.ReqToMapUtil;
import com.util.ControllerBase;
import com.util.RedisUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@RequestMapping(value = "/wx")
public class WXWebController extends ControllerBase {

    @Resource
    private RedisUtil redisUtil;
    /*
     * 请求登录页面
     */
    @RequestMapping(value = { "/wxtest" }, method = RequestMethod.GET)
    public String wxtest(Model model) {
        return "/weixin/test";
    }

//    @RequestMapping(value = { "/index" }, method = RequestMethod.GET)
//    public String index(Model model) {
//        return "/weixin/index";
//    }

    @RequestMapping(value = { "/confirmlogin" }, method = RequestMethod.GET)
    public String confirmlogin(Model model) {
        return "/weixin/confirmlogin";
    }
    @RequestMapping(value = { "/confirmbinding" }, method = RequestMethod.GET)
    public String confirmbinding(HttpServletRequest request,Model model) {
        Map map = ReqToMapUtil.getParameterMap(request);
        Map parameter = jsonToMap(redisUtil.get("QRcode:binding:" + map.get("uuid")));
        if (parameter.size()==0) {
            return "/weixin/error";
        }
        model.addAttribute("username",parameter.get("username"));
        return "/weixin/confirmbinding";
    }

    @RequestMapping(value = { "/binding" }, method = RequestMethod.GET)
    public String binding(HttpServletRequest request, Model model) {
        //判断请求是否携带有效参数,参数无效将禁止访问该页面
        Map map = ReqToMapUtil.getParameterMap(request);
        if (isnull(map.get("uuid")) || isnull(map.get("key"))){
            return "/404";
        }else {
            Object key=redisUtil.get(map.get("key").toString());
            if (isnull(key)){
                return "/login";
            }else {
                return "/weixin/binding";
            }
        }
    }
    @RequestMapping(value = { "/images" }, method = RequestMethod.GET)
    public String images(Model model) {

        return "/weixin/confirmbinding";
    }
}
