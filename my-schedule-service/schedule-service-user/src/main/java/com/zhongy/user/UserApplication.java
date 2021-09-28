package com.zhongy.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableResourceServer   //开启资源校验服务 ->令牌校验
@MapperScan("com.zhongy.user.dao")
//@EnableFeignClients
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}