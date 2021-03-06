package com.module.system.user.service;

import com.helei.api.db.DbAgent;
import com.helei.api.mapper.CommonDao;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings({"rawtypes", "serial", "unchecked"})
public class DbService extends DbAgent {

    public DbService(CommonDao dao) {
        super(dao);
    }

    public List getUserList(Map<String, Object> map) {
        String sql = " SELECT * FROM "+User+" WHERE 1=1";
        for (String key:map.keySet()){
            if (!key.equals("limit") && !key.equals("page") && !map.get(key).equals("")){
                sql+=" AND "+key+" like '%"+map.get(key)+"%'";
            }
        }
        if (map.get("limit") != null && !"".equals(map.get("limit"))) {
            sql += " limit " + (Integer.valueOf(map.get("page").toString()) - 1) * Integer.valueOf(map.get("limit").toString()) + "," + map.get("limit");
        }
        return querySql(sql);
    };
    public List<Map> getUserinfo(Map map){
        return  queryTable(User,map);
    }
    public int UserLock(String id, String lock) {
        return updateTable(User, new HashMap() {
                    {
                        put("id", id);
                    }
                }, new HashMap() {
                    {
                        put("loginlock", lock);
                    }
                }
        );
    }

    public int UpdateUserInfo(String id, Map<String, String> map) {
        return updateTable(User, new HashMap() {
                    {
                        put("id", id);
                    }
                }, new HashMap() {
                    {
                        for (String key : map.keySet()) {
                            put(key, map.get(key));
                        }
                    }
                }
        );
    }
    public void DeleteUser(Map<String,Object> map){
        deleteTable(User,new HashMap(){
            {
                for (String key : map.keySet()) {
                    put(key, map.get(key));
                }
            }
        });
    }
    public void SetUserDelInfo(Map<String, Object> map) throws Exception{
        if (map!=null) {
            String sql = "INSERT into " + USER_DEL + " SELECT * from " + User + " where 1=1";
            for (String key : map.keySet()) {
                sql += " AND " + key + "='" + map.get(key) + "'";
            }
            querySql(sql);
        }else {
            throw  new Exception("????????????");
        }
    }
    public List<Map> getUserDel(Map map){
        String sql = " SELECT * FROM "+USER_DEL+" WHERE 1=1";
        if (map.get("id") != null && !("").equals(map.get("id"))) {
            sql += " AND ID=" + map.get("id");
        }
        if (map.get("limit") != null && !"".equals(map.get("limit"))) {
            sql += " limit " + (Integer.valueOf(map.get("page").toString()) - 1) * Integer.valueOf(map.get("limit").toString()) + "," + map.get("limit");
        }
        return querySql(sql);
    }
    public List<Map> getGrouping(){
        Map map=new HashMap();
        map.put("state",0);
       return queryTable(USER_GROUP,map);
    }

    public List<Map> getnewsList(Map map) {
      String sql="SELECT s.ID,s.state,su.MC,DATE_FORMAT(time, '%Y-%m-%d %T') as datetime,title FROM news s " +
              "INNER JOIN `user` su ON su.id=s.sendout_user_id " ;
      if (!isnull(map.get("type"))){
          sql += "WHERE s.type=" + map.get("type");
          if (map.get("type").equals("1")){
              sql += " AND s.receive_user_id="+map.get("user_id");
          }else {
              sql += " AND s.receive_user_id="+map.get("user_id");
          }
      }else {
          sql += " AND s.receive_user_id="+map.get("user_id");
      }
        sql += " limit " + (Integer.valueOf(map.get("page").toString()) - 1) * Integer.valueOf(map.get("limit").toString()) + "," + map.get("limit");
      return querySql(sql);
    }
    public List<Map> getnews(Map map) {
        return queryTable(NEWS,map);
    }
    public void updateNews(Map keys,Map values){
        updateTable(NEWS,keys,values);
    }
    public List getNotice(String id){
        String sql="SELECT * FROM "+SYS_NOTICE+" sn WHERE NOT EXISTS (SELECT * FROM " + NEWS
                +" ns WHERE ns.notice_id = sn.ID AND ns.receive_user_id = "+id+") AND sn.state=1";
        return querySql(sql);
    }
    public int addNews(Map map){
        return insertTable(NEWS,map);
    }

    /**
     * ????????????????????????
     * @param id ??????????????????ID
     * @return
     */
    public List getNewsCount(String id){
        String sql="SELECT *,noticeNum+newsNum AS allNum FROM (select SUM(if( type='0' && state='0' ,1,0) )as noticeNum, SUM(if( type='1' && state='0',1,0) )as newsNum from news b INNER JOIN user u ON b.sendout_user_id=u.ID WHERE receive_user_id="+id+") A";
        return querySql(sql);
    }


    public List getwxBinding(Map map){
       return queryTable(WX_USER,map);
    }

    /**
     * ??????????????????
     * @param keys
     * @param values
     * @return
     */
    public int updateUser(Map keys,Map values){
        return updateTable(User,keys,values);
    }
}
