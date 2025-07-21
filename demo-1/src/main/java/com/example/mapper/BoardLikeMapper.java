package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.entity.BoardLike;

@Mapper
public interface BoardLikeMapper {
	int insertLike(BoardLike boardLike);
    void deleteLike(BoardLike boardLike);
    int countLikes(Long boardId);
    boolean hasUserLiked(BoardLike boardLike);
}
