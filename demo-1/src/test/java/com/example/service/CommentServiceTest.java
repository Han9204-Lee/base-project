package com.example.service;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.entity.Comment;

@SpringBootTest
public class CommentServiceTest {

	@Autowired
    private CommentService commentService;

    @Test
    void 코멘트_테스트() throws IOException {
    	Comment comment = new Comment();
    	comment.setBoardId(4L);
    	comment.setWriter(2L);
    	comment.setContent("댓글 입니다.3");
        commentService.addComment(comment);
    }
}
