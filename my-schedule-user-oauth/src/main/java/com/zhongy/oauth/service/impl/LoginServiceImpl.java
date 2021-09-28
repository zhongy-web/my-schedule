package com.zhongy.oauth.service.impl;

import com.zhongy.oauth.service.LoginService;
import com.zhongy.oauth.util.AdminToken;
import com.zhongy.oauth.util.AuthToken;
import com.zhongy.oauth.util.LoginUser;
import com.zhongy.user.feign.UserFeign;
import com.zhongy.user.pojo.User;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.oauth.service.impl *
 * @since 1.0
 */
@Service
public class LoginServiceImpl implements LoginService {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    private String url = "http://localhost:9001/oauth/token";


    /**
     * 用户注册
     * @return
     * @throws Exception
     */
//    @Override
//    public void register(User user) {
//        userFeign.add(user);
//    }

    /**
     * 用户退出
     */
    @Override
    public void logout() {

    }

    /**
     * 登陆流程：
     * 1. 先找redis缓存中有无对应的token有就直接将token以及retoken返回
     * 2. 没有就生成token，并将token和retoken存入redis中，设置过期时间 30分钟 、 15天
     */

    @Override
    public LoginUser login(String username, String password, String grandType) throws Exception {
        //todo 把登陆响应时间调整至20s
        //获取指定服务的注册数据
//        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
//        调用请求的地址 http://localhost:9001/oauth/token
//        String url = serviceInstance.getUri() + "/oauth/token";

        //校验用户名密码是否正确
        Result<User> userResult = userFeign.findById(username);
        if (userResult.getData() != null) {
            String data_password = userResult.getData().getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            //密码正确再进行封装
            if (encoder.matches(password, data_password)) {
                AuthToken authToken = getAuthToken(clientId, clientSecret, username, password, grandType, url);
                User user = userResult.getData();
                //并将消息放入消息队列中监听，到时删除即可。
                LoginUser loginUser = new LoginUser();
                loginUser.setUsername(user.getUsername());
                loginUser.setNickName(user.getName());
                loginUser.setHeadPicUrl(user.getHeadPicUrl());
                loginUser.setAccessToken(authToken.getAccessToken());
                loginUser.setEmail(user.getEmail());
//                user.setRefreshToken(authToken.getRefreshToken());
                return loginUser;
            }
        }
        return null;
    }

    //TODO 刷新有点小问题 以后再解决！！！！！
    @Override
    public AuthToken refresh(String token, String reToken) {
        String url = "http://localhost:9001/oauth/token";
        //封装数据
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("grant_type", "refresh_token");
        multiValueMap.add("refresh_token", reToken);
        HttpEntity httpEntity = new HttpEntity(multiValueMap, null);
        //TODO 可以优化
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map<String, String> map = response.getBody();
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(map.get("access_token"));
        authToken.setRefreshToken(map.get("refresh_token"));
        //将原来的刷新令牌弹出
        redisTemplate.opsForSet().remove("validReToken", token);

        //将新的令牌加入
        redisTemplate.opsForSet().add("validToken", authToken.getAccessToken(), "1");
        redisTemplate.opsForSet().add("validReToken", authToken.getAccessToken(), authToken.getRefreshToken());
        return authToken;
    }

    /**
     * QQ回调登陆
     *
     * @param openid
     * @param nickName
     * @param headUrl
     * @return
     */
    //todo 写的比较繁琐 应该是可以优化的。
    @Override
    public LoginUser loginByQQ(String openid, String nickName, String headUrl) throws UnsupportedEncodingException {
        Result<User> result = userFeign.findById(openid);

        //封装qq用户信息
        LoginUser qqUser = new LoginUser();
        qqUser.setHeadPicUrl(headUrl);
        qqUser.setUsername(openid);
        qqUser.setNickName(nickName);

        User user = result.getData();
        if (user == null) {
            User newUser = new User();
            newUser.setUsername(openid);
            newUser.setName(nickName);
            newUser.setHeadPicUrl(headUrl);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encode_password = encoder.encode("123456");
            newUser.setPassword(encode_password);
            userFeign.addQQUser(newUser);
            // todo 请求报400
            AuthToken authToken = getAuthToken(clientId, clientSecret, openid, "123456", "password", url);
            qqUser.setAccessToken(authToken.getAccessToken());
            return qqUser;
        }

        String access_token = (String) redisTemplate.opsForValue().get("token" + openid);
        if (StringUtils.isEmpty(access_token)) {
            AuthToken authToken = getAuthToken(clientId, clientSecret, openid, "123456", "password", url);
            access_token = authToken.getAccessToken();
            redisTemplate.opsForValue().set("token" + openid, access_token, Integer.valueOf(authToken.getExp_time()));
        }
        //todo 返回0代表登陆成功  这里可以优化
        qqUser.setAccessToken(access_token);
//        qqUser.setRefreshToken(authToken.getRefreshToken());
        return qqUser;
    }

    private AuthToken getAuthToken(String clientId, String clientSecret, String username, String password, String grandType, String url) throws UnsupportedEncodingException {
        //请求提交的数据封装
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("username", username);
        multiValueMap.add("password", password);
        multiValueMap.add("grant_type", grandType);

        //请求头封装
        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        String Authorization = "Basic " + new String(Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes()), "UTF-8");
        headerMap.add("Authorization", Authorization);
        //封装请求头和体
        HttpEntity httpEntity = new HttpEntity(multiValueMap, headerMap);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);

        //用户登录后的令牌信息
        Map<String, String> map = response.getBody();
        //将令牌信息转换成对象
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(map.get("access_token"));
        authToken.setRefreshToken(map.get("refresh_token"));
        authToken.setJti(map.get("jti"));
        authToken.setExp_time(String.valueOf(map.get("expires_in")));
        return authToken;
    }
}
