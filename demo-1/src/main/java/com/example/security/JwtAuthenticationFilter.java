package com.example.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private AntPathMatcher pathMatcher;
    
	@SuppressWarnings("unchecked")
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

	    String uri = request.getRequestURI();

	    for (String white : WHITELIST_URIS) {
	        if (pathMatcher.match(white, uri)) {
	        	chain.doFilter(request, response);
	            return;
	        }
	    }
	    
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                List<String> roles = (List<String>) claims.get("roles");

                List<GrantedAuthority> authorities = new ArrayList<>(roles.stream()
                	    .map(SimpleGrantedAuthority::new)
                	    .toList());

                // ✅ 인증 객체 생성 + SecurityContext에 저장
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
                
            }
        }

        chain.doFilter(request, response);
    }
	
    private static final List<String> WHITELIST_URIS = List.of(
    	    "/swagger-ui.html"
    	    ,"/swagger-ui/**"
    	    ,"/v3/api-docs/**"
    	    ,"/api-docs/**"
    );
}