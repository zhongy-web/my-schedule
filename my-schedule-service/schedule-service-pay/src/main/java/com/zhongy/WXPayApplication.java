package com.zhongy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
public class WXPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WXPayApplication.class, args);
    }
}

