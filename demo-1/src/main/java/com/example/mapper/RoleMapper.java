package com.example.mapper;

import java.util.List;
import java.util.Map;

import com.example.entity.Roles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {
    List<Roles> getRolesByLoginId(@Param("loginId") String loginId);
    int getRoleId(@Param("name") String name);
    int insertUserRoles(Map<String, Object> param);
}
