package com.example.security;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
	private final long accessTokenValidity = 1000 * 60 * 15; // 15분
	private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7; // 7일
	private final String secretKey = "your-256-bit-secret-must-be-at-least-32-bytes!";
	private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	private static final String TOKEN_PREFIX = "Bearer ";
	
    // Access Token 생성 (with roles)
    public String generateAccessToken(String userId, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성 (claims 없이)
    public String generateRefreshToken(String userId, List<String> roles) {
    	Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", roles);
        
        return Jwts.builder()
        		.setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseToken(token); // parse만으로도 유효성 검증됨
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    // 사용자 ID 추출
    public String extractUserId(String token) {
        return parseToken(token).getBody().getSubject();
    }

    // roles 등 추가 claim 추출
    public Claims extractAllClaims(String token) {
        return parseToken(token).getBody();
    }

    // 내부 파싱 메서드
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token);
    }
    
    // 헤더에서 JWT 토큰만 추출
    public String resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // "Bearer " 제거
        }

        return null; // 유효하지 않거나 없음
    }
}