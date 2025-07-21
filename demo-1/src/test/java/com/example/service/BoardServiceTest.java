package com.example.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.Attachment;
import com.example.entity.Board;
import com.example.entity.Comment;

@SpringBootTest
public class BoardServiceTest {

	@Autowired
    private BoardService boardService;
	@Autowired
    private CommentService commentService;
	@Autowired
    private AttachmentService attachmentService;
	
    @Test
    void 게시글_목록_테스트() throws IOException {
    	Board board = new Board();
    	List<Board> boardList = boardService.getAllBoards(board);
    	
    	for(Board vo : boardList) {
    		Long boardId = vo.getId();
    		List<Comment> commentList = commentService.getCommentsByBoard(boardId);
    		vo.setComments(commentList);
    		
    		List<Attachment> fileList = attachmentService.getFiles(boardId);
    		vo.setAttachments(fileList);
    		
    		System.out.println(vo.toString());
    	}
    }
	
    @Test
    void 게시글_상세정보_테스트() throws IOException {
    	Board board = boardService.getBoard(4L);
    	System.out.println(board.toString());
    }

    @Test
    void 게시글_생성_with_file() throws IOException {
    	List<MultipartFile> files = new ArrayList<>();

        // 테스트용 파일 생성
        MockMultipartFile file1 = new MockMultipartFile(
                "files",                   // form name
                "test.txt",                // original file name
                "text/plain",              // content type
                "테스트 파일 내용입니다.".getBytes() // file content
        );
        files.add(file1);

        
    	Board board = new Board();
    	board.setTitle("테스트 제목입니다.");
    	board.setContent("냉무");
    	board.setWriter(1L);
    	
        boardService.createBoard(board, files);
    }
}
