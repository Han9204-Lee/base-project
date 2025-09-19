package com.example.mapper;

import java.util.List;

import com.example.entity.Roles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MenuMapper {
    List<String> getUrisByRoles(@Param("roles") List<Roles> roles);
}