package com.module.common;

import com.Controller.TimedTasks;
import com.helei.api.common.ImplBase;
import com.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommonMethod extends ImplBase {
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private DbService dbService;
    @Autowired
    private TimedTasks timedTasks;
    @Autowired
    private HttpServletRequest request;

    @Value("${file.uploadFolder}")
    public String uploadFolder;

    public Map getConfig(int i) throws Exception {
        i++;
        if (i > 5) {
            throw new Exception("获取系统配置失败");
        }
        Map config = jsonToMap(redisUtil.get("config"));
        if (isnull(config)) {
            timedTasks.saveConfig();
            getConfig(i);
        }
        return config;
    }

    /**
     * 读取配置表所有配置
     *
     * @return
     * @throws Exception
     */
    public Map getConfig() throws Exception {
        int i = 0;
        return getConfig(i);
    }

    /**
     * 获取保存的MAP中的value
     *
     * @param key 键
     * @return value 值
     */
    public String getConfig(String key) {
        try {
            String str = redisUtil.get("config");
            Map map = jsonToMap(str);
            if (map.get(key) != null && map.get(key) != "") {
                return map.get(key).toString();
            } else {
                throw new Exception("配置文件中不存在[" + key + "]配置");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map getuserinfo() {
        return (Map<String, Object>) request.getSession().getAttribute("user");
    }

    public String getuserinfo(String key) {
        Map map = (Map<String, Object>) request.getSession().getAttribute("user");
        if (isnull(map.get(key)))
            return null;
        else
            return map.get(key).toString();
    }

    public Map getuserinfo(String[] keys) {
        Map map = (Map<String, Object>) request.getSession().getAttribute("user");
        return new HashMap() {{
            for (String key : keys) {
                put(key, map.get(key));
            }
        }};
    }

    /**
     * 获取用户编码
     *
     * @return
     */
    public String getuserBM() {
        Map map = getuserinfo();
        return map.get("BM").toString();
    }

    /**
     * 获取用户ID
     *
     * @return
     */
    public String getuserID() throws Exception {
        Map map = getuserinfo();
        if (isnull(map)) {
            throw new Exception("获取用户信息失败");
        }
        return map.get("ID").toString();
    }

    public List<Map> getUserinfo(Map map) {
        return dbService.getUserinfo(map);
    }
}
