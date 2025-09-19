package com.example;


import java.util.Arrays;
import java.util.List;

import com.example.entity.Roles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.entity.User;
import com.example.mapper.UserMapper;

@SpringBootApplication
@EnableScheduling
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner init(UserMapper userMapper, PasswordEncoder encoder) {
        return args -> {
        	String userId = "user123";
        	User existingUser = userMapper.findByLoginId(userId);
        	if (existingUser == null) {
                User user = new User();
                user.setLoginId(userId);
                user.setUserName("테스트사용자");
                user.setPassword(encoder.encode("1234"));
                user.setRoles(List.of(new Roles("ROLE_USER")));
                
                userMapper.insertUser(user);
                System.out.println("초기 사용자 생성 완료!");
            }
        	
        	userId = "admin123";
        	existingUser = userMapper.findByLoginId(userId);
        	if (existingUser == null) {
                User user = new User();
                user.setLoginId(userId);
                user.setUserName("테스트관리자");
                user.setPassword(encoder.encode("1234"));
                user.setRoles(List.of(new Roles("ROLE_ADMIN")));
                user.setActive(true);
                
                userMapper.insertUser(user);
                System.out.println("초기 사용자 생성 완료!");
            }
        };
    }
}
