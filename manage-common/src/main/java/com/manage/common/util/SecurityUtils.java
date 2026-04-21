package com.manage.common.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.DesensitizedUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class SecurityUtils {

    private static final String SECRET = "manage-frame-jwt-secret-key-2024-very-long-for-hs256";
    private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000L; // 12小时

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public static String createToken(String userId, String username, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userId)
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(getKey())
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String getUserIdFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public static String getUsernameFromToken(String token) {
        return parseToken(token).get("username", String.class);
    }

    public static boolean isTokenExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public static String generateUuid() {
        return IdUtil.randomUUID();
    }

    public static String desensitizedPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return DesensitizedUtil.mobilePhone(phone);
    }

    public static String desensitizedEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        return DesensitizedUtil.email(email);
    }
}
