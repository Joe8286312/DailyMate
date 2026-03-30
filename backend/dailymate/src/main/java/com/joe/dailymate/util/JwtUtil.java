// JwtUtil.java
package com.joe.dailymate.util;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtil {
//    public static final String SECRET = "DailymateSecretKey123!"; //长度不够
    public static final String SECRET = "DailymateSuperSecretKeyForJwtTokenABC123456!"; // 至少32个字符

    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .claim("username", username)
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static Claims parseToken(String token) throws ExpiredJwtException, JwtException {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从请求头的 Authorization: Bearer <token> 中提取当前用户 ID
     * 集中到这里，避免每个 Controller 重复实现
     */
    public static Long getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("未登录");
        }
        Claims claims = parseToken(header.substring(7));
        return Long.valueOf(claims.getSubject());
    }
}