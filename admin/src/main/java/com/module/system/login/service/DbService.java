package com.module.system.login.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.helei.api.db.DbAgent;
import com.helei.api.mapper.CommonDao;
import org.springframework.stereotype.Service;

@Service("sysDbService")
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class DbService extends DbAgent {

    public DbService(CommonDao dao) {
        super(dao);
    }
    public List getUserInfo(Map map){
        try {
            String sql = "select * from "+User;
            sql+=" WHERE username='"+map.get("username")+"'";
            List list = querySql(sql);
            return list;
        } catch (Exception e) {
            getLogger().warn(e);
            throw new RuntimeException(e);
        }
    }
    public List<Map>  getMenu(String ID){
        String sql="SELECT * FROM "+User+" WHERE ID="+ID;
        return querySql(sql);
    }
    public List<Map> getAuthority(String grouping){
        List<Map> list=queryTable(AUTHORITY,new HashMap(){
            {
                put("grouping_id",grouping);
            }
        });
        return list;
    }
    public List<Map> queryUser() {
        return queryTable(User);
    }
    public List<Map> queryMenu() {
        return queryTable(MENU);
    }
    public List<Map> queryUserRole(int userId) {
        String sql = "select * from "+User;
        sql+=" WHERE BM='"+userId+"'";
        return querySql(sql);
    }
    public List<Map> queryPermissionByUser(String Mebuids) {
        String sql = "select * from "+MENU;
        sql+=" WHERE ID in ("+Mebuids+") AND parent_id=0 ";
        return querySql(sql);
    }

    public List<Map> getSubmenu(String Mebuids,String id){
        String sql = "select * from "+MENU;
        sql+=" WHERE ID in ("+Mebuids+") AND parent_id="+id+" AND state=0";
        return querySql(sql);
    }

    public List getwxbinding(String openid){
        String sql="SELECT * FROM "+WX_USER+" wx,user u WHERE wx.user_id=u.id AND openid='"+openid+"'";
        return querySql(sql);
    }

    public List getuser_info(Map<String,Object> map){
        String sql="SELECT * FROM user u LEFT JOIN wx_user wx ON wx.user_id=u.id WHERE 1=1";
        for (String key:map.keySet()){
           sql+=" AND "+key+"='"+map.get(key)+"'";
        }
        return querySql(sql);
    }
    public int update_wx_user(Map keys,Map values){
        return updateTable(WX_USER,keys,values);
    }
    public List getEmail_config(){
        String sql="select * from "+EMAIL_NOTICE +" WHERE state=0 ORDER BY ifnull(type,1) ASC";
        return  querySql(sql);
    }
    public List getEmail_Template(Map map){
        return queryTable(EMAIL_TEMPLATE,map);
    }
}
