package com.example.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.LogOffset;
import com.example.entity.SwgLogs;
import com.example.mapper.LogOffsetMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LogCopyService {
	private static final Logger logger = LoggerFactory.getLogger(LogCopyService.class);
	private static final String LOG_FILE = "C:/work/application.log";
    private final LogOffsetMapper logOffsetMapper;
    private RandomAccessFile raf;
    
    public void runClusterTask() {
        try {
            File logFile = new File(LOG_FILE);
            LogOffset offset = loadOffsetFromDB(LOG_FILE);

            LogTailReader(LOG_FILE);
            // 저장된 위치부터 읽기 (마지막으로 완료된 줄 다음 위치)
            raf.seek(offset.getPosition());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            long lastCompleteLinePointer = offset.getPosition();
            while ((b = raf.read()) != -1) {
                if (b == '\n') {
                    String line = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                    saveLogLine(line.trim());
                    baos.reset();
                    // '\n' 까지 읽은 다음 위치를 저장 (완료된 라인의 끝)
                    lastCompleteLinePointer = raf.getFilePointer();
                } else {
                    baos.write(b);
                }
            }

            // 마지막에 줄바꿈이 없는 미완성 라인은 저장/처리하지 않음
            // 다음 실행 때 이어서 읽을 수 있도록 오프셋을 마지막 완료 지점으로 설정
            offset.setPosition(lastCompleteLinePointer);
            offset.setLastModified(logFile.lastModified());
            saveOffsetToDB(LOG_FILE, offset);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        } finally {
            try { if (raf != null) raf.close(); } catch (IOException ignore) {}
        }
    }
    
    public void LogTailReader(String filePath) throws IOException {
        this.raf = new RandomAccessFile(filePath, "r");
    }
    
    private void saveLogLine(String logLine) {
		String[] parts = logLine.split(" ", 3);
		String level = parts.length > 1 ? parts[1] : "";
		String message = parts.length > 2 ? parts[2] : "";

		SwgLogs swgLogs = new SwgLogs();
		swgLogs.setLogTime(Timestamp.valueOf(LocalDateTime.now()));
		swgLogs.setLevel(level);
		swgLogs.setMessage(message);
		logOffsetMapper.insertLog(swgLogs);
	}

    public void close() throws IOException {
        raf.close();
    }
    
    private LogOffset loadOffsetFromDB(String fileName) {
    	 LogOffset offset = logOffsetMapper.findByFileName(fileName);
         if (offset == null) {
             return new LogOffset(0, 0); // 기본값 처리
         }
         return offset;
    }

    private void saveOffsetToDB(String fileName, LogOffset offset) {
    	offset.setFileName(fileName);
    	int updated = logOffsetMapper.updateLogOffset(offset);
    	
        if (updated == 0) {
        	logOffsetMapper.insertLogOffset(offset);
        }
    }
}