package com.example.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {
    List<String> getRolesByUserId(@Param("userId") String userId);
    List<String> findRolesByUserId(@Param("userId") String userId);
    int getRoleId(@Param("name") String name);
    int insertUserRoles(Map<String, Object> param);
}
