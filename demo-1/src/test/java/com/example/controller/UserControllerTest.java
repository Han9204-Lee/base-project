package com.example.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.security.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
	private JwtUtil jwtUtil;
    
    private String jwtToken;

    @BeforeEach
    void setup() {
    	
        // 테스트 JWT 토큰 생성
    	String userId = "admin123";
    	List<String> userRoles = Arrays.asList("ROLE_ADMIN");
        jwtToken = jwtUtil.generateAccessToken(userId, userRoles);
    }
    
    @Test
    void getUserByIdTest() throws Exception {
        mockMvc.perform(get("/users/admin123")
        				.header("Authorization", "Bearer " + jwtToken)
        		) // URL 호출
               .andExpect(status().isOk()) // 200 OK 확인
               .andExpect(jsonPath("$.data.userId").value("admin123")); // JSON 응답 검증
    }
}