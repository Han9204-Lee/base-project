package com.example.security;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.AntPathMatcher;

public class WhiteListUrlCheck {
	
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    // ✅ 공통 허용 URL 정의
	public static final List<String> WHITELIST_COMMON_URLS = List.of(
            "/auth/**"
            , "/static/**"
    );
	
	public static final List<String> WHITELIST_SWAGGER_URLS = List.of(
             "/swagger-ui/**"
            , "/swagger-ui.html"
            , "/api-docs/**"
    );
	
	public static final List<String> WHITELIST_ALL_URLS = Stream.concat(WHITELIST_COMMON_URLS.stream(), WHITELIST_SWAGGER_URLS.stream())
            .collect(Collectors.toUnmodifiableList());

    // ✅ 주어진 URI가 화이트리스트 패턴과 일치하는지 체크
	public static boolean isWhitelisted(String requestUri) {
		return WHITELIST_ALL_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
	}
	
	public static boolean isWhitelistedForException(String requestUri) {
		return WHITELIST_SWAGGER_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
	}
}
