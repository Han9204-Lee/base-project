package com.example.service;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.entity.BoardLike;

@SpringBootTest
public class BoardLikeServiceTest {

	@Autowired
    private BoardLikeService boardLikeService;

    @Test
    void 게시판_좋아요_테스트() throws IOException {
    	BoardLike like = new BoardLike();
    	like.setBoardId(4L);
    	like.setUserId(2L);
    	boardLikeService.likeBoard(like);
    }
}
