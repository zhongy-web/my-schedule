package com.zhongy.oauth.service;

import com.zhongy.oauth.util.AuthToken;
import com.zhongy.oauth.util.LoginUser;

import java.io.UnsupportedEncodingException;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.oauth.service *
 * @since 1.0
 */
public interface LoginService {

    /**
     * 注册用户
     */
//    void register(User user);

    /**
     * 用户退出，销毁token
     */
    void logout();

    /**
     * 模拟用户的行为 发送请求 申请令牌 返回
     * @param username
     * @param password
     * @param grandType
     * @return
     */
    LoginUser login(String username, String password, String grandType) throws Exception;

    /**
     * 刷新令牌
     * 1.将grant_type(refresh_token)和refresh_token放到body中请求地址为localhost:9001/oauth/token
     */
    AuthToken refresh(String token, String reToken);

    LoginUser loginByQQ(String openid, String nickName, String headUrl) throws UnsupportedEncodingException;
}
