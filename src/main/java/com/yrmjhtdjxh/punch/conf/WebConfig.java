package com.yrmjhtdjxh.punch.conf;


import com.yrmjhtdjxh.punch.security.AuthRoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author GO FOR IT
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${img.location}")
    private String location;

    @Autowired
    private AuthRoleInterceptor authRoleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authRoleInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + location)
                .addResourceLocations("classpath:/static/images/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
