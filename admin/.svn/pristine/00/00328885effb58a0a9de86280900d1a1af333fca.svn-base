package com.module.weixin.controller;


import com.helei.api.common.ImplBase;
import com.helei.api.util.DESUtil;
import com.helei.api.util.DateUtils;
import com.helei.api.util.ReqToMapUtil;
import com.module.common.CommonMethod;
import com.module.weixin.service.ApiService;
import com.util.RedisUtil;
import com.util.ReleaseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@RequestMapping(value = "/wx")
@RestController
public class WXRestController extends ImplBase {
    @Autowired
    private ApiService apiService;

    private static int ExpireTime = 60;   // redis中存储的过期时间60s

    @Resource
    private RedisUtil redisUtil;

//    @RequestMapping("set")
//    public boolean redisset(String key, String value) {
//        return redisUtil.set(key, value,20);
//    }
//
//    @RequestMapping("get")
//    public Object redisget(String key) {
//        return redisUtil.get(key);
//    }
//
//    @RequestMapping("update")
//    public Object update(String key, String value) {
//        return redisUtil.update(key, value);
//    }
//
//    @RequestMapping("expire")
//    public boolean expire(String key) {
//        return redisUtil.expire(key, ExpireTime);
//    }

    /**
     * 获取Access_token 未测试启用
     */
    @RequestMapping("getAccess_token")
    public void getAccess_token() {
        Object Access_token = redisUtil.get("Access_token");
        if (Access_token == null) {
            System.out.println(apiService.getAccess_token());
        }
    }

    /**
     * 获取微信用户信息
     *
     * @param req
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "getUserInfo",method = RequestMethod.GET)
    public void getUserInfo(HttpServletRequest req, HttpServletResponse response, Model model) throws IOException {

        Map map = ReqToMapUtil.getParameterMap(req);
        apiService.getUserInfo(map, response);
//        String state=map.get("state").toString();
//        String[] array=state.split(",");
//        String uuid=array[0];
//        String key=array[1];
//        if (res.get("code").equals("10000")){
//            Map data=(Map) res.get("data");
//            redisUtil.update(uuid, ReleaseStatus.SCANNED);
//            response.sendRedirect("/wx/confirmlogin?uuid="+uuid+"&key="+key+"&openid="+data.get("openid"));
//        }else {
//            response.sendRedirect("/404");
//        }
    }


    @RequestMapping(value = "bindinguser",method = RequestMethod.GET)
    public void bindinguser(HttpServletRequest req, HttpServletResponse response, Model model) {
        Map map = ReqToMapUtil.getParameterMap(req);
        apiService.binding(map,response);
    }
    /**
     * 重定向到微信授权页面
     *
     * @param req
     * @param model
     */
    @RequestMapping(value = "getCode",method = RequestMethod.GET)
    public void getCode(HttpServletRequest req, Model model) {
        Map map = ReqToMapUtil.getParameterMap(req);
        apiService.getCode(map);
    }

    /**
     * 生成二维码
     * @return 二维码
     */
    @RequestMapping("getQrcode")
    public Map getQrcode(HttpServletRequest req) {
        Map map = ReqToMapUtil.getParameterMap(req);
        //需要设置一下二维码超时
        return apiService.createQr(map);
    }

    /**
     * 查询二维码状态
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "query",method = RequestMethod.POST)
    public Map queryIsScannedOrVerified(HttpServletRequest req, Model model) {
        Map map = ReqToMapUtil.getParameterMap(req);
        if (isnull(map.get("uuid"))){
            return new HashMap() {{
                put("error","查询二维码状态失败:UUID is null");
            }};
        }
        String uuid = map.get("uuid").toString();
        Object QrCodeStatus = redisUtil.get("QRcode:"+uuid);
        if (isnull(QrCodeStatus)){
            return new HashMap() {{
                put("QrCodeStatus", ReleaseStatus.Expired);
            }};
        }else {
            return new HashMap() {{
                put("uuid", uuid);
                put("QrCodeStatus", QrCodeStatus);
            }};
        }
    }

    /**
     * 查询扫码确认
     * @param req
     * @param model
     * @return
     */
    /*@RequestMapping("doScan")
    public Map doAppScanQrCode(HttpServletRequest req, Model model) {
        Map map = ReqToMapUtil.getParameterMap(req);
        String uuid = map.get("uuid").toString();
        String QrCodeStatus = redisUtil.get(uuid).toString();
        if (isnull(QrCodeStatus))
            return null;
        //需要规范一下状态
        switch (QrCodeStatus) {
            case ReleaseStatus.NOT_SCAN:
                redisUtil.update(uuid, ReleaseStatus.SCANNED);
                    return new HashMap(){{
                        put("code","SCANNED");
                        put("msg","请手机确认");
                    }};
            case ReleaseStatus.SCANNED:
                return new HashMap(){{
                    put("code","SCANNED");
                    put("msg","已扫描");
                }};
            case ReleaseStatus.VERIFIED:
                return new HashMap(){{
                    put("code","VERIFIED");
                    put("msg","已确认");
                }};
            case ReleaseStatus.EXIT:
                return new HashMap(){{
                    put("code","EXIT");
                    put("msg","取消授权");
                }};
        }
        return new HashMap(){{
            put("msg","失败");
        }};
    }*/

    /**
     * 查询扫码确认
     * @param req
     * @param model
     * @return
     */
    @RequestMapping(value = "Scancode",method = RequestMethod.POST)
    public Map Scancode(HttpServletRequest req, Model model){
        try {
        Map map = ReqToMapUtil.getParameterMap(req);
        String uuid = map.get("uuid").toString();
        String key = map.get("key").toString();
        String state =map.get("state").toString() ;
        Object QrCodeStatus = redisUtil.get("QRcode:"+uuid);
        if (!isnull(QrCodeStatus) && ReleaseStatus.SCANNED.equals(QrCodeStatus)){
        if (state.equals("0")) {
            Map resmap=new HashMap();
            resmap.put("time", DateUtils.currentIntegerTime());
            resmap.put("openid", map.get("openid"));
            String jiami=DESUtil.getEncryptString("parameter:"+uuid,toJson(resmap));
            redisUtil.set(key,jiami,1800);
            redisUtil.set("QRcode:"+uuid, ReleaseStatus.VERIFIED);
            //设置绑定用户ID到微信表 t 不为空则标识绑定
            if (!isnull(map.get("t"))){
                apiService.bindinguser(new HashMap(){{
                    put("uuid",uuid);
                    put("openid",map.get("openid"));
                }});
            };
            return new HashMap() {{
                put("code", "Success");
                put("msg", "已确认");
            }};
        }else if (state.equals("1")){
            redisUtil.update("QRcode:"+uuid, ReleaseStatus.EXIT);
            return new HashMap() {{
                put("code",  "Error");
                put("msg", "已取消登录");
            }};
        }else {
            return new HashMap() {{
                put("code", "Error");
                put("msg", "请求错误!");
            }};
        }
    }else {
            return new HashMap() {{
                put("code", "Error");
                put("msg", "二维码已过期请重新扫码");
            }};
        }
        }catch (Exception e){
             return new HashMap() {{
                put("code", "Error");
                put("msg", "请求错误!");
            }};
        }
    }
    @RequestMapping("/test1")
    public Map test(){
       return apiService.WX_Template_message(null);
    }
    @RequestMapping(value = "/delwxbinding",method = RequestMethod.POST)
    public Map delwxbinding(HttpServletRequest req){
        Map map = ReqToMapUtil.getParameterMap(req);
        return apiService.delwxbinding(map);
    }
}
