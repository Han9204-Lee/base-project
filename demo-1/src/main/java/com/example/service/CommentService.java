package com.example.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Comment;
import com.example.mapper.CommentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentMapper commentMapper;

    public void addComment(Comment comment) {
        commentMapper.insertComment(comment);
    }

    public List<Comment> getCommentsByBoard(Long boardId) {
        return commentMapper.selectCommentsByBoardId(boardId);
    }

    public void deleteComment(Long id) {
        commentMapper.deleteComment(id);
    }
}