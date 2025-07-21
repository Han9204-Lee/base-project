package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.entity.Board;

@Mapper
public interface BoardMapper {
	List<Board> selectBoardList(Board board);
	Board selectBoardById(@Param("id") Long id);
	int insertBoard(Board board);
    void updateBoard(Board board);
    void deleteBoard(@Param("id") Long id);
}
