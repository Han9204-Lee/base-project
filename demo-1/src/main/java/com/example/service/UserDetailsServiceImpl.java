package com.example.service;

import java.util.ArrayList;
import java.util.List;

import com.example.entity.Roles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.entity.User;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserMapper userMapper;
	private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
    	User user = userMapper.findByLoginId(loginId);
		if (user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId);
		}

        List<Roles> roles = roleMapper.getRolesByLoginId(loginId); // ex: ["ROLE_USER", "ROLE_ADMIN"]

        // Extract only the role names from Roles objects
        List<String> roleNames = roles.stream()
                .map(Roles::getName)
                .toList();

        // Convert role names to GrantedAuthority list
        List<GrantedAuthority> authorities = new ArrayList<>(roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());
        
        return new org.springframework.security.core.userdetails.User(
                user.getLoginId(),
                user.getPassword(),
                authorities
            );
	}
}
