package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
/*
 * 处理跨域问题
 */
@SuppressWarnings("deprecation")
@Configuration
public class CorsConfig  extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //对那些请求路径进行跨域处理
        registry.addMapping("/**")
                // 允许的请求头，默认允许所有的请求头
                .allowedHeaders("*")
                // 允许的方法，默认允许GET、POST、HEAD
                .allowedMethods("*")
                // 探测请求有效时间，单位秒
                .maxAge(1800)
                // 支持的域
                .allowedOrigins("*");
                //.allowedOrigins("http://127.0.0.1");
    }
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        //和页面有关的静态目录都放在项目的static目录下
//        registry.addResourceHandler("/upload/**").addResourceLocations("file:F:/upload/");
//    }
}