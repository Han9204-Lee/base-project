package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.ApiResponse;
import com.example.common.error.CustomException;
import com.example.common.error.ErrorCode;
import com.example.entity.User;
import com.example.service.UserService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;
    
	@GetMapping("/users")
	@Operation(
		summary = "사용자 목록 조회"
		, description = "사용자 목록을 조회 합니다."
		, security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		List<User> userList = userService.getAllUsers();
		String message = "Success";
		return ResponseEntity.ok(ApiResponse.success(message, userList));
	}
	
	@GetMapping("/users/{userId}")
	@Operation(
		summary = "사용자를 조회"
		, description = "사용자를 조회 합니다."
		, security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<User>> getUser(@PathVariable("userId") String userId) {
		User user = userService.findByUserId(userId);
		if (user == null) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		String message = "Success";
		return ResponseEntity.ok(ApiResponse.success(message, user));
	}

	@PostMapping("/users")
	@Operation(
		summary = "사용자 추가"
		, description = "사용자를 추가 합니다."
		, security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User userInfo) {
		userService.createUser(userInfo, userInfo.getRoles());
		
		String userId = userInfo.getUserId();
		User user = userService.findByUserId(userId);
		
		String message = "Success";
		return ResponseEntity.ok(ApiResponse.success(message, user));
	}

	@PutMapping("/users/{userId}")
	@Operation(
		summary = "사용자 정보 수정"
		, description = "사용자 정보를 수정 합니다."
		, security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable("userId") String userId, @RequestBody User userInfo) {
		userInfo.setUserId(userId);
		User user = userService.updateUser(userInfo);

		String message = "Success";
		return ResponseEntity.ok(ApiResponse.success(message, user));
	}

	@DeleteMapping("/users/{userId}")
	@Operation(
		summary = "사용자 삭제"
		, description = "사용자를 삭제 합니다."
		, security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<User>> deleteUser(@RequestBody User user) {
		userService.deleteUser(user);
		
		String message = "Success";
		return ResponseEntity.ok(ApiResponse.success(message, null));
	}
	
	@PostMapping("/user/execute")
	@Hidden
	public ResponseEntity<ApiResponse<?>> userExecute() {
		int batchSize = userService.batchInsertUsers();
		String message = "made data " + batchSize;
		return ResponseEntity.ok(ApiResponse.success(message));
	}
}