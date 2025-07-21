package com.example.service;

import java.util.ArrayList;
import java.util.List;

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
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    	User user = userMapper.findByUserId(userId);
		if (user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
		}

        List<String> roles = roleMapper.getRolesByUserId(userId); // ex: ["ROLE_USER", "ROLE_ADMIN"]

        List<GrantedAuthority> authorities = new ArrayList<>(roles.stream()
        		.map(role -> new SimpleGrantedAuthority(role))
        	    .toList());
        
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                authorities
            );
	}
}
