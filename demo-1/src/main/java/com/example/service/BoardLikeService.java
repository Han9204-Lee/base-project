package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.BoardLike;
import com.example.mapper.BoardLikeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardLikeService {
	private final BoardLikeMapper boardLikeMapper;

    public void likeBoard(BoardLike like) {
        if (!boardLikeMapper.hasUserLiked(like)) {
            boardLikeMapper.insertLike(like);
        }
    }

    public void unlikeBoard(BoardLike like) {
        boardLikeMapper.deleteLike(like);
    }

    public int getLikeCount(Long boardId) {
        return boardLikeMapper.countLikes(boardId);
    }
}