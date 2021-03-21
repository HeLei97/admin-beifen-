package com.module.system.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.helei.api.common.ImplBase;
import com.helei.api.util.ReqToMapUtil;
import com.module.common.CommonMethod;
import com.module.system.user.service.ApiService;
import com.util.ControllerBase;
import com.util.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Map;


@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@RequestMapping(value = "/user")
@RestController
public class UserRestController extends ImplBase {
    @Autowired
    ApiService apiService;
    @Autowired
    private CommonMethod commonMethod;
    /**
     * 查询所有用户
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "/getUserlist")
    public Map getUserList(HttpServletRequest req, Model model){
        Map user = null;
        try {
            user = getUserInfo(req);
            Map map= ReqToMapUtil.getParameterMap(req);
            user.put("data",map);
            return apiService.getUserList(user);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(e);
            return apiService.getReturn_status("20002");
        }
    }

    /**
     * 设置用户锁定
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "/setUserLock")
    public Map setUserLock(HttpServletRequest req, Model model){
        Map user = null;
        try {
            user = getUserInfo(req);
            Map map= ReqToMapUtil.getParameterMap(req);
            return apiService.SetUserLock(map);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(e);
            return apiService.getReturn_status("20002");
        }
    };

    /**
     * 获取用户个人信息
     * @param req
     * @return
     * @throws Exception
     */
    public Map getUserInfo(HttpServletRequest req) throws  Exception{
        Map user = commonMethod.getuserinfo();
        if (user!=null){
            return user;
        }else {
            throw new Exception("获取登录信息失败");
        }
    }

    /**
     * 更新用户信息
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "/UserinfoUpdate",method ={RequestMethod. POST})
    public Map UserinfoUpdate(HttpServletRequest req, Model model){
            Map map= ReqToMapUtil.getParameterMap(req);
            return apiService.UpdateUserInfo(map);
    };

    /**
     * 根据用户ID 查询
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "/getUserInfoByID",method ={RequestMethod. POST})
    public Map getUserInfoByID(HttpServletRequest req, Model model){
        Map user = null;
        try {
            user = getUserInfo(req);
            Map map= ReqToMapUtil.getParameterMap(req);
            return apiService.getUserByID(map);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(e);
            return apiService.getReturn_status("20002");
        }
    }

    /**
     * 删除用户并写入删除表
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "/delUser",method ={RequestMethod. POST})
    public Map delUser(HttpServletRequest req, Model model){
        Map map= ReqToMapUtil.getParameterMap(req);
        return  apiService.deleteUser(map);
    }

    /**
     * 查询用户分组信息
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "/getGrouping",method ={RequestMethod. POST})
    public Map getGrouping(HttpServletRequest req, Model model){
        return  apiService.getGrouping();
    }

    /**
     * 敏感操作验证操作密码
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "/ConfirmPassword",method = RequestMethod.POST)
    public Map ConfirmPassword(HttpServletRequest req, Model model){
        Map map= ReqToMapUtil.getParameterMap(req);
        return  apiService.ConfirmPassword(map);
    }

    /**
     * 获取公告
     * @param req
     * @return
     */
    @RequestMapping(value = "getmsgList",method = RequestMethod.GET)
    public Map getmsgList(HttpServletRequest req){
        Map user = commonMethod.getuserinfo();
        Map map= ReqToMapUtil.getParameterMap(req);
        map.put("user_id",user.get("ID"));
        return  apiService.getmsgList(map);
    }

    /**
     * 获取用户信息
     * @param req
     * @return
     */
    @RequestMapping(value = "getuserinfo",method = RequestMethod.POST)
    public Map getuserinfo(HttpServletRequest req){
        Map user = commonMethod.getuserinfo();
        Map map= ReqToMapUtil.getParameterMap(req);
        map.put("ID",user.get("ID"));
        return  apiService.getuserinfo(map);
    }

    /**
     * 上传头像图片
     * @param req
     * @param file
     * @return
     */
    @RequestMapping(value = "upload_img",method = RequestMethod.POST)
    public Map upload_img(HttpServletRequest req, MultipartFile file){
        Map map= ReqToMapUtil.getParameterMap(req);
        return apiService.upload_img(req, file);
    }
    @RequestMapping(value = "updatePwd",method = RequestMethod.POST)
    public Map updatePwd(HttpServletRequest req){
        Map map= ReqToMapUtil.getParameterMap(req);
        return apiService.updatePwd(map);
    }
    @RequestMapping(value = "updateOperationPwd",method = RequestMethod.POST)
    public Map updateOperationPwd(HttpServletRequest req){
        Map map= ReqToMapUtil.getParameterMap(req);
        return apiService.updateOperationPwd(map);
    }
}
