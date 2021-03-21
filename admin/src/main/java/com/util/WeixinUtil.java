package com.util;


import com.helei.api.common.ImplBase;
import com.helei.api.util.HttpUtils;
import com.module.common.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class WeixinUtil extends ImplBase {
    private String access_token_url="https://api.weixin.qq.com/cgi-bin/token";
    private String code_access_token="https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private String userinfo_url="https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
    @Autowired
    private HttpServletResponse response;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private CommonMethod commonMethod;
    /**
     * 将Config 表中查询出的数据转换成键值对
     * @param list
     * @return
     */
    public Map ConfigRowtocolumn(List<Map<String,Object>> list){
        Map map=new HashMap();
        for (Map<String,Object> m:list) {
            map.put(m.get("keyname"),m.get("value"));
        }
        return map;
    }

    /**
     * 验证access_token 是否在有效期
     * @param access_token_time 获取时间
     * @param expires_in 有效期 秒
     * @return Boolean true --有效  false --失效
     * @throws ParseException
     */
    public Boolean access_token_Is_it_effective(Object access_token_time,Object expires_in) throws ParseException {
        Calendar nowDate=Calendar.getInstance();
        Calendar oldDate=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
        Date date = simpleDateFormat.parse(access_token_time.toString());
        nowDate.setTime(new Date());//设置为当前系统时间
        oldDate.setTime(date);//设置为想要比较的日期
        Long timeNow=nowDate.getTimeInMillis();
        Long timeOld=oldDate.getTimeInMillis();
        Long time = (timeNow-timeOld);//相差毫秒数
        int expires_millisecond=Integer.parseInt(expires_in==null?"7200":expires_in.toString())*1000;
        if (time>expires_millisecond){
            return  false;
        }else {
            return  true;
        }
    }

    public Map  access_token(String appid,String secret) throws Exception {
        //grant_type=client_credential&appid=APPID&secret=APPSECRET
        Map parameter=new HashMap();
        parameter.put("appid",appid);
        parameter.put("secret",secret);
        parameter.put("grant_type","client_credential");
        String url=SplicingURL(access_token_url,parameter);
        getLogger().warn("URL:"+url);
        String result=HttpUtils.httpGet(url);
        getLogger().warn("result:"+jsonToMap(result));
        return jsonToMap(result);
    }

    /**
     * code 换取Access_token Access_token不等于基础支持Access_token
     * @param appid
     * @param secret
     * @param code
     * @return
     * @throws Exception
     */
    public Map  CodeExchangeForAccess_token(Object appid,Object secret,Object code) throws Exception {
        Map parameter=new HashMap();
        String url=String.format(code_access_token,appid,secret,code);
        getLogger().warn("URL:"+url);
        String result=HttpUtils.httpGet(url);
        getLogger().warn("result:"+jsonToMap(result));
        return jsonToMap(result);
    }

    /**
     * 获取用户信息
     * @param access_token  Access_token不等于基础支持Access_token
     * @param openid 微信标识
     * @return
     * @throws Exception
     */
    public Map  GetUserinformation(Object access_token,Object openid) throws Exception {
        Map parameter=new HashMap();
        String url=String.format(userinfo_url,access_token,openid);
        getLogger().warn("URL:"+url);
        String result=HttpUtils.httpGet(url);
        getLogger().warn("result:"+jsonToMap(result));
        return jsonToMap(result);
    }

    /**
     * 重定向授权页面
     * @param appID
     * @param scope
     * @throws Exception
     */
    public void  Reauthorization(Object appID,Object scope,Object state) throws Exception {
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appID+"&redirect_uri=http%3A%2F%2F"+commonMethod.getConfig("realmname")+"%2Fwx%2FgetUserInfo&response_type=code&scope="+scope+"&state="+state+"#wechat_redirect";
        response.sendRedirect(url);
    }
    /**
     * 重定向授权页面
     * @param appID
     * @param scope
     * @throws Exception
     */
    public void  Reauthorizationbinding(Object appID,Object scope,Object state) throws Exception {
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appID+"&redirect_uri=http%3A%2F%2F"+commonMethod.getConfig("realmname")+"%2Fwx%2Fbindinguser&response_type=code&scope="+scope+"&state="+state+"#wechat_redirect";
        response.sendRedirect(url);
    }
}
