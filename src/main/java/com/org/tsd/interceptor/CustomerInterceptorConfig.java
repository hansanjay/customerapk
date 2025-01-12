package com.org.tsd.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomerInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    CustomInterceptor customInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
        	.addInterceptor(customInterceptor)
        	.addPathPatterns("/**");
    }
}