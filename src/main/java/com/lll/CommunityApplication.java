package com.lll;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author 龙儿
 */
@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void init(){
        // 与Redis冲突
        // 解决netty启动中遇到的问题
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
