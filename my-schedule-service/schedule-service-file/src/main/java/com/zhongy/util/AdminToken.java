package com.zhongy.util;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class AdminToken {

    public static String adminToken() {
        /**
         * 管理员令牌
         */
        //加载证书
        ClassPathResource resource = new ClassPathResource("zhongy.jks");

        //读取证书数据
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "zhongy".toCharArray());

        //获取证书中的一对密钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("zhongy", "zhongy".toCharArray());

        //获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        //创建令牌，需要私钥加盐【RSA算法】
        Map<String,Object> payload = new HashMap<>();
        payload.put("name", "zhongy");
        payload.put("authorities", new String[]{"ROLE_ADMIN", "ROLE_USER"});
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(payload), new RsaSigner(privateKey));

        //获取令牌
        String token = jwt.getEncoded();
        return token;
    }

    public static void main(String[] args) {
        String token = adminToken();
        System.out.println(token);
    }

    /**
     * 解析令牌
     */
    @Test
    public void testParseToken() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiemhvbmd5IiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iLCJ1c2VyIl19.LThxhEGfWO8mWt37x6yp5aHrGfuBW2RfWHslFNQbTwLwaAl6hJUqu0Q06oBUl1TOphymIGmN-jx6HuArTyiruPwIG3ar-S8wC4rBB2G_4ClOYF2_lbbyo9i-3lDwaR3pprPZnd_34rtSZRGB8eqP4z_gpkWD2hJqQNCzvOeFET9IOj9VcCWZEN0djxpuC3MfBsWK4CiuCaIO6QcwiWYwPpPdRkdh0mu46xd0AUf3q0BMhA3cg5LtnsO3s3lKv6uqw29RK3MG44j0eoSk2z5ZLh8fWwlm-n6ikkTNKwLjjRt41kyowyONLfzRPjakZ74p3wmos09vH72F8WaWiK2qeQ";
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhFFgB39nCgpQFk8CbxtVlnolYzZ7pTw6KobNmiIpBKLKq1Am12lHl2dtrcusRs9NfE3smwQtKMfkMXVogpLJ3D+K5/WFp6RhQqRsAW5ms64k7IeG33pfsV5I96hIpB/PvXeVHd1uJxX/CIApPBBxiXWoCgoO2W/cRe/m2m4CwrwsnZ5Pf0mGgZRbi7uzpH1ORo1g6WBhuLY7dC/VhRhM8+36LzHVJPGMePbNlQ7Z7t1Syi9GuNTFXemMZHLhZtwElqwue+oh40iOPET9d3I7WqGprdyIXL778TEPMCYldgo0dh1Jwx1oo/fnFwTdE/V2TjfPfgvNPdKwU5VEVFcR4QIDAQAB-----END PUBLIC KEY-----";
        Jwt jwt = JwtHelper.decodeAndVerify(token,
                new RsaVerifier(publickey)
        );
        String claims = jwt.getClaims();
        System.out.println(claims);
    }


}
