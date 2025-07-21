package com.example.interceptor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.security.SecurityUtil;
import com.example.service.MenuPermissionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuAuthorizationInterceptor implements HandlerInterceptor {

    private final MenuPermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String userId = SecurityUtil.getCurrentUserId();

        // 현재 인증된 사용자 정보에서 role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isSystemClient = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_API_CLIENT"));

            // 시스템 클라이언트면 권한 검사 생략
            if (isSystemClient) {
                return true;
            }
        }
        
        if (!permissionService.hasPermission(userId, requestUri)) {
            throw new AccessDeniedException("메뉴 접근 권한이 없습니다.");
        }

        return true;
    }
}