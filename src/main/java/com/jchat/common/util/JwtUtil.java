package com.jchat.common.util;

import com.jchat.auth.dto.UserInfoDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // Access Token 생성 (사용자 정보 포함)
    public String generateAccessToken(UserInfoDto userInfoDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userNo", userInfoDto.getUserNo());
        claims.put("id", userInfoDto.getId());
        claims.put("name", userInfoDto.getName());
        claims.put("birth", userInfoDto.getBirth());
        claims.put("email", userInfoDto.getEmail());

        return generateToken(claims, userInfoDto.getUserNo(), accessTokenValidity);
    }

    // Refresh Token 생성 (userId만)
    public String generateRefreshToken(Long userNo) {
        return generateToken(new HashMap<>(), userNo, refreshTokenValidity);
    }

    // 토큰 생성 (공통)
    private String generateToken(Map<String, Object> claims, Long subject, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .claims(claims) // 커스텀 정보
                .subject(String.valueOf(subject)) // userNo
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰에서 userId 추출
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    // 토큰에서 모든 사용자 정보 추출
    public UserInfoDto getUserInfoFromToken(String token) {
        Claims claims = parseClaims(token);
        return UserInfoDto.builder()
                .userNo(Long.parseLong(claims.getSubject()))
                .id(claims.get("id", String.class))
                .name(claims.get("name", String.class))
                .birth(claims.get("birth", Integer.class))
                .email(claims.get("email", String.class))
                .build();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 파싱
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 쿠키에 Access Token 추가
    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);  // 로컬: false, 운영: true
        cookie.setPath("/");
        cookie.setMaxAge((int) (accessTokenValidity / 1000));
        response.addCookie(cookie);
    }

    // 쿠키에 Refresh Token 추가
    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenValidity / 1000));
        response.addCookie(cookie);
    }

    // 쿠키 삭제
    public void deleteTokenCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);  // 추가
        accessTokenCookie.setSecure(false);   // 추가

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);  // 추가
        refreshTokenCookie.setSecure(false);   // 추가

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

}