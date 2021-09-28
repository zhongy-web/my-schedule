package com.zhongy.intercepter;

import com.zhongy.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenRequestInterceptor implements RequestInterceptor {
    /**
     * 从数据库加载查询用户信息
     * 1.没有令牌，生成令牌（admin）
     * 2.令牌需要携带过去
     * 3.令牌需要存放到Header文件中
     * 4.请求->Feign调用 ->拦截器RequestInterceptor->Feign调用之前执行拦截
     */
    @Override
    public void apply(RequestTemplate template) {
        //生成admin令牌
        String token = AdminToken.adminToken();
        template.header("Authorization", "bearer " + token);
    }
}
