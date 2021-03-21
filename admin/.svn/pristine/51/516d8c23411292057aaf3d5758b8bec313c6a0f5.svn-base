package com.module.system.authority.service;

import com.helei.api.db.DbAgent;
import com.helei.api.mapper.CommonDao;
import com.sdicons.json.validator.impl.predicates.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("AuthDbService")
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class DbService extends DbAgent {
    @Autowired
    protected HttpServletRequest request;
    //----new--
    public Map getuserinfoMap() {
        return  (Map<String, Object>) request.getSession().getAttribute("user");
    }


    public DbService(CommonDao dao) {
        super(dao);
    }
    public List<Map> getGrouping(){
        String sql="select  u.ID,grouping_name,state,`remarks`,menu_id from "+USER_GROUP+" u" +
                " LEFT JOIN "+AUTHORITY+" a ON a.grouping_id=u.ID";
        return querySql(sql);
    }
    public List<Map> getAuthority(Map<String,Object> param){
        String sql="SELECT * FROM "+AUTHORITY+" WHERE 1=1 ";
        for (String key:param.keySet()){
            if (!key.equals("group"))
                sql+=" AND "+key+" like '%"+param.get(key)+"%'";
            else
                sql+=" AND grouping_id in ("+param.get(key)+")";
        }

        return querySql(sql);
    }

    /**
     * 查询菜单表
     * @param map
     * @return
     */
    public List<Map> getMenu(Map map){
        return queryTable(MENU,map);
    }

    /**
     * 查询菜单权限,多条件匹配查询出父节点
     * @param parent_id
     * @param id
     * @return
     */
    public List<Map> getMenu(String parent_id,String id){
        String sql="SELECT CASE ID WHEN "+id+" THEN concat(`name`,'访问') ELSE `name` END AS `name`,M.* FROM menu M WHERE parent_id = "+parent_id+" OR ID ="+id;
        return querySql(sql);
    }
    public int UpdateAuthority(Map where,Map parameter){
        return updateTable(AUTHORITY,where,parameter);
    }
    public int AddAuthority(Map parameter){
        return insertTable(AUTHORITY,parameter);
    }

    public List getMenulist(){
        String sql="SELECT m.ID AS id, type,parent_id as pid,name as title,url,icon,m.state,group_concat(u.grouping_name) AS UserGroup,group_concat(u.ID) as UserGroupID FROM "+MENU+" m " +
                "LEFT JOIN "+AUTHORITY+" a ON a.menu_id LIKE concat('%,', m.ID, ',%') or a.menu_id LIKE concat(m.ID, ',%') or a.menu_id LIKE concat('%,', m.ID) " +
                "LEFT JOIN "+USER_GROUP+" u ON u.ID=a.grouping_id  group by m.ID ORDER  BY m.sort";
        return querySql(sql);
    };
    public int setMenuLock(String id, String lock) {
        return updateTable(MENU, new HashMap() {
                    {
                        put("id", id);
                    }
                }, new HashMap() {
                    {
                        put("state", lock);
                    }
                }
        );
    }
    public List getMenubinding(Map<String,Object> map){
        String sql="SELECT * FROM  "+AUTHORITY+" WHERE 1=1 ";
        for (String key:map.keySet()){
            if (!"id".equals(key)){sql+=" AND "+key+" IN ("+map.get(key)+")";}
        }
        String str="";
        if (!isnull(map.get("id"))) {
            Object id=map.get("id");
            sql += " AND" + str + "(menu_id LIKE concat('%,', " + id + ", ',%') OR menu_id LIKE concat(" + id + ", ',%') OR menu_id LIKE concat('%,', " + id + "))";
        }
        return querySql(sql);
    }
    public void  delMenu(Map map){
        deleteTable(MENU,map);
    }
    public List getUser(Map map){
        String sql="SELECT * FROM "+User+" WHERE grouping_id="+map.get("ID");
        return querySql(sql);
    }

    /**
     * 删除用户组
     * @param map
     */
    public void  delGroup(Map map){
        deleteTable(USER_GROUP,map);
    }
    public List getGrouping(Map<String,Object> map){
        String sql="SELECT * FROM "+USER_GROUP+" WHERE 1=1 ";
        if (map!=null) {
            for (String key : map.keySet()) {
                if (!key.equals("limit") && !key.equals("page") && !map.get(key).equals("")) {
                    sql += " AND " + key + " like '%" + map.get(key) + "%'";
                }
            }
            if (map.get("limit") != null && !"".equals(map.get("limit"))) {
                sql += " limit " + (Integer.valueOf(map.get("page").toString()) - 1) * Integer.valueOf(map.get("limit").toString()) + "," + map.get("limit");
            }
        }
        return querySql(sql);
    }
    public int addGroup(Map map){
        return insertTableGetID(USER_GROUP,map);
    }
    public int updateGroup(Map key,Map map){
        return updateTable(USER_GROUP,key,map);
    }
    public int setGroupLock(Map key, Map parameter) {
        return updateTable(USER_GROUP, key, parameter);
    }
    public int addMenu(Map m){
        return insertTableGetID(MENU,m);
    }
    public Map getMenuSort(String parent_id){
        String sql="SELECT MAX(sort+1) as sort FROM "+MENU+" WHERE parent_id="+parent_id;
        List<Map> list=querySql(sql);
        if (list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }

    public int updateMenu(Map key,Map parameter){
        return updateTable(MENU,key,parameter);
    }
    public int  addAuthority(Map map){
        return insertTable(AUTHORITY,map);
    }

    /**
     * 删除权限表
     * @param map
     */
    public void   delAuthority(Map map){
         deleteTable(AUTHORITY,map);
    }

    /**
     * 获取系统配置表
     * @return
     */
    public List getConfig(){
        return queryTable(CONFIG);
    }

    /**
     * 获取邮件配置表
     * @return
     */
    public List getEmaillimit(Map<String,Object> map){
        String sql="SELECT * FROM  "+EMAIL_NOTICE+" WHERE 1=1 ";
        if (map!=null) {
            for (String key : map.keySet()) {
                if (!key.equals("limit") && !key.equals("page") && !map.get(key).equals("")) {
                    sql += " AND " + key + " like '%" + map.get(key) + "%'";
                }
            }
            if (map.get("limit") != null && !"".equals(map.get("limit"))) {
                sql += " limit " + (Integer.valueOf(map.get("page").toString()) - 1) * Integer.valueOf(map.get("limit").toString()) + "," + map.get("limit");
            }
        }
        return querySql(sql);
    }

    public int addEmail(Map map){
        return insertTable(EMAIL_NOTICE,map);
    }
    public int updateEmail(Map key,Map value){
        return updateTable(EMAIL_NOTICE,key,value);
    }
    public List getEmail(Map map){
        return queryTable(EMAIL_NOTICE,map);
    }
    public void delEmail(Map map){
        deleteTable(EMAIL_NOTICE,map);
    }
    public List getEmail_TemplateList(Map<String,Object> map){
        String sql="SELECT ID,title,remarks,type,state,is_default FROM  "+EMAIL_TEMPLATE+" WHERE 1=1 ";
        if (map!=null) {
            for (String key : map.keySet()) {
                if (!key.equals("limit") && !key.equals("page") && !map.get(key).equals("")) {
                    sql += " AND " + key + " like '%" + map.get(key) + "%'";
                }
            }
            if (map.get("limit") != null && !"".equals(map.get("limit"))) {
                sql += " limit " + (Integer.valueOf(map.get("page").toString()) - 1) * Integer.valueOf(map.get("limit").toString()) + "," + map.get("limit");
            }
        }
        return  querySql(sql);
    }
    public List getEmail_Template(Map<String,Object> map){
        return  queryTable(EMAIL_TEMPLATE,map);
    }
    public int addEmail_Template(Map<String,Object> map){
        return  insertTable(EMAIL_TEMPLATE,map);
    }
    public int updateEmail_Template(Map<String,Object> keys,Map<String,Object> values){
        return  updateTable(EMAIL_TEMPLATE,keys,values);
    }
    public void deleteEmail_Template(Map<String,Object> map) throws Exception {
        Test_data_lock(EMAIL_TEMPLATE,map.get("ID"));
        deleteTable(EMAIL_TEMPLATE,map);
        relieve_data_lock(EMAIL_TEMPLATE,map.get("ID"));
    }

    /**
     * 设置表锁
     */
    public void setlate_lock(String tablename,Object ID) throws Exception {
        //查询是否有锁
        Test_data_lock(tablename,ID);
        //查询是否存在锁
        List<Map> res=getdata_lock(tablename,ID);
        if (res.size()>0){
            //更新表锁
            updatedata_lock(tablename,ID);
        }else {
            //添加表锁
            Add_data_lock(tablename,ID);
        }
    }
    public  List getTemplatecontentType(){
        String sql="SELECT type FROM "+EMAIL_TEMPLATE+" GROUP BY type";
        return querySql(sql);
    }
}
