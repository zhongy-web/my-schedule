package entity;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

public class FeignInterceptor implements RequestInterceptor {
    /**
     * 从数据库加载查询用户信息
     * 1.没有令牌，生成令牌（admin）
     * 2.令牌需要携带过去
     * 3.令牌需要存放到Header文件中
     * 4.请求->Feign调用 ->拦截器RequestInterceptor->Feign调用之前执行拦截
     */
    @Override
    public void apply(RequestTemplate template) {
        /**
         * 获取用户令牌
         * 将令牌再封装到头文件中
         */
        // 记录了当前用户请求的所有数据，包含请求头和请求参数等
        // 用户当前请求的时候对应线程的数据，如果开启了熔断，默认线程池隔离，会开启新线程，需要将熔断策略换成信号量隔离，此时不会开启新的线程
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Enumeration<String> headerNames = requestAttributes.getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //请求头的key
            String headerKey = headerNames.nextElement();
            //获取请求头的值
            String header = requestAttributes.getRequest().getHeader(headerKey);
            System.out.println(header);

            //将请求头信息封装到头中。使用Feign调用，会传递给下个微服务
            template.header(headerKey, header);
        }
    }
}

