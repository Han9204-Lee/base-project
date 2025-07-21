package com.example.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {
	
	public byte[] exportExcel() throws Exception {
		List<String> hearders = Arrays.asList("이름", "나이", "날짜");
		List<Map<String, Object>> data = Arrays.asList(
	                Map.of("이름", "홍길동", "나이", 30, "날짜", "2025-06-26"),
	                Map.of("이름", "김철수", "나이", 25, "날짜", "2025-06-25 09:47")
		);
		 
		return createExcel(hearders, data);
	}

	public byte[] createExcel(List<String> hearders, List<Map<String, Object>> dataList) throws Exception {
		try (
				Workbook workbook = new XSSFWorkbook(); 
				ByteArrayOutputStream out = new ByteArrayOutputStream()
		) {
			Sheet sheet = workbook.createSheet("Sheet1");
			// 스타일 생성 (반복문 밖에서 한 번만 생성)
			CellStyle dateStyle = workbook.createCellStyle();
			CreationHelper creationHelper = workbook.getCreationHelper();
			dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd HH:mm")); // 날짜 포맷

			// 헤더 생성
			if(!hearders.isEmpty()) {
				int cellIdx = 0;
				Row headerRow = sheet.createRow(0);
				for (String header : hearders) {
					headerRow.createCell(cellIdx++).setCellValue(header);
				}
			}
			
			// 데이터 생성
			if (!dataList.isEmpty()) {
				// 데이터 행
				for (int i = 0; i < dataList.size(); i++) {
				    Row row = sheet.createRow(i + 1);
				    Map<String, Object> data = dataList.get(i);
				    for (int j = 0; j < hearders.size(); j++) {
				        String key = hearders.get(j);
				        Object value = data.get(key);
				        Cell cell = row.createCell(j);

						if (value instanceof Number) {
							cell.setCellValue(((Number) value).doubleValue()); // 숫자
						} else if (value instanceof Boolean) {
							cell.setCellValue((Boolean) value); // 불리언
						} else if (value instanceof LocalDate) {
							LocalDate localDate = (LocalDate) value;
							Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
							cell.setCellValue(date);
							cell.setCellStyle(dateStyle);
						} else if (value instanceof LocalDateTime) {
							LocalDateTime ldt = (LocalDateTime) value;
							Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
							cell.setCellValue(date);
							cell.setCellStyle(dateStyle);
						} else if (value instanceof Date) {
							cell.setCellValue((Date) value);
							cell.setCellStyle(dateStyle);
						} else {
							cell.setCellValue(value != null ? value.toString() : ""); // 문자열 또는 null
						}
					}
				}
				
			}

			workbook.write(out);
			return out.toByteArray();
		}
	}
}