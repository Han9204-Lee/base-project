package com.example.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.common.error.CustomException;
import com.example.security.WhiteListUrlCheck;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
		logger.error("MethodArgumentNotValidException", ex);
		String message = ex.getBindingResult().getFieldError().getDefaultMessage();
		return ResponseEntity.badRequest().body(ApiResponse.error(400, message));
	}

	// 예: 사용자 정의 예외 (ex: UserNotFoundException extends RuntimeException)
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<?>> handleCustomException(HttpServletRequest request, CustomException ex) {
		logger.error("CustomException", ex);
		String uri = request.getRequestURI();
		// ✅ Swagger 요청은 무시하고 예외 그대로 던짐
		if (WhiteListUrlCheck.isWhitelistedForException(uri)) {
	    	throw ex;
	    }
		return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
				.body(ApiResponse.error(ex.getErrorCode().getCode(), ex.getMessage()));
	}

	// 나머지 예외 (디버깅용)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<?>> handleException(HttpServletRequest request, Exception ex) throws Exception {
		logger.error("Exception", ex);
		String uri = request.getRequestURI();
		// ✅ Swagger 요청은 무시하고 예외 그대로 던짐
		if (WhiteListUrlCheck.isWhitelistedForException(uri)) {
	    	throw ex;
	    }

		return ResponseEntity.internalServerError().body(ApiResponse.error(500, "서버 오류: " + ex.getMessage()));
	}
}

