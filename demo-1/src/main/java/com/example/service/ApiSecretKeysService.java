package com.example.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.ApiSecretKeys;
import com.example.mapper.ApiSecretKeysMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiSecretKeysService {
    private final ApiSecretKeysMapper apiSecretKeysMapper;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional(readOnly = true)
    public ApiSecretKeys findByClientId(String clientId) {
    	return apiSecretKeysMapper.findByClientId(clientId);
    }
    
    @Transactional(readOnly = true)
    public int totalCount(String clientId, String clientSecret) {
    	return apiSecretKeysMapper.totalCount(clientId, clientSecret);
    }
    
    public void updateRefreshToken(String clientId, String refreshToken) {
    	Map<String, Object> param = new HashMap<>();
    	param.put("clientId", clientId);
    	param.put("refreshToken", refreshToken);
    	
    	apiSecretKeysMapper.updateRefreshToken(param);
    }
    
	public boolean isMatch(String clientId, String clientSecret) {
		ApiSecretKeys apiSecretKeys = findByClientId(clientId);
		String storedSecret = apiSecretKeys.getClientSecret();
		boolean isMatch = encoder.matches(clientSecret, storedSecret);
		return isMatch;
	}
}