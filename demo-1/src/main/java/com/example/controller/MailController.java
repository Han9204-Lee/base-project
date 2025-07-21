package com.example.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.MailService;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Hidden
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody Map<String, String> body) throws MessagingException {
    	String to = body.get("to");
        mailService.sendMail(to, "테스트 메일", "이것은 테스트 메일입니다.");
        return ResponseEntity.ok("메일 발송 완료");
    }
    
    @PostMapping("/send-attachment")
    public ResponseEntity<String> sendWithAttachment(@RequestBody Map<String, String> body) throws MessagingException {
    	String to = body.get("to");
    	String path = body.get("path");
    	
        mailService.sendMailWithAttachment(to, "첨부파일 메일 테스트", "이것은 첨부파일이 포함된 메일입니다.", path);
        return ResponseEntity.ok("첨부파일 메일 전송 완료");
    }
    
    @PostMapping("/send-html")
    public ResponseEntity<String> sendHtml(@RequestBody Map<String, String> body) throws MessagingException {
    	String to = body.get("to");
        String html = """
            <html>
              <body>
                <h1 style='color: blue;'>HTML 메일 테스트</h1>
                <p>안녕하세요. <strong>Spring Boot</strong>에서 보낸 HTML 메일입니다.</p>
                <p>감사합니다.</p>
              </body>
            </html>
            """;

        mailService.sendHtmlMail(to, "HTML 메일 테스트", html);
        return ResponseEntity.ok("HTML 메일 발송 완료");
    }
}