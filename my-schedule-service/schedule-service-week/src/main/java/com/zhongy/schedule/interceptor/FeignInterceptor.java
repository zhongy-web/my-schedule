package com.zhongy.schedule.interceptor;

import com.zhongy.schedule.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignInterceptor implements RequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader("Authorization");
            template.header("Authorization", authorization);
        } else {
            //todo 这里会有个问题，如果计划多的时候会一直生成token，可能不太好。。
            String token = AdminToken.adminToken();
            template.header("Authorization", "bearer " + token);
        }
    }
}
