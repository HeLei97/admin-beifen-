package com.Controller;

import com.util.ControllerBase;
import com.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Configuration
@EnableScheduling
public class TimedTasks extends ControllerBase {

    @Autowired
    private com.service.ApiService apiService;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 这个方法每10秒打印一次.
     * 使用cron表达式 指定：秒，分钟，小时，日期，月份，星期，年（可选）.
     */
    @PostConstruct
    @Scheduled(cron = "0 */5 * * * ?")
    public void saveConfig() {
        Map map=apiService.SaveConfig();
        System.out.println(toJson(map));
        redisUtil.set("config",toJson(map));
    }

}
