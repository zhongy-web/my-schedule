package com.zhongy.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer   //开启资源校验服务 ->令牌校验
@MapperScan("com.zhongy.schedule.dao")
@EnableScheduling
@EnableFeignClients(basePackages = {"com.zhongy.user.feign"})
public class WeekApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeekApplication.class, args);
    }
}
