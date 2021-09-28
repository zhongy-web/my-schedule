package com.zhongy.sport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.zhongy.user.feign")
@MapperScan("com.zhongy.sport.dao")
public class SportApplication {
    public static void main(String[] args) {
        SpringApplication.run(SportApplication.class, args);
    }
}
