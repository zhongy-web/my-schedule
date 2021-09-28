package com.zhongy.community;

import entity.FeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer   //开启资源校验服务 ->令牌校验
@MapperScan("com.zhongy.community.dao")
@EnableScheduling
@EnableFeignClients(basePackages = {"com.zhongy.user.feign"})
public class CommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

    @Bean
    public FeignInterceptor tokenRequestInterceptor() {
        return new FeignInterceptor();
    }

}
