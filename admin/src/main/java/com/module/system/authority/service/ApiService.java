package com.module.system.authority.service;


import com.helei.api.common.ImplBase;
import com.helei.api.util.ArrayUtils;
import com.helei.api.util.StringUtils;
import com.util.JavaMailSenderImpl;
import com.util.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.*;

@SuppressWarnings({"serial", "unchecked", "rawtypes"})
@Service("AuthApiService")
public class ApiService extends ImplBase {

    @Autowired
    private DbService dbService;

    @Autowired
    private MailService mailService;

    @Autowired
    private HttpServletRequest request;

    //private Map<String,Object> userinfo=(Map<String, Object>) request.getSession().getAttribute("user");

    //----new--
    public Map<String, Object> getuserinfoMap() {
        return  (Map<String, Object>) request.getSession().getAttribute("user");
    }

    public Map getUserGroup(Map map) {
        try {
            List list = dbService.getGrouping(map);
            if (list.size() > 0) {
                return dbService.res_success(list, list.size());
            } else {
                return dbService.getReturn_status("20000");
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000");
        }
    }

    public Map getMenu(Map map) {
        try {
            List<Map> menu = new ArrayList<>();
            List<Map> Menulist = dbService.getMenu(new HashMap() {{
                put("parent_id", 0);
            }});
            List<Map> list = dbService.getAuthority(new HashMap() {{
                put("grouping_id", map.get("grouping"));
            }});
            for (Map m : Menulist) {
                Map Parentmenu = new HashMap();
                Parentmenu.put("title", m.get("name"));
                Parentmenu.put("id", m.get("ID"));
                Parentmenu.put("field", m.get("ID"));
                List<Map> Submenu = dbService.getMenu(new HashMap() {{
                    put("parent_id", m.get("ID"));
                }});
                //?????????????????????
                if (list.size() > 0) {
                    String menu_id = list.get(0).get("menu_id").toString();
                    String[] menulist = menu_id.split(",");
                    //??????????????????????????????
                    if (StringUtils.existStr(menulist, m.get("ID").toString())) {
                        Parentmenu.put("spread", true);
                        //Parentmenu.put("checked", true);
                    } else {
                        Parentmenu.put("spread", false);
                        //Parentmenu.put("checked", false);
                    }
                } else {
                    //Parentmenu.put("spread", false);
                }

                for (Map Mmap : Submenu) {
                    Map Submenumap = new HashMap();
                    Submenumap.put("title", Mmap.get("name"));
                    Submenumap.put("id", Mmap.get("ID"));
                    Submenumap.put("field", Mmap.get("ID"));
                    Submenumap.put("spread", false);
                    //?????????????????????????????????
                    if (list.size() > 0) {
                        String menu_id = list.get(0).get("menu_id").toString();
                        String[] menulist = menu_id.split(",");
                        //??????????????????????????????
                        if (StringUtils.existStr(menulist, Mmap.get("ID").toString())) {
                            Submenumap.put("spread", true);
                            //Submenumap.put("checked", true);
                        } else {
                            Submenumap.put("spread", false);
                            //Submenumap.put("checked", false);
                        }
                    } else {
                        //Submenumap.put("checked", false);
                    }
                    List<Map> butmenu = dbService.getMenu(Mmap.get("ID").toString(), Mmap.get("ID").toString());

                    //??????????????????
                    for (Map butmap : butmenu) {
                        Map Butmenumap = new HashMap();
                        Butmenumap.put("title", butmap.get("name"));
                        //??????ID?????????????????????,???????????????ID????????????,??????????????????????????????
                        if (butmap.get("ID").equals(Mmap.get("ID"))) {
                            Butmenumap.put("field", "-" + butmap.get("ID"));
                            Butmenumap.put("id", "-" + butmap.get("ID"));
                        } else {
                            Butmenumap.put("field", butmap.get("ID"));
                            Butmenumap.put("id", butmap.get("ID"));
                        }
                        Butmenumap.put("spread", false);
                        //?????????????????????????????????
                        if (list.size() > 0) {
                            String menu_id = list.get(0).get("menu_id").toString();
                            String[] menulist = menu_id.split(",");
                            //??????????????????????????????
                            if (StringUtils.existStr(menulist, butmap.get("ID").toString())) {
                                Butmenumap.put("checked", true);
                            } else {
                                //Butmenumap.put("checked", false);
                            }
                        } else {
                            //Butmenumap.put("checked", false);
                        }
                        //?????????map??????,??????????????????????????????????????????
                        Collections.replaceAll(butmenu, butmap, Butmenumap);
                        Submenumap.put("children", butmenu);
                    }
                    //?????????map??????,??????????????????????????????????????????
                    Collections.replaceAll(Submenu, Mmap, Submenumap);
                    Mmap.putAll(Parentmenu);
                }
                Parentmenu.put("children", Submenu);
                menu.add(Parentmenu);
            }
            return dbService.getReturn_status("10000", menu);
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000");
        }
    }

    @Transactional
    public Map UpdateAuthority(Map map) {
        try {
            Map grouping_id = new HashMap();
            grouping_id.put("grouping_id", map.get("grouping_id"));
            map.remove("grouping_id");
            if (dbService.UpdateAuthority(grouping_id, map) > 0) {
                return dbService.getReturn_status("10003");
            } else {
                int number = dbService.AddAuthority(new HashMap() {{
                    putAll(grouping_id);
                    putAll(map);
                }});
                if (number > 0) {
                    return dbService.getReturn_status("10003");
                } else {
                    return dbService.getReturn_status("20003");
                }
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000");
        }
    }

    public List getMenulist() {
        return dbService.getMenulist();
    }

    public Map setMenuLock(Map map) {
        try {
            String lock;
            if ("true".equals(map.get("lock"))) {
                lock = "1";
            } else {
                lock = "0";
            }
            ;
            int number = dbService.setMenuLock(map.get("id").toString(), lock);
            if (number > 0) {
                return dbService.getReturn_status("10003");
            } else {
                return dbService.getReturn_status("20003");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map delMenu(Map map) {
        try {
            //??????????????????????????????
            List list = dbService.getMenubinding(map);
            //???????????????????????????
            List lists = dbService.getMenu(new HashMap() {{
                put("parent_id", map.get("id"));
            }});
            if (list.size() > 0 || lists.size() > 0) {
                return dbService.getReturn_status("20007");
            } else {
                dbService.delMenu(map);
                return dbService.getReturn_status("10004");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map DeleteGroup(Map map) {
        try {
            //??????????????????????????????
            List list = dbService.getUser(map);
            if (list.size() > 0) {
                return dbService.getReturn_status("20007");
            } else {
                dbService.delGroup(map);
                dbService.delAuthority(new HashMap() {{
                    put("grouping_id", map.get("ID"));
                }});
                return dbService.getReturn_status("10004");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    @Transactional
    public Map addGroup(Map map) {
        try {
            //??????????????????????????????
            List list = dbService.getGrouping(new HashMap() {{
                put("grouping_name", map.get("grouping_name"));
            }});
            if (list.size() > 0) {
                return dbService.getReturn_status("20008");
            } else {
                int number = dbService.addGroup(map);
                BigInteger bi = (BigInteger) map.get("ID");//?????????????????????ID
                int ID = bi.intValue();
                number = dbService.addAuthority(new HashMap() {{
                    put("grouping_id", ID);
                    put("menu_id", "");
                }});
                if (number > 0) {
                    return dbService.getReturn_status("10001");
                } else {
                    return dbService.getReturn_status("20010");
                }
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map getGroup(Map map) {
        try {
            //??????????????????????????????
            List list = dbService.getGrouping(map);
            if (list.size() > 0) {
                return dbService.getReturn_status("10000", list.get(0));
            } else {
                return dbService.getReturn_status("20009");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map updateGroup(Map map) {
        try {
            Map key = new HashMap();
            key.put("ID", map.get("ID"));
            map.remove("ID");
            //??????????????????????????????
            int number = dbService.updateGroup(key, map);
            if (number > 0) {
                return dbService.getReturn_status("10003");
            } else {
                return dbService.getReturn_status("20003");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map setGroupLock(Map map) {
        try {
            String lock;
            if ("true".equals(map.get("lock"))) {
                lock = "1";
            } else {
                lock = "0";
            }
            ;
            int number = dbService.setGroupLock(new HashMap() {
                {
                    put("ID", map.get("ID"));
                }
            }, new HashMap() {
                {
                    put("state", lock);
                }
            });
            if (number > 0) {
                return dbService.getReturn_status("10001");
            } else {
                return dbService.getReturn_status("20004");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    @Transactional
    public Map addMenu(Map<String, Object> map) {
        try {
            String type = map.get("type").toString();//?????????????????????????????????
            String group = "";
            if (!"".equals(map.get("group")) && map.get("group") != null) {
                group = map.get("group").toString();//?????????????????????
            }
            String sotr;
            map.remove("group");//??????????????????
            if (type.equals("0")) {//??????????????????????????????id 0;
                map.put("parent_id", "0");
                Map m = dbService.getMenuSort("0");//?????????????????????
                if (m.size() > 0) {
                    map.putAll(m);//??????MAP
                }
            } else {
                Map m = dbService.getMenuSort(map.get("parent_id").toString());//?????????????????????
                if (m != null && m.size() > 0) {
                    map.putAll(m);//??????MAP
                } else {
                    map.put("sort", 0);
                }
            }
//            if (type.equals("3")){
//                map.put("BM", RadomUtil.generateNumber(4));
//            }
            int Number = dbService.addMenu(map);//???????????????????????????ID
            BigInteger bi = (BigInteger) map.get("ID");//?????????????????????ID
            int ID = bi.intValue();
            if (group.equals("")) {
                group = String.valueOf(ID);//??????????????????????????????????????????ID
            } else {
                group += "," + String.valueOf(ID);//??????????????????????????????ID????????????ID
            }
            if (Number > 0) {//???????????????????????????????????????
                if (!group.equals("")) {//???????????????????????????
                    String finalGroup = group;
                    List<Map> list = dbService.getAuthority(new HashMap() {
                        {
                            put("group", finalGroup);
                        }
                    });//?????????????????????????????????????????????????????????
                    for (Map m : list) {
                        String menu_id = m.get("menu_id").toString();
                        String[] menu = menu_id.split(",");
                        String[] groups = group.split(",");
                        String str = ArrayUtils.ArrayMergingtoString(menu, groups);//?????????????????????????????????
                        Number = dbService.UpdateAuthority(new HashMap() {{
                                                               put("ID", m.get("ID"));
                                                           }},
                                new HashMap() {{
                                    put("menu_id", str);
                                }});//??????????????????
                        if (Number == 0) {
                            return dbService.getReturn_status("20003");
                        }
                    }
                }
                return dbService.getReturn_status("10003");
            } else {
                return dbService.getReturn_status("20003");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map getParentMenu(Map map) {
        try {
            Object parent_id = isnull(map.get("parent_id"), 0);
            Object type = isnull(map.get("type"), 0);
            //????????????????????????????????????
            List list = dbService.getMenu(new HashMap() {{
                if (parent_id.equals(0)) {
                    switch (type.toString()) {
                        case "1":
                            put("type", 0);
                            break;
                        case "2":
                            put("type", 1);
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (type.toString()) {
                        case "1":
                            put("type", 0);
                            break;
                        case "2":
                            put("ID", parent_id);
                            break;
                        default:
                            break;
                    }
                }
            }});
            return dbService.getReturn_status("10000", list);
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map getUsergroupAll(Map map) {
        try {
            List list = dbService.getGrouping(null);
            return dbService.getReturn_status("10000", list);
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map getMenuInfo(Map map) {
        try {
            List list = dbService.getMenu(map);
            return dbService.getReturn_status("10000", list);
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    @Transactional
    public Map updateMenu(Map<String, Object> map) {
        try {
            String ID = map.get("ID").toString();
            String group = "";
            if (!isnull(map.get("group"))) {
                group = map.get("group").toString();
            }
            String type = map.get("type").toString();
            map.remove("ID");
            map.remove("group");
            map.remove("type");
            int number = dbService.updateMenu(new HashMap() {{
                put("ID", ID);
            }}, map);
            if (number > 0 && !isnull(group)) {
                //???????????????????????????????????????
                String finalGroup = group;
                List<Map<String, Object>> list = dbService.getMenubinding(new HashMap() {
                    {
                        put("grouping_id", finalGroup);
                    }
                });
                String menus = "";
                if ("1".equals(type)) {//???????????????????????????????????????ID
                    menus = map.get("parent_id") + "," + ID;
                } else {
                    menus = ID;
                }
                for (Map m : list) {
                    String menu_id = m.get("menu_id").toString();
                    String[] menu = menu_id.split(",");
                    String str = "";
                    if (!Arrays.asList(menu).contains(ID)) {
                        String[] groups = menus.split(",");
                        str = ArrayUtils.ArrayMergingtoString(menu, groups);//?????????????????????????????????
                    } else {
                        Object[] new_menu = ArrayUtils.Arrayremove(menu, ID);
                        str = StringUtils.join(new_menu, ",");
                    }
                    String finalStr = str;
                    number = dbService.UpdateAuthority(new HashMap() {{
                                                           put("ID", m.get("ID"));
                                                       }},
                            new HashMap() {{
                                put("menu_id", finalStr);
                            }});//??????????????????
                    if (number == 0) {
                        return dbService.getReturn_status("20003");
                    }
                }

            }
            return dbService.getReturn_status("10003");
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.res_error(e.getMessage());
        }
    }

    public Map getConfig() {
        try {
            List list = dbService.getConfig();
            return dbService.getReturn_status("10000", list);
        } catch (Exception e) {
            return dbService.res_error(e.getMessage());
        }
    }

    ;

    public Map getEmail(Map map) {
        try {
            List list = dbService.getEmaillimit(map);
            return dbService.res_success(list, list.size());
        } catch (Exception e) {
            return dbService.res_error("??????????????????");
        }
    }
    public Map getEmailbyID(Map map){
        try {
            if (isnull(map.get("ID"))){
                throw new Exception("ID????????????");
            }
            String ID=map.get("ID").toString();
            List<Map> list = dbService.getEmail(new HashMap(){{
                put("ID",ID);
            }});
            return dbService.getReturn_status("10000",list);
        } catch (Exception e) {
            return dbService.getReturn_status("20000","??????????????????");
        }
    }
    /**
     * ??????????????????????????????
     * @param map
     * @return
     */
    public Map setupEmail(Map map) {
        try {
            String Host = isnull(map.get("Host"), "").toString();
            String Username = isnull(map.get("Username"), "").toString();
            String Password = isnull(map.get("Password"), "").toString();
            if (isnull(Host) || isnull(Username) || isnull(Password)) {
                throw new Exception("???????????????????????????,???????????????!");
            }
            //????????????????????????
            List<Map> list = dbService.getEmail(new HashMap(){{
                put("Username",Username);
            }});
            //??????????????????????????????
            JavaMailSender javaMailSender = new JavaMailSenderImpl(Host, Username, Password, true).GetJavaMailSender();
            Map resMap = mailService.sendSimpleMail(javaMailSender, Username, "?????????????????????", "???????????????????????????????????????,??????????????????????????????????????????IMAP/SMTP??????!");
            if (resMap.get("code").equals(0)) {
                if (isnull(map.get("type"))?list.size()>0:list.size()>0 && "update".equals(map.get("type"))){
                    String id=isnull(map.get("id"))?list.get(0).get("ID").toString():map.get("id").toString();
                    int number=dbService.updateEmail(new HashMap(){{
                        put("ID",id);
                    }},new HashMap() {{
                        put("Host", Host);
                        put("Username", Username);
                        put("Password", Password);
                        put("state",0);
                    }});
                    if (number>0){
                        return dbService.getReturn_status("10000","?????????????????????");
                    }else {
                        throw new Exception("???????????????????????????!");
                    }
                }else if ( isnull(map.get("type"))?list.size()==0:(list.size()==0 && "add".equals(map.get("type")))){
                    int number=dbService.addEmail(new HashMap() {{
                        put("Host", Host);
                        put("Username", Username);
                        put("Password", Password);
                        put("state",0);
                    }});
                    if (number>0){
                        return dbService.getReturn_status("10000", "?????????????????????");
                    }else {
                        throw new Exception("??????????????????!");
                    }
                }else {
                    if (list.size()>0||"add".equals(map.get("type")))
                        throw new Exception("????????????,???????????????!");
                    else if (list.size()==0||"add".equals(map.get("type")))
                        throw new Exception("????????????,???????????????!");
                    else
                        throw new Exception("????????????,????????????,????????????????????????,?????????????????????!");
                }
            }else {
                throw new Exception("????????????????????????,???????????????!");
            }
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000", e.getMessage());
        }
    }
    @Transactional
    public Map delEmail(Map map){
        try {
            if (isnull(map.get("ID"))){
                throw new Exception("ID????????????!");
            }
            dbService.delEmail(map);
            return dbService.getReturn_status("10004");
        } catch (Exception e) {
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }

    public Map getEmailTemplate(Map map){
        try {
            List<Map> list=dbService.getEmail_TemplateList(map);
            return dbService.res_success(list);
        } catch (Exception e) {
            getLogger().error(e);
            return dbService.res_error(e.getMessage());
        }
    }

    /**
     * ??????ID????????????
     * @param map
     * @return
     */
    public Map getTemplatecontent(Map map){
        try {
            if (isnull(map.get("ID"))){
                throw new Exception("ID????????????");
            }
            List<Map> list=dbService.getEmail_Template(map);
            if (list.size()<1){
                throw new Exception("??????????????????");
            }
            dbService.setlate_lock("EMAIL_TEMPLATE",map.get("ID"));
            return dbService.getReturn_status("10000",list.get(0));
        } catch (Exception e) {
            getLogger().error(e);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }

    /**
     * ??????????????????
     * @param map
     * @return
     */
    @Transactional
    public Map preserve_Templatecontent(Map map){
        try {
            if (isnull(map.get("type")) || isnull(map.get("title"))){
                throw new Exception("???????????????????????????");
            }
            if (isnull(map.get("pattern")) || "add".equals(map.get("pattern"))) {
                int res = dbService.addEmail_Template(new HashMap() {{
                    put("type", map.get("type"));
                    put("title", map.get("title"));
                    put("content", map.get("code"));
                    put("remarks", map.get("remarks"));
                    put("add_user_id", getuserinfoMap().get("ID"));
                    put("state", "1");
                    put("is_default", 0);
                }});
                if (res>0){
                    return dbService.getReturn_status("10000");
                }else {
                    throw new Exception("??????????????????");
                }
            }else if ("update".equals(map.get("pattern"))){
                int res = dbService.updateEmail_Template(new HashMap() {{
                    put("ID", map.get("ID"));
                }},new HashMap() {{
                    put("type", map.get("type"));
                    put("title", map.get("title"));
                    put("content", map.get("code"));
                    put("remarks", map.get("remarks"));
                }});
                if (res>0){
                    return dbService.getReturn_status("10000");
                }else {
                    throw new Exception("??????????????????");
                }
            }else {
                throw new Exception("????????????????????????");
            }
        } catch (Exception e) {
            getLogger().error(e);
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
    /**
     * ??????????????????
     * @param map
     * @return
     */
    @Transactional
    public Map delete_Templatecontent(Map map){
        try {
            if (isnull(map.get("ID"))){
                throw new Exception("ID????????????");
            }
            List<Map> list=dbService.getEmail_Template(new HashMap(){{
                put("ID",map.get("ID"));
            }});
            if (list.size()<=0){
                throw new Exception("???????????????");
            }
            if (list.size()>1){
                throw new Exception("????????????,??????????????????");
            }
            if (list.get(0).get("is_default").equals(1)){
                throw new Exception("????????????,????????????");
            }
            dbService.deleteEmail_Template(new HashMap(){{put("ID",map.get("ID"));}});
            return dbService.getReturn_status("10004");
        } catch (Exception e) {
            getLogger().error(e);
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
    public Map delete_Templatecontent_lock(Map map){
        try {
            if (isnull(map.get("ID"))){
                throw new Exception("ID????????????");
            }
            dbService.relieve_data_lock("email_template",map.get("ID"));
            return dbService.getReturn_status("10004");
        } catch (Exception e) {
            e.printStackTrace();
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
    @Transactional
    public Map opt_Templatecontent(Map map){
        try {
            if (isnull(map.get("ID")) || isnull(map.get("type"))  ){
                throw new Exception("ID???type????????????");
            }
            int res=dbService.updateEmail_Template(new HashMap(){{
                put("type",map.get("type"));
            }},new HashMap(){{
                put("state","1");
            }});
            if(res>0){
                res=dbService.updateEmail_Template(new HashMap(){{
                    put("ID",map.get("ID"));
                    put("type",map.get("type"));
                }},new HashMap(){{
                    put("state","0");
                }});
                if (res==0){
                    throw new Exception("??????????????????");
                }
            }else {
                throw new Exception("??????????????????");
            }
            return dbService.getReturn_status("10004");
        } catch (Exception e) {
            e.printStackTrace();
            dbService.setLog(e, getuserinfoMap().get("username"), map);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
    public Map getTemplatecontentType(Map map){
        try {
            List<Map> list=dbService.getTemplatecontentType();
            if (list.size()<1){
                throw new Exception("??????????????????");
            }
            return dbService.getReturn_status("10000",list);
        } catch (Exception e) {
            getLogger().error(e);
            return dbService.getReturn_status("20000",e.getMessage());
        }
    }
}
