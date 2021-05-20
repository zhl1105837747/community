package com.lll.config;

import com.lll.controller.interceptor.DataInterceptor;
import com.lll.controller.interceptor.LoginRequiredInterceptor;
import com.lll.controller.interceptor.LoginTicketInterceptor;
import com.lll.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    /**
     * @Autowired private LoginRequiredInterceptor loginRequiredInterceptor;
     */

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/js/**", "/css/**", "/img/**");

        /**
         *   registry.addInterceptor(loginRequiredInterceptor)
         *                 .addPathPatterns("/**")
         *                 .excludePathPatterns("/js/**", "/css/**", "/img/**");
         */


        registry.addInterceptor(messageInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/js/**", "/css/**", "/img/**");

        registry.addInterceptor(dataInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/js/**", "/css/**", "/img/**");

    }
}
