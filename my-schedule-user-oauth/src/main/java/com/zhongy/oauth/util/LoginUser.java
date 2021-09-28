package com.zhongy.oauth.util;

import lombok.Data;

@Data
public class LoginUser {

    private String username;
    private String nickName;
    private String headPicUrl;
    private String accessToken;
    private String email;
//    private String refreshToken;
}
