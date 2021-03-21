package com.module.weixin.service;


import com.Controller.TimedTasks;
import com.helei.api.common.ImplBase;

import com.helei.api.util.HttpUtils;
import com.helei.api.util.ImageUtil;
import com.module.common.CommonMethod;
import com.util.QRcodeUtils;
import com.util.RedisUtil;
import com.util.ReleaseStatus;
import com.util.WeixinUtil;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"serial", "unchecked", "rawtypes"})
@Service("wxApiService")
public class ApiService extends ImplBase {

    @Autowired
    private DbService dbService;

    @Resource
    private WeixinUtil weixinUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private TimedTasks timedTasks;

    @Resource
    private CommonMethod commonMethod;

    @Value("${imges.wx.url}")
    private String imageurl;


    /**
     * 获取保存的MAP中的value
     *
     * @param key 键
     * @return 数据集合
     */
    public Map getConfig(String[] key) {
        return new HashMap() {{
            try {

                for (int i = 0; i < key.length; i++) {
                    String value = commonMethod.getConfig(key[i].toString());
                    put(key[i].toString(), value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }};
    }

    /**
     * 获取基础支持的 Access_token
     *
     * @return Access_token
     */
    public String getAccess_token() {
        try {
            Map return_map = new HashMap();
            Object redis_access_token = commonMethod.getConfig("access_token");
            Object redis_expires_in = commonMethod.getConfig("expires_in");
            Object redis_access_token_time = commonMethod.getConfig("access_token_time");
            Boolean is_redis_Effective = weixinUtil.access_token_Is_it_effective(redis_access_token_time, redis_expires_in);
            //如果 redis 内有数据首先判断redis是否有效
            if (isnull(redis_access_token) || !is_redis_Effective) {
                String[] keys = new String[]{"appID", "appsecret", "access_token", "expires_in", "access_token_time"};
                Map<String, Object> map = (Map<String, Object>) getConfig(keys);
                for (String key : keys) {
                    if (!map.containsKey(key)) {
                        throw new Exception(key + " 配置项未查询到,请确定config表内是否有该配置项");
                    }
                }
                Object access_token_time = map.get("access_token_time");
                Object expires_in = map.get("expires_in");
                Object access_token = map.get("access_token");
                Object appID = map.get("appID");
                Object appsecret = map.get("appsecret");
                Boolean is_Effective = weixinUtil.access_token_Is_it_effective(access_token_time, expires_in);
                if (isnull(appID) || isnull(appsecret)) {
                    throw new Exception("appID或appsecret配置项为空,请确定config表内是否有该配置项");
                }
                if (isnull(access_token_time) || isnull(expires_in) || isnull(access_token)) {//如值为空从新获取写入数据库
                    Refresh_Access_token();
                    return getAccess_token();
                } else if (is_Effective) {
                    return_map.put("access_token", access_token);
                    return access_token.toString();
                } else {
                    //如果无效 重新获取 并写入redis 并返回
                    Map res_map = weixinUtil.access_token(appID.toString(), appsecret.toString());
                    Map sqlres = dbService.updateAccess_token(res_map);
                    //走刷新access_token逻辑
                    return sqlres.get("access_token").toString();
                }
            } else {
                return_map.put("access_token", redis_access_token);
                Boolean is_Effective = weixinUtil.access_token_Is_it_effective(redis_access_token_time, redis_expires_in);
                if (is_Effective) {
                    //有效直接返回
                    return redis_access_token.toString();
                } else {
                    //无效删除redis数据 并执行递归操作
                    Refresh_Access_token();
                    return getAccess_token();//从新进入获取的方法
                }
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, null, null);
            return null;
        }
    }
    public Boolean Refresh_Access_token() throws Exception {
        Map map = jsonToMap(redisUtil.get("config"));
        if (isnull(map) || map.size() == 0) {
            throw new Exception("获取配置失败!");
        }
        if (isnull(map.get("appID")) || isnull(map.get("appsecret"))) {
            throw new Exception("获取appID,appsecret失败!");
        }
        Object appID = map.get("appID");
        Object appsecret = map.get("appsecret");
        //获取access_token逻辑
        Map res_map = weixinUtil.access_token(appID.toString(), appsecret.toString());
        //更新access_token逻辑
        Map sqlres = dbService.updateAccess_token(res_map);
        timedTasks.saveConfig();
        if (sqlres.size()>0){
            return true;
        }else {
            return false;
        }
    }
    @Transactional
    public void getUserInfo(Map map, HttpServletResponse response) {
        try {
            String code = map.get("code").toString();
            String state = map.get("state").toString();
            String[] array = state.split(",");
            String uuid = array[0];
            String key = array[1];
            String appID = commonMethod.getConfig("appID");
            String appsecret = commonMethod.getConfig("appsecret");
            //如果redis中没有保存就重新保存
            if (isnull(appID) || isnull(appsecret)) {
                getWxtoconfigure();
                appID = commonMethod.getConfig("appID");
                appsecret = commonMethod.getConfig("appsecret");
            }
            //获取用户信息的Access_token
            Map url_map = weixinUtil.CodeExchangeForAccess_token(appID, appsecret, code);
            //判断是否成功请求
            if (isnull(url_map.get("errcode"))) {
                String access_token = url_map.get("access_token").toString();
                String openid = url_map.get("openid").toString();
                //获取微信登录信息
                Map info_map = weixinUtil.GetUserinformation(access_token, openid);
                if (!isnull(info_map.get("errcode"))) {
                    //如果返回48001 错误 就重新获取授权
                    if (info_map.get("errcode").equals("48001")) {
                        weixinUtil.Reauthorization(appID, "snsapi_userinfo", state);
                    } else {
                        getLogger().error("获取用户信息失败:" + info_map.get("errcode"));
                    }
                } else {
                    //查询是否有保存微信用户信息
                    List<Map> list = dbService.getWXUser(new HashMap() {
                        {
                            put("openid", info_map.get("openid"));
                        }
                    });
                    if (list.size() == 0) {
                        //数据库中无数据 插入数据并保存图片
                        String imgurl = ImageUtil.downloadjpg(info_map.get("headimgurl").toString(), info_map.get("openid").toString(), imageurl);
                        int row = dbService.addWXUser(new HashMap() {
                            {
                                put("openid", info_map.get("openid"));
                                put("country", info_map.get("country"));
                                put("province", info_map.get("province"));
                                put("city", info_map.get("city"));
                                put("sex", info_map.get("sex"));
                                put("nickname", info_map.get("nickname"));
                                put("headimgurl", info_map.get("headimgurl"));
                                put("imgurl", imgurl);
                            }
                        });
                        //判断是否成功插入数据
                        if (row > 1) {
                            redisUtil.update("QRcode:" + uuid, ReleaseStatus.SCANNED);
                            response.sendRedirect("/wx/confirmlogin?uuid=" + uuid + "&key=" + key + "&openid=" + info_map.get("openid"));
                        } else {
                            String url = "/wx/confirmlogin?uuid=" + map.get("uuid");
                            response.sendRedirect(url);
                            dbService.getReturn_status("20000");
                        }
                    } else {
                        redisUtil.update("QRcode:" + uuid, ReleaseStatus.SCANNED);
                        response.sendRedirect("/wx/confirmlogin?uuid=" + uuid + "&key=" + key + "&openid=" + info_map.get("openid"));
                    }
                }
            } else {
                String errcode = url_map.get("errcode").toString();
                if (errcode.equals("40029") || errcode.equals("40163")) {
                    weixinUtil.Reauthorization(appID, "snsapi_userinfo", state);
                }
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, null, null);
        }
    }

    public void getCode(Map map) {
        try {
            Object appid = commonMethod.getConfig("appID");
            if (isnull(appid)) {
                getWxtoconfigure();
                getCode(map);
            }
            if (map.get("t").equals("0")) {
                weixinUtil.Reauthorization(appid, "snsapi_userinfo", map.get("uuid") + "," + map.get("key"));
            } else if (map.get("t").equals("1")) {
                weixinUtil.Reauthorizationbinding(appid, "snsapi_userinfo", map.get("uuid") + "," + map.get("key"));
            } else {
                throw new Exception("接口请求方式为空");
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, null, map);
        }
    }

    public void getWxtoconfigure() {
        timedTasks.saveConfig();
    }

    public Map createQr(Map param) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String key = GetRandomString(10);
        try {
            String[] strs = new String[]{"realmname", "QRcodetime"};
            Map<String, String> map = getConfig(strs);
            //判断扫码方式0--登录 1--用户绑定
            String url;
            if (isnull(param.get("t"))) {
                url = "http://" + map.get("realmname") + "/wx/getCode?t=0&uuid=" + uuid + "&key=" + key;
            } else {
                String[] keys=new String[]{"ID","username"};
                redisUtil.set("QRcode:binding:" + uuid, toJson(commonMethod.getuserinfo(keys)), Long.valueOf(String.valueOf(map.get("QRcodetime"))));
                url = "http://" + map.get("realmname") + "/wx/getCode?t=1&uuid=" + uuid + "&key=" + key;
            }
            String qrCode = QRcodeUtils.createQrCode(url, 200, 200);
            redisUtil.set("QRcode:" + uuid, "NOT_SCAN", Long.valueOf(String.valueOf(map.get("QRcodetime"))));
            return new HashMap() {{
                put("qrCode", qrCode);
                put("uuid", uuid);
                put("key", key);
            }};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void binding(Map map, HttpServletResponse response) {
        try {
            String code = map.get("code").toString();
            String state = map.get("state").toString();
            String[] array = state.split(",");
            String uuid = array[0];
            String key = array[1];
            getUserisbinding(uuid, response);
            String appID = commonMethod.getConfig("appID");
            String appsecret = commonMethod.getConfig("appsecret");
            //如果redis中没有保存就重新保存
            if (isnull(appID) || isnull(appsecret)) {
                getWxtoconfigure();
                appID = commonMethod.getConfig("appID");
                appsecret = commonMethod.getConfig("appsecret");
            }
            //获取用户信息的Access_token
            Map url_map = weixinUtil.CodeExchangeForAccess_token(appID, appsecret, code);
            //判断是否成功请求
            if (isnull(url_map.get("errcode"))) {
                String access_token = url_map.get("access_token").toString();
                String openid = url_map.get("openid").toString();
                //获取微信登录信息
                Map info_map = weixinUtil.GetUserinformation(access_token, openid);
                if (!isnull(info_map.get("errcode"))) {
                    //如果返回48001 错误 就重新获取授权
                    if (info_map.get("errcode").equals("48001")) {
                        weixinUtil.Reauthorization(appID, "snsapi_userinfo", state);
                    } else {
                        getLogger().error("获取用户信息失败:" + info_map.get("errcode"));
                    }
                } else {
                    //查询是否有保存微信用户信息
                    List<Map> list = dbService.getWXUser(new HashMap() {
                        {
                            put("openid", info_map.get("openid"));
                        }
                    });
                    if (list.size() == 0) {
                        //数据库中无数据 插入数据并保存图片
                        String imgurl = ImageUtil.downloadjpg(info_map.get("headimgurl").toString(), info_map.get("openid").toString(), imageurl);
                        int row = dbService.addWXUser(new HashMap() {
                            {
                                put("openid", info_map.get("openid"));
                                put("country", info_map.get("country"));
                                put("province", info_map.get("province"));
                                put("city", info_map.get("city"));
                                put("sex", info_map.get("sex"));
                                put("nickname", info_map.get("nickname"));
                                put("headimgurl", info_map.get("headimgurl"));
                                put("imgurl", imgurl);
                            }
                        });
                        //判断是否成功插入数据
                        if (row > 1) {
                            redisUtil.update("QRcode:" + uuid, ReleaseStatus.SCANNED);
                            response.sendRedirect("/wx/confirmbinding?uuid=" + uuid + "&key=" + key + "&openid=" + info_map.get("openid"));
                        } else {
                            String url = "/wx/confirmbinding?uuid=" + map.get("uuid");
                            response.sendRedirect(url);
//                        dbService.getReturn_status("20000");
                            throw new Exception("数据保存失败");
                        }
                    } else {
                        redisUtil.update("QRcode:" + uuid, ReleaseStatus.SCANNED);
                        response.sendRedirect("/wx/confirmbinding?uuid=" + uuid + "&key=" + key + "&openid=" + info_map.get("openid"));
                    }
                }
            } else {
                String errcode = url_map.get("errcode").toString();
                if (errcode.equals("40029") || errcode.equals("40163")) {
                    weixinUtil.Reauthorization(appID, "snsapi_userinfo", state);
                }
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, null, map);
            try {
                response.sendRedirect("/wx/confirmbinding");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 获取用户绑定信息
     * @param uuid
     * @param response
     * @throws Exception
     */
    public void getUserisbinding(String uuid, HttpServletResponse response) throws Exception {
        Map parameter = jsonToMap(redisUtil.get("QRcode:binding:" + uuid));

        if (isnull(parameter.get("ID"))) {
            throw new Exception("用户编码不能为空");
        } else {
            List<Map> userinfo = commonMethod.getUserinfo(new HashMap() {{
                put("ID", parameter.get("ID"));
            }});
            if (userinfo.size() < 0) {
                throw new Exception("用户不存在");
            }
            List list = dbService.getuserisbinding(new HashMap() {{
                put("user_id", userinfo.get(0).get("ID"));
            }});
            if (list.size() > 0) {
                throw new Exception("用户已绑定微信,无法继续绑定,请先解除绑定");
            } else {

            }
        }
    }

    /**
     * 绑定用户到wx_user 表
     * @param map
     * @return
     */
    public Map bindinguser(Map map) {
        try {
            if (isnull(map.get("uuid"))){
                throw new Exception("获取数据失败:uuid is null");
            }
            if (isnull(map.get("openid"))){
                throw new Exception("获取数据失败:openid is null");
            }
            Map parameter=jsonToMap(redisUtil.get("QRcode:binding:"+map.get("uuid")));
            int row=dbService.updateWXUser(new HashMap(){{
                put("openid",map.get("openid"));
            }},new HashMap(){{
                put("user_id",parameter.get("ID"));
            }});
            if (row>0){
                Map send_data=new HashMap();
                send_data.put("touser",map.get("openid"));//openid
                send_data.put("template_id","4CKBj3YcDJ3XeLUUMQJmz0acrIcepPNgcCX8fz-FnPQ");//模板ID
                //send_data.put("url","http://weixin.qq.com/download");
                send_data.put("data",new HashMap(){{
                    put("first",new HashMap(){{
                        put("value","您已成功绑定");
                        put("color","#173177");
                    }});
                    put("key1",new HashMap(){{
                        put("value",parameter.get("username"));
                        put("color","#173177");
                    }});
                    put("remark",new HashMap(){{
                        put("value","绑定成功后可使用微信扫码登录!");
                        put("color","#173177");
                    }});
                }});
                Map res=WX_Template_message(send_data);
                return dbService.getReturn_status("10000","绑定成功");
            }else {
                return dbService.getReturn_status("20000","绑定失败:更新失败");
            }
        } catch (Exception e) {
            dbService.setLog(e, null, map);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
    public Map WX_Template_message(Map map){
        try {
            String str=HttpUtils.httpPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+getAccess_token(),toJson(map));
            Map res=jsonToMap(str);
            if (res.get("errcode").equals("0")){
                dbService.addWXMessage(new HashMap(){{
                    put("send_data",toJson(map));
                    put("state",0);
                    put("openid",map.get("touser"));
                    put("frequency",1);
                    put("ret_data",str);
                }});
                return dbService.getReturn_status("10000",res);
            }else {
                dbService.addWXMessage(new HashMap(){{
                    put("send_data",toJson(map));
                    put("state",1);
                    put("openid",map.get("touser"));
                    put("frequency",1);
                    put("ret_data",str);
                }});
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            dbService.setLog(e,null,map);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
    public Map delwxbinding(Map map){
        try {
            String user_id="";
            if (isnull(map.get("user_id"))){
                user_id=commonMethod.getuserID();
            }else {
                user_id=map.get("user_id").toString();
            }
            String finalUser_id = user_id;
            List<Map> list=dbService.getWXUser(new HashMap(){{
                put("user_id", finalUser_id);}
            });
            if (list.size()==0){
                throw new Exception("查询绑定关系失败");
            }
            int row=dbService.updateWXUser(new HashMap(){{
                put("user_id", finalUser_id);
                put("openid", list.get(0).get("openid"));
            }}, new HashMap(){{
                put("user_id",null);
            }});
            if (row>0){
                Map send_data=new HashMap();
                send_data.put("touser",list.get(0).get("openid"));//openid
                send_data.put("template_id","bVKOyLOO8XkdUq66kN4p8F1hVpDysOT914XpK74pjPM");//模板ID
                send_data.put("url","http://"+commonMethod.getConfig("realmname")+"/wx/confirmbinding");
                send_data.put("data",new HashMap(){{
                    put("first",new HashMap(){{
                        put("value","您已成功解绑");
                        put("color","#173177");
                    }});
                    put("key1",new HashMap(){{
                        put("value",commonMethod.getuserinfo(new String[]{"username"}));
                        put("color","#173177");
                    }});
                    put("remark",new HashMap(){{
                        put("value","您已无法使用微信登录系统,如非本人操作,请点击更多进行撤销操作!");
                        put("color","#173177");
                    }});
                }});
                WX_Template_message(send_data);
                return dbService.getReturn_status("10000","解绑成功");
            }else {
                throw new Exception("数据修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
}
