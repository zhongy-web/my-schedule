package com.zhongy;

import com.zhongy.intercepter.TokenRequestInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) // 排除掉数据库自动加载
@EnableEurekaClient
@EnableResourceServer
@EnableFeignClients(basePackages = {"com.zhongy.user.feign"})
@EnableScheduling
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

    @Bean
    public TokenRequestInterceptor tokenRequestInterceptor() {
        return new TokenRequestInterceptor();
    }

}
