package top.mayiqin.ai_edu_platform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT令牌的生成、解析和验证功能
 * 使用 jjwt 0.12.x 版本，支持 Jakarta EE
 * @author m'y'q
 */
@Slf4j
public class JwtUtil {
    
    /**
     * 生成JWT令牌
     *
     * @param secretKey JWT签名密钥
     * @param ttlMillis JWT过期时间(毫秒)
     * @param claims    自定义声明（如userId等）
     * @return JWT令牌字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 计算过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 将字符串密钥转换为 SecretKey
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 构建JWT
        return Jwts.builder()
                // 设置自定义声明
                .claims(claims)
                // 设置过期时间
                .expiration(exp)
                // 设置签名算法和密钥
                .signWith(key)
                // 生成紧凑的JWT字符串
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param secretKey JWT签名密钥
     * @param token     加密后的token
     * @return Claims对象，包含所有声明信息
     * @throws ExpiredJwtException Token已过期
     * @throws JwtException 签名验证失败或Token无效
     * @throws IllegalArgumentException Token格式错误
     */
    public static Claims parseJWT(String secretKey, String token) {
        try {
            // 将字符串密钥转换为 SecretKey
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // 解析JWT并获取Claims
            return Jwts.parser()
                    // 设置签名密钥
                    .verifyWith(key)
                    // 构建解析器
                    .build()
                    // 解析token
                    .parseSignedClaims(token)
                    // 获取负载内容
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token已过期: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT Token验证失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("JWT Token解析失败: {}", e.getMessage());
            throw new IllegalArgumentException("无效的Token格式", e);
        }
    }
    
    /**
     * 从Token中提取用户ID
     *
     * @param secretKey JWT签名密钥
     * @param token     JWT令牌
     * @return 用户ID
     * @throws IllegalArgumentException Token无效或已过期
     */
    public static Long getUserIdFromToken(String secretKey, String token) {
        try {
            Claims claims = parseJWT(secretKey, token);
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                log.warn("Token中缺少userId字段");
                throw new IllegalArgumentException("Token中缺少用户ID");
            }
            // 兼容 Number 类型（可能是 Integer 或 Long）
            return ((Number) userIdObj).longValue();
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token已过期，无法提取userId");
            throw new IllegalArgumentException("Token已过期");
        } catch (Exception e) {
            log.warn("从Token中提取userId失败: {}", e.getMessage());
            throw new IllegalArgumentException("无效的Token");
        }
    }
    
    /**
     * 验证Token是否有效
     *
     * @param secretKey JWT签名密钥
     * @param token     JWT令牌
     * @return true-有效，false-无效
     */
    public static boolean isTokenValid(String secretKey, String token) {
        try {
            parseJWT(secretKey, token);
            return true;
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 生成Refresh Token
     *
     * @param secretKey JWT签名密钥
     * @param ttlMillis Refresh Token过期时间(毫秒)
     * @param claims    自定义声明（如userId等）
     * @return Refresh Token字符串
     */
    public static String createRefreshJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 计算过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 将字符串密钥转换为 SecretKey
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 构建Refresh Token
        return Jwts.builder()
                .claims(claims)
                .expiration(exp)
                .signWith(key)
                .compact();
    }
}
