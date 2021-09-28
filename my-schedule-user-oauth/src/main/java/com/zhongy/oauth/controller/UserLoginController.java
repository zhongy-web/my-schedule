package com.zhongy.oauth.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhongy.oauth.service.LoginService;
import com.zhongy.oauth.util.AuthToken;
import com.zhongy.oauth.util.QQHttpClient;
import com.zhongy.oauth.util.LoginUser;
import com.zhongy.user.pojo.User;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.UUID;

@RestController
@RequestMapping("/oauth")
public class UserLoginController {

    @Value("${head.url}")
    private String url;

    @Value("${qq.redirect_URI}")
    private String redirect_URI;

    @Autowired
    private LoginService loginService;



    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public Result<AuthToken> refresh(@RequestBody AuthToken authToken) {
        AuthToken refresh = loginService.refresh(authToken.getAccessToken(), authToken.getRefreshToken());
        return new Result<AuthToken>(true, StatusCode.OK, "刷新成功！", refresh);
    }


    /**
     * 退出登陆
     */
    @GetMapping("/logout")
    public Result logout() {
        return new Result(true, StatusCode.OK, "退出登陆");
    }


    /**
     * 注册
     *
     */
//    @PostMapping("/register")
//    public Result register(@RequestBody User user) {
//        System.out.println(user);
//        user.setCreated(new Date());
//        user.setRole(2);
//        user.setHeadPicUrl(url);
//        loginService.register(user);
//        return new Result(true, StatusCode.OK, "注册成功！");
//    }


    /**
     * 登录方法
     * 参数传递：
     * 1.账号 username
     * 2.密码 password
     * 3.授权方式 grant_type
     *
     * 请求头传递
     * 4.Basic  Base64（客户端ID:客户端密钥） Authorization=xxxxxxxxxxxxx
     */
    /*
    todo 登陆接口响应太久了，又要生成token又要调用用户服务查数据库，需要分开。
     */
    @PostMapping(value = "/login")
    public Result login(@RequestBody User user) throws Exception {
        //调用loginService实现登录
        String grant_type = "password";
        LoginUser loginUser = loginService.login(user.getUsername(), user.getPassword(), grant_type);
        if (loginUser != null) {
            //将token存入redis中
//            redisTemplate.opsForSet().add("validTokenId", authToken.getJti());
            return new Result(true, StatusCode.OK, "登录成功", loginUser);
        }
        return new Result(false, StatusCode.LOGINERROR, "登录失败");
    }

    /**
     * qq第三方登陆请求
     */
    @GetMapping(value = "/loginByQQ")
    public Result loginByQQ(HttpSession session) {
        //QQ互联中的回调地址
        String backUrl = redirect_URI;

        //用于第三方应用防止CSRF攻击
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        session.setAttribute("state",uuid);

        //Step1：获取Authorization Code
        String url = "https://graph.qq.com/oauth2.0/authorize?response_type=code"+
                "&client_id=" + QQHttpClient.APPID +
                "&redirect_uri=" + URLEncoder.encode(backUrl) +
                "&state=" + uuid;
        return new Result(true, StatusCode.OK, url);
    }

    /**
     * QQ回调 注意 @GetMapping("/qq/callback")路径
     * 是要与QQ互联填写的回调路径一致(我这里因为前端请求原因不用写成  api/qq/callback)
     * @return
     */
    //todo get改成了request
    @PostMapping("/loginAfter")
    public Result<String> qqCallBack(String code) throws Exception {
        //qq返回的信息：http://graph.qq.com/demo/index.jsp?code=9A5F************************06AF&state=test

        //Step2：通过Authorization Code获取Access Token
        String backUrl = redirect_URI;
        String url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code"+
                "&client_id=" + QQHttpClient.APPID +
                "&client_secret=" + QQHttpClient.APPKEY +
                "&code=" + code +
                "&redirect_uri=" + backUrl;

        String access_token = QQHttpClient.getAccessToken(url);

        //Step3: 获取回调后的 openid 值
        url = "https://graph.qq.com/oauth2.0/me?access_token=" + access_token;
        String openid = QQHttpClient.getOpenID(url);

        //Step4：获取QQ用户信息
        url = "https://graph.qq.com/user/get_user_info?access_token=" + access_token +
                "&oauth_consumer_key="+ QQHttpClient.APPID +
                "&openid=" + openid;

        JSONObject jsonObject = QQHttpClient.getUserInfo(url);
        LoginUser qqUser = loginService.loginByQQ(openid, (String) jsonObject.get("nickname"), (String) jsonObject.get("figureurl_qq_2"));
        if (qqUser != null) {
            return new Result(true, StatusCode.OK, "登录成功", qqUser);
        }
        return new Result(false, StatusCode.LOGINERROR, "登录失败");
    }
}
