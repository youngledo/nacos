package com.alibaba.nacos.plugin.auth.impl.jwt;

import com.alibaba.nacos.plugin.auth.impl.utils.Base64Decode;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

/**
 * Token Generator Utility.
 *
 * @author Generated for debugging
 */
public class TokenGenerator {

    private static final String HS512_JWT_HEADER = "eyJhbGciOiJIUzUxMiJ9";
    private static final Base64.Encoder URL_BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final String JWT_SEPARATOR = ".";

    public static void main(String[] args) {
        String username = args.length > 0 ? args[0] : "nacos";
        String secretKey = args.length > 1 ? args[1] : "SecretKey012345678901234567890123456789012345678901234567890123456789";
        long expireSeconds = args.length > 2 ? Long.parseLong(args[2]) : 18000L;

        String token = generateToken(username, secretKey, expireSeconds);

        System.out.println("============================================================");
        System.out.println("用户名: " + username);
        System.out.println("过期时间: " + expireSeconds + " 秒");
        System.out.println("============================================================");
        System.out.println("\n生成的 Token:\n" + token + "\n");
        System.out.println("============================================================");
        System.out.println("\n使用示例:");
        System.out.println("  curl -H 'Authorization: Bearer " + token + "' http://localhost:8848/nacos/v1/cs/configs");
    }

    public static String generateToken(String username, String base64edKey, long expireSeconds) {
        // 1. Decode secret key
        byte[] decode = Base64Decode.decode(base64edKey);

        // 2. Determine algorithm (512 bits = HS512)
        NacosSignatureAlgorithm signatureAlgorithm = NacosSignatureAlgorithm.HS512;
        Key key = new SecretKeySpec(decode, signatureAlgorithm.getJcaName());

        // 3. Create payload
        NacosJwtPayload payload = new NacosJwtPayload();
        payload.setSub(username);
        payload.setExp(System.currentTimeMillis() / 1000L + expireSeconds);

        // 4. Sign
        return signatureAlgorithm.sign(payload, key);
    }
}