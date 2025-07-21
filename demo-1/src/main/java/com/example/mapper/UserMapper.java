package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.entity.User;

@Mapper
public interface UserMapper {
	List<User> getAllUsers();
	User findByUserId(@Param("userId") String userId);
	int insertUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
    void updateRefreshToken(@Param("userId") String userId, @Param("refreshToken") String token);
    void insertUsers(@Param("users") List<User> users);
}
