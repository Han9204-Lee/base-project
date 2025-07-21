package com.example.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.entity.User;

@SpringBootTest
public class UserServiceTest {

	@Autowired
    private UserService userService;

    @Test
    void 트랜잭션_테스트() {
        User user = new User();
        user.setUserName("testUser1");
        user.setPassword("test1234");

        try {
        	List<String> roles = List.of("ROLE_USER");
        	user.setRoles(roles);
        	
            userService.createUser(user, user.getRoles());
        } catch (Exception e) {
            System.out.println("예외 발생 확인 → 롤백 기대됨");
        }
    }
}
