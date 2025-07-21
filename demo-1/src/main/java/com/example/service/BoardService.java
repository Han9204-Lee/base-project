package com.example.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.Board;
import com.example.mapper.BoardMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardMapper boardMapper;
    private final AttachmentService attachmentService;
    
    public void createBoard(Board board, List<MultipartFile> files) throws IOException {
        boardMapper.insertBoard(board);
        
        attachmentService.saveFiles(board.getId(), files);
    }
    
    @Transactional(readOnly = true)
    public Board getBoard(Long id) {
        return boardMapper.selectBoardById(id);
    }

    @Transactional(readOnly = true)
    public List<Board> getAllBoards(Board board) {
        return boardMapper.selectBoardList(board);
    }

    public void updateBoard(Board board) {
        boardMapper.updateBoard(board);
    }

    public void deleteBoard(Long id) {
        boardMapper.deleteBoard(id);
    }
}