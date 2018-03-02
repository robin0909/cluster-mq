package com.robin.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        registry.addResourceHandler("/dist/**").addResourceLocations("classpath:/web/dist/");
        registry.addResourceHandler("/imgs/**").addResourceLocations("classpath:/web/imgs/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/web/");
    }
}