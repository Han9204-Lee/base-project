package com.example.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.example.common.ApiResponse;
import com.example.entity.Attachment;
import com.example.entity.Board;
import com.example.entity.BoardLike;
import com.example.entity.Comment;
import com.example.service.AttachmentService;
import com.example.service.BoardLikeService;
import com.example.service.BoardService;
import com.example.service.CommentService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Hidden
public class BoardController {
    private final BoardService boardService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;
    private final BoardLikeService boardLikeService;

    
	@GetMapping
	public ResponseEntity<ApiResponse<List<Board>>> getAllBoards(@RequestBody Board board) {
		List<Board> boardList = boardService.getAllBoards(board);
		String message = "Success";
		return ResponseEntity.ok(ApiResponse.success(message, boardList));
	}
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<?>> createBoardWithFiles(
            @RequestPart("board") Board board,
            @RequestPart("files") List<MultipartFile> files) throws IOException {

        boardService.createBoard(board, files);

        String message = "게시글과 파일 업로드 성공";
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateBoard(@PathVariable Long id, @RequestBody Board board) {
        board.setId(id);
        String message = "게시글 수정 성공";
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        String message = "게시글 삭제 성공";
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/{boardId}/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long boardId, @PathVariable Long fileId) throws IOException {
        Attachment file = attachmentService.getFile(boardId, fileId);
        if (file == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다.");
        }

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            throw new FileNotFoundException("리소스가 존재하지 않습니다.");
        }

        // ✅ 한글 파일명 깨짐 방지
        String encodedFileName = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);

        // ✅ 다운로드 응답
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }
    
    // 댓글 등록
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<Void> addComment(@PathVariable Long boardId, @RequestBody Comment comment) {
        comment.setBoardId(boardId);
        commentService.addComment(comment);
        return ResponseEntity.ok().build();
    }

    // 댓글 조회
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getCommentsByBoard(boardId));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
    
    // 좋아요 추가
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<?>> likeBoard(@RequestBody BoardLike like) {
        boardLikeService.likeBoard(like);
        String message = "좋아요가 등록되었습니다.";
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    // 좋아요 취소
    @DeleteMapping("/like/{id}")
    public ResponseEntity<ApiResponse<?>> unlikeBoard(@RequestBody BoardLike like) {
        boardLikeService.unlikeBoard(like);
        String message = "좋아요가 취소되었습니다.";
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}