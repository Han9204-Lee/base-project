package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.entity.User;

@Mapper
public interface UserMapper {
	List<User> getAllUsers();
	User findByLoginId(@Param("loginId") String loginId);
	void insertUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
    void updateRefreshToken(@Param("loginId") String loginId, @Param("refreshToken") String token);
    void insertUsers(@Param("users") List<User> users);
}
