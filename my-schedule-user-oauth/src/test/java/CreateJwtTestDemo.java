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

public class CreateJwtTestDemo {
    @Test
    public void testCreateToken() {
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
        payload.put("name", "531158CC1865626F1483C93563C50A0C");
        payload.put("authorities", new String[]{"ROLE_ADMIN", "ROLE_USER"});
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(payload), new RsaSigner(privateKey));

        //获取令牌
        String token = jwt.getEncoded();
        System.out.println(token);
    }

    @Test
    public void parseToken() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjA1MTA5Mjg0MSwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iLCJ1c2VyIl0sImp0aSI6ImFlOTM5MmU0LWJmNmEtNGIwOC1iZDRmLTkwMDkzZDcwMTU2YiIsImNsaWVudF9pZCI6Inpob25neSIsInVzZXJuYW1lIjoiemhvbmd5In0.eUhWsugJiq1U3mWRmsIgijo8fwTLrLH_kCYVxhO5iZCpUFzLlsw2iKoxD3vOo3YbhPfSUu0BphyoVLGMk9eaAtFZVxIdorsv-BL7ioX6Nl5MnLKyI_U0wdOn4Aww3NUCxBRA6T9YdWpXDwqPaiNfTWoy0Kt10Puv2i11iufAD0-B7-YcXCybnR33XOZeRoHP4DcauR8OpGaW43LVHnxj4_tGuewkcFj5hHIDe9GZ4w9_9AeD7PtMPPJxwaQaascaYW7vk-UFu0UyxxtbVfLilWFk31js0DzmFWNp8wPkh3i8SNe7YDMC-NCuo1LNZgc5GTpwm4oWgzrfvtyxj0CZMg";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhFFgB39nCgpQFk8CbxtVlnolYzZ7pTw6KobNmiIpBKLKq1Am12lHl2dtrcusRs9NfE3smwQtKMfkMXVogpLJ3D+K5/WFp6RhQqRsAW5ms64k7IeG33pfsV5I96hIpB/PvXeVHd1uJxX/CIApPBBxiXWoCgoO2W/cRe/m2m4CwrwsnZ5Pf0mGgZRbi7uzpH1ORo1g6WBhuLY7dC/VhRhM8+36LzHVJPGMePbNlQ7Z7t1Syi9GuNTFXemMZHLhZtwElqwue+oh40iOPET9d3I7WqGprdyIXL778TEPMCYldgo0dh1Jwx1oo/fnFwTdE/V2TjfPfgvNPdKwU5VEVFcR4QIDAQAB-----END PUBLIC KEY-----";

        Jwt jwt = JwtHelper.decodeAndVerify(token,
                new RsaVerifier(publicKey)
        );
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
