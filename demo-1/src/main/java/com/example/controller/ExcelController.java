package com.example.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ExcelService;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @GetMapping("/excel/download")
    public ResponseEntity<byte[]> downloadExcel() throws Exception {
        byte[] excelBytes = excelService.exportExcel();

        String fileName = URLEncoder.encode("샘플_엑셀.xlsx", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}