package com.service;


import com.helei.api.common.ImplBase;
import com.util.RedisUtil;
import com.util.WeixinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"serial", "unchecked", "rawtypes"})
@Service("jobApiService")
public class ApiService extends ImplBase {

    @Autowired
    private DbService dbService;

    @Autowired
    private HttpServletRequest request;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WeixinUtil weixinUtil;

    public Map SaveConfig(){
        try {
            List list=dbService.getConfig();
            if (list.size() > 0) {
                Map<String, Object> resmap = weixinUtil.ConfigRowtocolumn(list);
                return resmap;
            } else {
                return dbService.getReturn_status("20000");
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e,null,null);
            return dbService.getReturn_status("20000");
        }
    }
}
