package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.entity.Comment;

@Mapper
public interface CommentMapper {
	int insertComment(Comment comment);
    List<Comment> selectCommentsByBoardId(Long boardId);
    void deleteComment(Long id);
}
