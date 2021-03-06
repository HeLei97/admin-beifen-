package com.module.system.login.service;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.helei.api.util.*;
import com.module.common.CommonMethod;
import com.util.JavaMailSenderImpl;
import com.util.MailService;
import com.util.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import com.helei.api.common.ImplBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@SuppressWarnings({"serial", "unchecked", "rawtypes"})
@Service("sysApiService")
public class ApiService extends ImplBase {

    @Autowired
    private DbService dbService;

    @Autowired
    private HttpServletRequest request;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private MailService mailService;

    @Autowired
    private CommonMethod commonMethod;


    //private Map<String,Object> userinfo=(Map<String, Object>) request.getSession().getAttribute("user");

    private Map<String,Object> userinfo=null;
    //----new--
    public Map getuserinfoMap(){
        if (userinfo==null){
            userinfo=(Map<String, Object>) request.getSession().getAttribute("user");
        }
        return userinfo;
    }

    /**
     * 判断用户是否注册,写入用户表
     *
     * @param key
     * @return
     */
    public Map queryOperator(String key) {
        return dbService.queryOperator(key);
    }

    /**
     * 注册账号
     *
     * @param map
     * @return
     */
    @Transactional
    public Map register(Map map) {
        try {
            if (isnull(map.get("username")) || isnull(map.get("MC"))|| isnull(map.get("password")) || isnull(map.get("email")) || isnull(map.get("email_code")) )
            {
                throw new Exception("表单数据不完整");
            }
            String email_code=redisUtil.get("email_code:"+map.get("email"));
            if (!map.get("email_code").equals("DEBUG.")&&!map.get("email_code").equals(email_code)){
                throw new Exception("邮箱验证失败,验证码错误!");
            }
            Object type=map.get("type");
            String password=PwdUtil.encodePwd(map.get("password").toString());
            List Userlist;
            if (!isnull(type)) {
                String uuid=map.get("uuid").toString();
                String key=map.get("key").toString();
                if (isnull(uuid) || isnull(key)){
                    return dbService.getReturn_status("20001","参数错误");
                }else {
                    String str=redisUtil.get(key).toString();
                    if (isnull(str)){
                        return dbService.getReturn_status("20001","请求已失效");
                    }else {
                        String jiemi= DESUtil.getDecryptString(uuid,str);
                        Map data=jsonToMap(jiemi);
                        String openid=data.get("openid").toString();
                        String BM=dbService.GetUserBm();
                        Userlist = dbService.getUserInfo(map);
                        if (Userlist.size() > 0) {
                            return dbService.getReturn_status("20001");
                        } else {
                            Map user=new HashMap(){{
                                put("MC",map.get("MC"));
                                put("BM",BM);
                                put("username",map.get("username"));
                                put("password",password);
                                put("email",map.get("email"));
                                put("grouping_id","2");
                                put("grouping_name","会员");
                            }};
                            dbService.SetUser(user);
                            int number=dbService.update_wx_user(new HashMap(){{
                                put("openid",openid);
                            }},new HashMap(){{
                                put("user_id",user.get("ID"));
                            }});
                            redisUtil.delete("email_code:"+map.get("email"));
                            if (number>0){
                                return new HashMap() {
                                    {
                                        String un = request.getParameter("username");
                                        String pwd = PwdUtil.encodePwd(request.getParameter("password"));
                                        UsernamePasswordToken token = new UsernamePasswordToken(un, pwd);
                                        Subject subject = SecurityUtils.getSubject();
                                        try {
                                            subject.login(token);
                                            getLogger().info("对用户：[" + un + "]进行登录验证,验证通过!");
                                        } catch (UnknownAccountException e) {
                                            subject.logout();
                                            getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：未知账号");
                                            put("code", 0);
                                            put("status", "Erro");
                                            put("msg", "用户名或密码错误,请重新登录");
                                        } catch (IncorrectCredentialsException e) {
                                            subject.logout();
                                            getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：密码错误");
                                            put("code", 0);
                                            put("status", "Erro");
                                            put("msg", "用户名或密码错误,请重新登录");
                                        } catch (LockedAccountException e) {
                                            subject.logout();
                                            getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：账号被锁定");
                                            put("code", 0);
                                            put("status", "Erro");
                                            put("msg", "该账户账号已锁定");
                                        } catch (ExcessiveAttemptsException e) {
                                            subject.logout();
                                            getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：错误次数过多");
                                            put("code", 0);
                                            put("status", "Erro");
                                            put("msg", "该账户账号已锁定");
                                        } catch (AuthenticationException e) {
                                            subject.logout();
                                            getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：用户名或密码不正确");
                                            put("code", 0);
                                            put("status", "Erro");
                                            put("msg", "用户名或密码错误,请重新登录");
                                        } catch (Exception e){
                                            subject.logout();
                                            getLogger().info("对用户：[" + un + "]进行登录验证,验证未通过!错误：" + e.getMessage());
                                            put("code", 0);
                                            put("status", "Erro");
                                            put("msg", "用户名或密码错误,请重新登录");
                                        }
                                        if (subject.isAuthenticated()) {
                                            put("code", 1);
                                            put("status", "ok");
                                            put("msg", "登录成功<br /><br />欢迎回来");
                                            Map user = (Map)subject.getPrincipal();
                                            Object a=subject.getSession();
                                            request.getSession().setAttribute("user", user);
                                        }
                                    }
                                };
                            }else {
                                return new HashMap(){
                                    {
                                        put("code", 0);
                                        put("status", "Erro");
                                        put("msg", "请求错误");
                                    }
                                };
                            }
                        }
                    }
                }
            }else {
                String BM=dbService.GetUserBm();
                Userlist = dbService.getUserInfo(map);
                if (Userlist.size() > 0) {
                    return dbService.getReturn_status("20001");
                } else {
                    redisUtil.delete("email_code:"+map.get("email"));
                    return dbService.SetUser(new HashMap(){{
                        put("username",map.get("username"));
                        put("BM",BM);
                        put("MC",map.get("MC"));
                        put("password",password);
                        put("email",map.get("email"));
                    }});
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            getLogger().warn(e);
            dbService.setLog(e,null,map);
            return dbService.getReturn_status("20000",e.getMessage() );
        }
    }

    public Map queryMenuTree(Map map) {
        try {
            String grouping = map.get("grouping_id").toString();
            List<Map> AuthList = dbService.getAuthority(grouping);
            String menu_list;
            Map res=new HashMap();
            if (AuthList.size() == 0) {
                menu_list="";
            } else {
                menu_list = AuthList.get(0).get("menu_id").toString();
            }
            if (!menu_list.equals("")) {
                res = dbService.getReturn_status("10000");
                List<Map> list = dbService.queryPermissionByUser(menu_list);
                for (Map Menumap : list) {
                    List<Map> list1 = dbService.getSubmenu(menu_list, Menumap.get("ID").toString());
                    Menumap.put("Submenu", list1);
                }
                request.getSession().setAttribute("menu", list);
                res.put("data", list);
            }
//            }else{
//                throw  new Exception("获取菜单失败");
//            }
            return res;
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e,getuserinfoMap().get("username"),map);
            return dbService.getReturn_status("20000");
        }
    }

    public List queryUser() {
        try {
            return dbService.queryUser();
        } catch (Exception e) {
            getLogger().warn(e);
            return null;
        }
    }
    public List queryMenu() {
        try {
            return dbService.queryMenu();
        } catch (Exception e) {
            getLogger().warn(e);
            return null;
        }
    }
    public List queryUserRole(int userId) {
        try {
            return dbService.queryUserRole(userId);
        } catch (Exception e) {
            getLogger().warn(e);
            return null;
        }
    }
    public List queryPermissionByUser(String groupingID) {
        try {
            List<Map> AuthList=dbService.getAuthority(groupingID);
            String Mebuids=AuthList.get(0).get("menu_id").toString();
            return dbService.queryPermissionByUser(Mebuids);
        } catch (Exception e) {
            getLogger().warn(e);
            return null;
        }
    }
    public String getIpAddr(HttpServletRequest request){
        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
    public Map wxlogin(Map map){
        String uuid=map.get("uuid").toString();
        String key=map.get("key").toString();
        Object str=redisUtil.get(key);
        String jiemi = DESUtil.getDecryptString(uuid, str.toString());
        Map wxmap = jsonToMap(jiemi);
        Object openid = wxmap.get("openid");
        String un="";
        String pwd="";
        if (!isnull(str)){
            boolean flag=true;
            if (!isnull(map.get("type")) && "binding".equals(map.get("type"))){
                un = request.getParameter("username");
                pwd = PwdUtil.encodePwd(request.getParameter("password"));
            }else {
                flag=false;
                List<Map> list=dbService.getwxbinding(openid.toString());
                if (list.size()>0) {
                    un = list.get(0).get("username").toString();
                    pwd = list.get(0).get("password").toString();;
                }else {
                    return  new HashMap(){{
                    put("code", 2);
                    put("status", "Erro");
                    put("msg", "未绑定会员");
                    }};
                }
            }
            boolean finalFlag = flag;
            Object finalOpenid = openid;
            String finalUn = un;
            String finalPwd = pwd;
            return new HashMap() {
            {
                UsernamePasswordToken token = new UsernamePasswordToken(finalUn, finalPwd);
                Subject subject = SecurityUtils.getSubject();
                try {
                    subject.login(token);
                    getLogger().info("对用户：[" + finalUn + "]进行登录验证,验证通过!");
                } catch (UnknownAccountException e) {
                    subject.logout();
                    getLogger().info("对用户：[" + finalUn + "]进行登录验证,验证未通过!错误：未知账号");
                    put("code", 0);
                    put("status", "Erro");
                    put("msg", "用户名或密码错误,请重新登录");
                } catch (IncorrectCredentialsException e) {
                    subject.logout();
                    getLogger().info("对用户：[" + finalUn + "]进行登录验证,验证未通过!错误：密码错误");
                    put("code", 0);
                    put("status", "Erro");
                    put("msg", "用户名或密码错误,请重新登录");
                } catch (LockedAccountException e) {
                    subject.logout();
                    getLogger().info("对用户：[" + finalUn + "]进行登录验证,验证未通过!错误：账号被锁定");
                    put("code", 0);
                    put("status", "Erro");
                    put("msg", "该账户账号已锁定");
                } catch (ExcessiveAttemptsException e) {
                    subject.logout();
                    getLogger().info("对用户：[" + finalUn + "]进行登录验证,验证未通过!错误：错误次数过多");
                    put("code", 0);
                    put("status", "Erro");
                    put("msg", "该账户账号已锁定");
                } catch (AuthenticationException e) {
                    subject.logout();
                    getLogger().info("对用户：[" + finalUn + "]进行登录验证,验证未通过!错误：用户名或密码不正确");
                    put("code", 0);
                    put("status", "Erro");
                    put("msg", "用户名或密码错误,请重新登录");
                } catch (Exception e){
                    subject.logout();
                    getLogger().info("对用户：[" + finalUn + "]进行登录验证,验证未通过!错误：" + e.getMessage());
                    put("code", 0);
                    put("status", "Erro");
                    put("msg", "用户名或密码错误,请重新登录");
                }
                if (subject.isAuthenticated()) {
                    put("code", 1);
                    put("status", "ok");
                    put("msg", "登录成功<br /><br />欢迎回来");
                    Map user = (Map) subject.getPrincipal();
                    Object a = subject.getSession();
                    request.getSession().setAttribute("user", user);
                    if (finalFlag) {
                        int number = dbService.update_wx_user(new HashMap() {{
                            put("openid", finalOpenid);
                        }}, new HashMap() {{
                            put("user_id", user.get("id"));
                        }});
                        if (number > 0) {
                            redisUtil.delete(key);
                        }else {
                            subject.logout();
                            put("code", 0);
                            put("status", "Erro");
                            put("msg", "绑定失败");
                        }
                    }else {
                        redisUtil.delete(key);
                    }
                }
            }
        };}else {
            return new HashMap(){
                {
                    put("code", 0);
                    put("status", "Erro");
                    put("msg", "请求错误");
                }
            };
        }
    }

    /**
     * 获取用户头像
     * @param map
     * @return
     */
    public Map getimg(Map map) {
        try {
            List<Map> list = dbService.getuser_info(new HashMap() {{
                put("BM", map.get("BM"));
            }});
            String url = "";
            Map user_map = list.get(0);
            if (isnull(user_map.get("user_img"))) {
                if (isnull(user_map.get("imgurl"))) {
                    return dbService.getReturn_status("20000");
                } else {
                    byte[] bytes = Base64Util.imageTobyte(commonMethod.uploadFolder+user_map.get("imgurl").toString());
                    return dbService.getReturn_status("10000", "data:image/jpg;base64," + Base64Util.encode(bytes));
                }
            } else {
                byte[] bytes = Base64Util.imageTobyte(commonMethod.uploadFolder+user_map.get("user_img").toString());
                return dbService.getReturn_status("10000", "data:image/jpg;base64," + Base64Util.encode(bytes));
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, null, null);
            return dbService.getReturn_status("20000");
        }
    }
    public Map getEmailCode(Map map){
        try {
            if (isnull(map.get("email"))){
                throw new Exception("email不能为空");
            }
            String emailreg = "[a-zA-Z0-9]{3,20}@([a-zA-Z0-9]{2,10}|[a-zA-Z0-9]{2,10}[.][a-zA-Z0-9]{2,10})[.](com|cn|net)";
            if (!map.get("email").toString().matches(emailreg)) {
                throw new Exception("email格式不正确");
            }
            String radom=RadomUtil.randomStr(6);
            List<Map> list=dbService.getEmail_Template(new HashMap(){{
                put("type","register");
                put("state","0");
            }});
            if (list.size()<0){
                throw new Exception("未配置注册邮件模板");
            }
            int time=CommUtil.checkIntVal(commonMethod.getConfig("reg_email_code_time"),10);
            String content= MessageFormat.format(list.get(0).get("content").toString(), radom, DateUtils.addSeconds(time),time/60);
            Map resMap=send_out_email_code(map.get("email").toString(),"注册验证码",content);
            if (resMap.get("code").equals(0)) {
            boolean bool=redisUtil.set("email_code:"+map.get("email").toString(),radom,1000);
            if (bool){
                return dbService.getReturn_status("10000","验证码已下发");
            }else {
                throw new Exception("保存出错");
            }
            }else {
                return resMap;
            }
        } catch (Exception e) {
            getLogger().warn(e);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }

    public Map send_out_email_code(String to,String subject,String content){
        try {
            List<Map> list=dbService.getEmail_config();
            for (Map map:list) {
                JavaMailSender javaMailSender=new JavaMailSenderImpl(map.get("Host").toString(),map.get("Username").toString(),map.get("Password").toString(),true).GetJavaMailSender();
                Map resMap=mailService.sendSimpleMail(javaMailSender,to,subject,content);
                if (resMap.get("code").equals(0)) {
                    return resMap;
                }else {

                }
            }
            return null;
        } catch (Exception e) {
            getLogger().warn(e);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
}
