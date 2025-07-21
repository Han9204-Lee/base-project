package com.example.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.entity.ApiSecretKeys;

@Mapper
public interface ApiSecretKeysMapper {
	ApiSecretKeys findByClientId(@Param("clientId") String clientId);
	int totalCount(@Param("clientId") String clientId, @Param("clientSecret") String clientSecret);
	void updateRefreshToken(Map<String, Object> param);
}
