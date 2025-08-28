package com.example.service;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LogCopyServiceTest {

	@Autowired
    private LogCopyService logCopyService;
	
    @Test
    void 로그_읽기_테스트() throws IOException {
    	logCopyService.runClusterTask();
    }
}
