package zhongy.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import zhongy.common.FilterRoutes;

/**
 * 全局过滤器
 * 实现用户权限鉴别
 */
@Component
@EnableConfigurationProperties(FilterRoutes.class)
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Autowired
    private FilterRoutes filterRoutes;

    //令牌名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    /**
     * 全局拦截
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //todo 这里可以使用枚举类
        //白名单放行
        for (String allowPath : filterRoutes.getAllowPaths()) {
            if (request.getPath().toString().equals(allowPath)) {
                return chain.filter(exchange);
            }
        }

        //获取用户令牌信息
        //1)头文件中
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        /**
         * 过期和刷新令牌处理。
         */
        //如果没有令牌则拦截 TODO token这一块就这样先用着  以后再调整
        /**
         * 1.判断token是否有效
         * 2.判断retoken是否过期
         * 3.如果都过期了就返回登陆页面重新登陆
         */
//        try {
//            //如果token过期
//        } catch (Exception e) {
//            //判断refreshToken是否过期
//            String refreshToken = (String) redisTemplate.opsForValue().get(token);
//            if (StringUtils.isEmpty(refreshToken)) {
//                //两个都没有就直接报401，并让前端返回到登陆页面
//                //无效拦截
//                //设置没有权限的状态码  401
//                response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                //返回双token过期的消息
//                return response.setComplete();
//            }
//            //TODO 返回token过期的状态码
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        }
        //如果没有就查看有没有对应的refreshToken
//        if (!redisTemplate.opsForSet().isMember("validToken", token)) {
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            JSONObject message = new JSONObject();
//            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
//
//            if (!redisTemplate.opsForSet().isMember("validReToken", token)) {
//                //设置没有权限的状态码  401
//                message.put("flag", 1);
//                message.put("message", "双token失效");
//
//            } else {
//                //设置没有权限的状态码  401
//                message.put("flag", 0);
//                message.put("message", "请使用refreshToken进行token刷新操作");
//            }
//
//            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bits);
//            //返回双token过期的消息
//            return response.writeWith(Mono.just(buffer));
//        }

        //如果此时令牌为空，不允许访问，直接拦截   token要有bearer+空格
        if (StringUtils.isEmpty(token)) {
            //设置没有权限的状态码  401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //响应空数据
            return response.setComplete();
        }

        //有效放行
        return chain.filter(exchange);
    }

    /**
     * 排序  越小越先执行
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
