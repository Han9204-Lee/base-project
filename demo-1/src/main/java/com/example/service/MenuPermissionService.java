package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import com.example.mapper.MenuMapper;
import com.example.mapper.RoleMapper;
import com.example.security.WhiteListUrlCheck;

@Service
public class MenuPermissionService {

    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    @Autowired
    private AntPathMatcher pathMatcher;
    
    public MenuPermissionService(RoleMapper roleMapper, MenuMapper menuMapper) {
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
    }
    
	public boolean hasPermission(String userId, String requestUri) {

		if (WhiteListUrlCheck.isWhitelisted(requestUri)) {
			return true;
		}

		List<String> roles = roleMapper.getRolesByUserId(userId); // 예: [ROLE_ADMIN, ROLE_USER]

		if (roles.isEmpty()) {
			return false;
			// ADMIN 권한이 있으면 무조건 허용
		} else if (roles.contains("ROLE_ADMIN")) {
			return true;
		}

		List<String> allowedUris = menuMapper.getUrisByRoles(roles); // 예: [/admin/**, /user/profile]
		// TODO role이 아닌 url만 단독으로 개별 부여시 추가하여 체크하기
		return allowedUris.stream().anyMatch(uriPattern -> pathMatcher.match(uriPattern, requestUri));
	}
}