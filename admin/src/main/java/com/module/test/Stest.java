package com.module.test;

import com.util.RedisUtil;
import org.apache.shiro.util.AntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.MessageFormat;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@RequestMapping(value = "/test")
@RestController
public class Stest {
    @Autowired
    private RedisUtil redisUtil;

    public static void main(String[] args) {
        String s = "/a/**";
        String Path = "/user/info";
        AntPathMatcher matcher = new AntPathMatcher();
        String pattern = "/abc/**/a.jsp";
        System.out.println("pattern:" + pattern);
        System.out.println("/abc/aa/bb/a.jsp:" + matcher.match(pattern, "/abc/aa/bb/a.jsp"));
        System.out.println("/aBc/aa/bb/a.jsp:" + matcher.match(pattern, "/aBc/aa/bb/a.jsp"));
        System.out.println("/abc/a.jsp:" + matcher.match(pattern, "/abc/a.jsp"));
        System.out.println(matcher.match(s, Path));
    }

    @RequestMapping("/set")
    public void test() {
        redisUtil.set("test:123", "123");

    }

    @RequestMapping("/get")
    public Object get(HttpServletRequest req) {
        return redisUtil.get("*");
    }

}
