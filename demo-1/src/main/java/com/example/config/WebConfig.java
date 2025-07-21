package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.interceptor.MenuAuthorizationInterceptor;
import com.example.security.WhiteListUrlCheck;
import com.example.service.MenuPermissionService;

/**
 * 웹 인터셉터나 컨트롤러 관련 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MenuPermissionService permissionService;
    
    public WebConfig(MenuPermissionService permissionService) {
        this.permissionService  = permissionService ;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(menuAuthorizationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(WhiteListUrlCheck.WHITELIST_ALL_URLS.toArray(new String[0])); // 공통 허용 경로 제외
    }
    
    @Bean
    public MenuAuthorizationInterceptor menuAuthorizationInterceptor() {
        return new MenuAuthorizationInterceptor(permissionService);
    }
}