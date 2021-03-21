package com.module.system.user.service;

import com.helei.api.common.ImplBase;
import com.helei.api.util.PwdUtil;
import com.module.common.CommonMethod;
import com.util.UploadFile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiService extends ImplBase {
    @Autowired
    DbService dbService;
    @Autowired
    private CommonMethod commonMethod;
    @Value("${file.staticAccessPath}")
    private String staticAccessPath;
    @Value("${file.uploadFolder}")
    private String uploadFolder;

    /**
     * 获取用户列表
     *
     * @param map 管理员登录信息
     * @return
     */
    public Map getUserList(Map<String, Object> map) {
        if (map != null) {
            try {
                if ("1".equals(map.get("grouping_id").toString())) {
                    List userList = dbService.getUserList((Map<String, Object>) map.get("data"));
                    return dbService.res_success(userList, userList.size());
                } else if (map.get("grouping_id") == null) {
                    throw new Exception("用户未登录");
                } else {
                    throw new Exception(map.get("username") + "用户无法访问指定数据");
                }
            } catch (Exception e) {
                dbService.setLog(e, commonMethod.getuserBM(), map);
                return dbService.res_error(e.getMessage());
            }
        } else {
            return dbService.res_error("未登录");
        }
    }

    public Map SetUserLock(Map map) {
        try {
            String lock;
            if ("true".equals(map.get("lock"))) {
                lock = "1";
            } else {
                lock = "0";
            }
            ;
            int number = dbService.UserLock(map.get("ID").toString(), lock);
            if (number > 0) {
                return dbService.getReturn_status("10003");
            } else {
                return dbService.getReturn_status("20003");
            }
        } catch (Exception e) {
            dbService.setLog(e, commonMethod.getuserBM(), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map getReturn_status(String code) {
        return dbService.getReturn_status(code);
    }

    /**
     * 根据ID 获取用户信息
     *
     * @param map 管理员登录信息
     * @return
     */
    public Map getUserByID(Map<String, Object> map) {
        try {
            if (map == null || "".equals(map.get("id"))) {
                return dbService.getReturn_status("20005");
            } else {
                List<Map> userList = dbService.getUserList(map);
                userList.get(0).put("Grouping", dbService.getGrouping());
                if (userList.size() > 0) {
                    Map res = dbService.getReturn_status("10000");
                    res.put("data", userList);
                    return res;
                } else {
                    return dbService.getReturn_status("20005");
                }
            }
        } catch (Exception e) {
            dbService.setLog(e, commonMethod.getuserBM(), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map UpdateUserInfo(Map map) {
        try {
            String id = map.get("ID").toString();
            map.remove("ID");
            int number = dbService.UpdateUserInfo(id, map);
            if (number > 0) {
                return dbService.getReturn_status("10003");
            } else {
                return dbService.getReturn_status("20003");
            }
        } catch (Exception e) {
            Map info = new HashMap();
            dbService.setLog(e, commonMethod.getuserBM(), map);
            return dbService.res_error(e.getMessage());
        }
    }

    @Transactional
    public Map deleteUser(Map map) {
        try {
            List list = dbService.getUserDel(map);
            if (list.size() == 0) {
                dbService.SetUserDelInfo(map);
            }
            dbService.DeleteUser(map);
            return dbService.getReturn_status("10004");
        } catch (Exception e) {
            dbService.setLog(e, commonMethod.getuserBM(), map);
            return dbService.getReturn_status("20000");
        }
    }

    public Map getGrouping() {
        List userList = dbService.getGrouping();
        if (userList.size() > 0) {
            Map res = dbService.getReturn_status("10000");
            res.put("data", userList);
            return res;
        } else {
            return dbService.getReturn_status("20005");
        }
    }

    public Map ConfirmPassword(Map map) {
        try {
            if (isnull(map.get("operation_pwd"))) {
                throw new Exception("口令不能为空!");
            }
            map.put("ID", commonMethod.getuserinfo().get("ID"));
            List<Map> userinfo = dbService.getUserinfo(new HashMap() {{
                put("ID", commonMethod.getuserinfo().get("ID"));
            }});
            if (isnull(userinfo.get(0).get("operation_pwd"))) {
                return dbService.getReturn_status("20011");
            }
            map.put("operation_pwd", PwdUtil.encodePwd(map.get("operation_pwd").toString()));
            List list = dbService.getUserinfo(map);
            if (list.size() > 0) {
                return dbService.getReturn_status("10000", "口令匹配成功");
            } else {
                return dbService.getReturn_status("20006", "口令不匹配");
            }
        } catch (Exception e) {
            getLogger().error(e);
            return dbService.getReturn_status("20000", e.getMessage());
        }
    }

    public Map getmsgList(Map map) {
        List newsList = dbService.getnewsList(map);
        if (newsList.size() > 0) {
            return dbService.res_success(newsList, newsList.size());
        } else {
            return dbService.res_error("消息为空");
        }
    }

    public Map getNews(String id) {
        Map user = commonMethod.getuserinfo();
        List newsList = dbService.getnews(new HashMap() {{
            put("ID", id);
            put("receive_user_id", user.get("ID"));
        }});
        Map news = (Map) newsList.get(0);
        if (news.get("state").equals("0")) {
            dbService.updateNews(new HashMap() {{
                put("ID", news.get("ID"));
            }}, new HashMap() {{
                put("state", 1);
            }});
        }
        if (newsList.size() > 0) {
            return dbService.getReturn_status("10000", news);
        } else {
            return dbService.getReturn_status("20000", "消息获取失败");
        }
    }

    /**
     * 检查公告写入消息表
     */
    @Transactional
    public void Receiving() {
        String id = commonMethod.getuserinfo("ID");
        List<Map> list = dbService.getNotice(id);
        if (list.size() > 0) {
            //写入公告到个人表
            for (Map m : list) {
                dbService.addNews(new HashMap() {{
                    put("title", m.get("title"));
                    put("content", m.get("content"));
                    put("receive_user_id", id);
                    put("sendout_user_id", m.get("publisher_id"));
                    put("type", 0);
                    put("state", 0);
                    put("notice_id", m.get("ID"));
                }});
            }
        }
    }

    /**
     * 检查有误系统公告插入到个人消息列表
     * @return
     */
    public Map getNumber_of_unread_messages() {
        String id = commonMethod.getuserinfo("ID");
        //查询是否存在记录
        List newlist=dbService.getnews(new HashMap(){{
            put("receive_user_id",id);
        }});
        if (newlist.size()==0){
            return new HashMap(){{
                put("allNum",0);
                put("noticeNum",0);
                put("newsNum",0);
            }};
        }
        List<Map>  list = dbService.getNewsCount(id);
        if (list.size() > 0) {
            //查询出数据全部为0 list 里面会有1条数据但无法操作,判断list 里不是MAP类型 返回默认数据
            if (list.get(0) instanceof Map){
                return list.get(0);
            }else {
                return new HashMap(){{
                    put("allNum",0);
                    put("noticeNum",0);
                    put("newsNum",0);
                }};
            }
        } else {
            return new HashMap(){{}};
        }
    }

    public Map getuserinfo(Map map) {
        try {
            if (isnull(map.get("ID")) && isnull(map.get("BM"))) {
                throw new Exception("查询参数不能为空");
            }
            List<Map> list = dbService.getUserinfo(map);
            if (list.size() > 0) {
                Map res_map = new HashMap() {{
                    put("username", list.get(0).get("username"));
                    put("BM", list.get(0).get("BM"));
                    put("ID", list.get(0).get("ID"));
                    put("MC", list.get(0).get("MC"));
                    put("grouping_name", list.get(0).get("grouping_name"));
                    put("email", list.get(0).get("email"));
                    if (!isnull(list.get(0).get("operation_pwd")))
                        put("oper_pwd", "已设置");
                    else
                        put("oper_pwd", "未设置");
                }};
                List<Map> wxlist = dbService.getwxBinding(new HashMap() {{
                    put("user_id", list.get(0).get("ID"));
                }});
                if (wxlist.size() > 0) {
                    res_map.put("wx", wxlist.get(0).get("nickname"));
                }
                //查询用户表是否有头像
                if (isnull(list.get(0).get("user_img"))) {
                    //查询微信是否存在绑定头像
                    if (wxlist.size() > 0 && !isnull(wxlist.get(0).get("imgurl"))) {
                        res_map.put("img", "/api/file/" + wxlist.get(0).get("imgurl"));
                    }
                } else {
                    res_map.put("img", "/api/file/" + list.get(0).get("user_img"));
                }
                return dbService.getReturn_status("10000", res_map);
            }
            throw new Exception("未查询到数据");
        } catch (Exception e) {
            getLogger().error(e);
            return dbService.getReturn_status("20000", e.getMessage());
        }
    }

    public Map upload_img(HttpServletRequest req, MultipartFile file) {
        try {
            UploadFile uploadFile = new UploadFile();
            String id = commonMethod.getuserID();
            Map ret = uploadFile.uploadFile(file, req, uploadFolder + "head_portrait/", id, new String[]{".jpg", ".png", ".gif"});
            if (ret.get("code").equals(0)) {
                int row = dbService.updateUser(new HashMap() {{
                    put("ID", id);
                }}, new HashMap() {{
                    put("user_img", ret.get("src").toString().replace(uploadFolder, ""));
                }});
                if (row > 0) {
                    ret.remove("src");
                    return ret;
                } else {
                    throw new Exception("更新头像失败");
                }
            } else {
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dbService.getReturn_status("20000", e.getMessage());
        }
    }

    public Map updatePwd(Map map) {
        try {
            if (isnull(map.get("oldPassword"))){
                throw  new Exception("旧密码不能为空");
            }
            if (isnull(map.get("password"))){
                throw  new Exception("新密码不能为空");
            }
            if (isnull(map.get("repassword"))){
                throw  new Exception("确认密码不能为空");
            }
            if (!map.get("password").equals(map.get("repassword"))){
                throw  new Exception("两次密码不一致");
            }
            if (map.get("oldPassword").equals(map.get("password"))){
                throw  new Exception("新密码不能和旧密码相同");
            }
            String id=isnull(map.get("id"))?commonMethod.getuserID():map.get("id").toString();
            List<Map> list=dbService.getUserinfo(new HashMap(){{
                put("ID",id);
            }} );
            if (list.size()==0){
                throw  new Exception("获取用户信息失败");
            }
            String encryptionPwd=PwdUtil.encodePwd(map.get("oldPassword").toString());
            if (!encryptionPwd.equals(list.get(0).get("password"))){
                throw  new Exception("旧密码不正确");
            }
            //修改密码
            int row=dbService.updateUser(new HashMap(){{
                put("ID",id);
            }},new HashMap(){{
                put("password",PwdUtil.encodePwd(map.get("password").toString()));
            }});
            if (row>0){
                Subject subject = SecurityUtils.getSubject();
                subject.logout();
                return dbService.getReturn_status("10000", "修改成功");
            }else {
                return dbService.getReturn_status("20000", "失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dbService.getReturn_status("20000", e.getMessage());
        }
    }
    public Map updateOperationPwd(Map map) {
        try {
            if (isnull(map.get("oldPassword"))){
                throw  new Exception("旧密码不能为空");
            }
            if (isnull(map.get("password"))){
                throw  new Exception("新密码不能为空");
            }
            if (isnull(map.get("repassword"))){
                throw  new Exception("确认密码不能为空");
            }
            if (!map.get("password").equals(map.get("repassword"))){
                throw  new Exception("两次密码不一致");
            }
            if (map.get("oldPassword").equals(map.get("password"))){
                throw  new Exception("新密码不能和旧密码相同");
            }
            String id=isnull(map.get("id"))?commonMethod.getuserID():map.get("id").toString();
            List<Map> list=dbService.getUserinfo(new HashMap(){{
                put("ID",id);
            }} );
            if (list.size()==0){
                throw  new Exception("获取用户信息失败");
            }
            String encryptionPwd=PwdUtil.encodePwd(map.get("oldPassword").toString());
            if (!encryptionPwd.equals(list.get(0).get("operation_pwd"))){
                throw  new Exception("旧密码不正确");
            }
            //修改密码
            int row=dbService.updateUser(new HashMap(){{
                put("ID",id);
            }},new HashMap(){{
                put("operation_pwd",PwdUtil.encodePwd(map.get("password").toString()));
            }});
            if (row>0){
                return dbService.getReturn_status("10000", "修改成功");
            }else {
                return dbService.getReturn_status("20000", "失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dbService.getReturn_status("20000", e.getMessage());
        }
    }
}
