package com.module.weixin.service;

import com.helei.api.db.DbAgent;
import com.helei.api.mapper.CommonDao;
import com.helei.api.util.DateUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.security.Key;
import java.util.*;
import java.util.List;

@Service("wxDbService")
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class DbService extends DbAgent {
    public DbService(CommonDao dao) {
        super(dao);
    }
    public Map updateAccess_token(Map map){
        Map res=new HashMap();
        String sql="UPDATE "+CONFIG+" SET VALUE='%s' WHERE KEYNAME='%s'";
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            res.put(entry.getKey(),entry.getValue());
            querySql(String.format(sql,entry.getValue(),entry.getKey()));
        }
        String dataStr=DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        querySql(String.format(sql,dataStr,"access_token_time"));
        res.put("access_token_time",dataStr);
        return  res;
    }
    public List getWXUser(Map map){
        return queryTable(WX_USER,map);
    }
    public int addWXUser(Map map){
        return insertTable(WX_USER,map);
    }

    /**
     * 更新微信绑定表
     * @param key
     * @param value
     * @return
     */
    public int updateWXUser(Map key,Map value){
        return updateTable(WX_USER,key,value);
    }
    /**
     * 微信推送记录
     * @param map
     */
    public int addWXMessage(Map map){
        return insertTable(WX_MESSAGE,map);
    }
}
