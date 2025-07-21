package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.ApiResponse;
import com.example.common.error.CustomException;
import com.example.common.error.ErrorCode;
import com.example.dto.LoginSystemDto;
import com.example.dto.LoginUserDto;
import com.example.dto.TokenDto;
import com.example.entity.ApiSecretKeys;
import com.example.entity.User;
import com.example.security.JwtUtil;
import com.example.service.ApiSecretKeysService;
import com.example.service.UserService;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "AuthController", description = "토큰과 관련된 API입니다.")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private ApiSecretKeysService apiSecretKeysService;

	@PostMapping("/login")
	@Operation(summary = "사용자계정 로그인", description = "사용자 계정을 로그인하고, 토큰을 발급 받습니다.")
	public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginUserDto loginUserDto) {
		String userId = loginUserDto.getUserId();
		String password = loginUserDto.getPassword();

		Authentication authentication = new UsernamePasswordAuthenticationToken(userId, password);
		authenticationManager.authenticate(authentication);

		User user = userService.findByUserId(userId);

		String accessToken = jwtUtil.generateAccessToken(userId, user.getRoles());
		String refreshToken = jwtUtil.generateRefreshToken(userId, user.getRoles());

		// DB에 리프레시 토큰 저장
		userService.updateRefreshToken(user.getUserId(), refreshToken);

		Map<String, Object> token = new HashMap<>();
		token.put("accessToken", accessToken);
		token.put("refreshToken", refreshToken);
		return ResponseEntity.ok(ApiResponse.success("Login success", token));
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/refresh")
	@Operation(
		summary = "토큰 갱신"
		, description = "Refresh Token을 이용해 Access Token 재발급"
	)
	public ResponseEntity<ApiResponse<?>> refresh(@RequestBody TokenDto tokenDto) {
		String refreshToken = tokenDto.getRefreshToken();
		// 1. Refresh Token 유효성 확인
		if (!jwtUtil.validateToken(refreshToken)) {
			String message = "Invalid or expired refresh token";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(message));
		}

		// 2. 토큰에서 Claims 추출
		Claims claims = jwtUtil.extractAllClaims(refreshToken);
		String subject = claims.getSubject(); // userId or clientId
		List<String> roles = (List<String>) claims.get("roles");

		// 3. 사용자/클라이언트 판별 (by roles 또는 custom claim)
		boolean isClient = roles.contains("ROLE_API_CLIENT");
		String newAccessToken = null;
		
		if (isClient) {
			newAccessToken = handleClientRefresh(subject, refreshToken);
		} else {
			newAccessToken = handleUserRefresh(subject, refreshToken);
		}
		
		if (newAccessToken == null) {
	        String message = "Token mismatch or subject not found";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(message));
	    }
		
		Map<String, Object> token = new HashMap<>();
		token.put("accessToken", newAccessToken);
		token.put("refreshToken", refreshToken);
		return ResponseEntity.ok(ApiResponse.success("Renewed success", token));
	}
	
	private String handleClientRefresh(String clientId, String refreshToken) {
	    ApiSecretKeys client = apiSecretKeysService.findByClientId(clientId);
	    if (client == null || !refreshToken.equals(client.getRefreshToken())) {
	        return null;
	    }
	    return jwtUtil.generateAccessToken(clientId, List.of("ROLE_API_CLIENT"));
	}

	private String handleUserRefresh(String userId, String refreshToken) {
	    User user = userService.findByUserId(userId);
	    if (user == null || !refreshToken.equals(user.getRefreshToken())) {
	        return null;
	    }
	    return jwtUtil.generateAccessToken(userId, List.of("ROLE_USER"));
	}

	@PostMapping("/logout")
	@Operation(
		summary = "로그아웃"
		, description = "사용자계정을 로그아웃 합니다."
	    , security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<?>> logout(Authentication authentication) {
		String userId = authentication.getName();

		User user = userService.findByUserId(userId);
		if (user == null) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
		userService.updateRefreshToken(userId, null);

		String message = "Logged out";
		return ResponseEntity.ok(ApiResponse.success(message, null));
	}

	@PostMapping("/system/login")
	@Operation(
			summary = "시스템계정 로그인"
			, description = "시스템계정을 로그인하고, 토큰을 발급 받습니다."
			, security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<?>> token(@RequestBody LoginSystemDto loginSystemDto) {
		String clientId = loginSystemDto.getClientId();
		String clientSecret = loginSystemDto.getClientSecret();
		// 1. 클라이언트 검증
		if (!apiSecretKeysService.isMatch(clientId, clientSecret)) {
			String message = "Invalid client credentials";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(message));
		}

		// 2. 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(clientId, List.of("ROLE_API_CLIENT"));
		String refreshToken = jwtUtil.generateRefreshToken(clientId, List.of("ROLE_API_CLIENT"));

		// DB에 리프레시 토큰 저장
		apiSecretKeysService.updateRefreshToken(clientId, refreshToken);

		Map<String, Object> token = new HashMap<>();
		token.put("accessToken", accessToken);
		token.put("refreshToken", refreshToken);
		return ResponseEntity.ok(ApiResponse.success("Login success", token));
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/system/logout")
	@Operation(
			summary = "시스템계정 로그아웃"
			, description = "시스템계정을 로그아웃 합니다."
			, security = { @SecurityRequirement(name = "bearerAuth") }
	)
	public ResponseEntity<ApiResponse<?>> logoutSystem(HttpServletRequest request) {
	    String token = jwtUtil.resolveTokenFromHeader(request);
        
		// 1. Refresh Token 유효성 확인
		if (!jwtUtil.validateToken(token)) {
			String message = "Invalid or expired refresh token";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(message));
		}

		// 2. 토큰에서 Claims 추출
		Claims claims = jwtUtil.extractAllClaims(token);
		List<String> roles = (List<String>) claims.get("roles");

		// 3. 사용자/클라이언트 판별 (by roles 또는 custom claim)
		boolean systemAccount = roles.contains("ROLE_API_CLIENT");
		
	    if (!systemAccount) {
	        throw new CustomException(ErrorCode.NOT_ALLOW); // 사람이 이 API 쓰는 거 막기
	    }
	    
	    String systemId = jwtUtil.extractUserId(token);
	    apiSecretKeysService.updateRefreshToken(systemId, null); // refresh token 삭제

	    String message = "System account logged out";
		return ResponseEntity.ok(ApiResponse.success(message, null));
	}
}