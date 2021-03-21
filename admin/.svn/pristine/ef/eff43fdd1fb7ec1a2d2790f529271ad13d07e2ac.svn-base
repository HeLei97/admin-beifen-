package com.config;

import com.helei.api.common.ImplBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.xml.ws.Endpoint;

@Configuration
public class Config extends ImplBase {

    @Autowired
    SpringBus bus;

    @Autowired
    com.module.system.login.service.DbService DbService;

    @Bean
    public Endpoint endpointService() {
        EndpointImpl endpoint = new EndpointImpl(bus, DbService);
        endpoint.publish("/backstageService");
        getLogger().info("后台管理系统发表成功!");
        return endpoint;
    }

    @Configuration
    public class UploadFilePathConfig extends WebMvcConfigurerAdapter {

        @Value("${file.staticAccessPath}")
        private String staticAccessPath;
        @Value("${file.uploadFolder}")
        private String uploadFolder;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler(staticAccessPath).addResourceLocations("file:" + uploadFolder);
        }
    }
}
