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
//	Charset charset = StandardCharsets.UTF_8;
    private final LogOffsetMapper logOffsetMapper;
    private RandomAccessFile raf;
    
//    public void runClusterTask() {
//    	try {
//            File logFile = new File(LOG_FILE);
//            LogOffset offset = loadOffsetFromDB(LOG_FILE);
//
//            // 로테이션 감지
//            /*if (logFile.length() < offset.getPosition() && logFile.lastModified() != offset.getLastModified()) {
//                offset.setPosition(0);
//            }*/
//
//            try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
//                raf.seek(offset.getPosition());
//
//                String line;
//                while ((line = raf.readLine()) != null) {
//                    saveLogLine(new String(line.getBytes(), charset));
//                }
//
//                // 위치 업데이트
//                offset.setPosition(raf.getFilePointer());
//                offset.setLastModified(logFile.lastModified());
//                saveOffsetToDB(LOG_FILE, offset);
//            }
//
//            logger.info("[Quartz] 로그 읽기 완료");
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getLocalizedMessage());
//        }
//    }
    
    public void runClusterTask() {
		try {
			File logFile = new File(LOG_FILE);
			LogOffset offset = loadOffsetFromDB(LOG_FILE);

			LogTailReader(LOG_FILE);
			raf.seek(offset.getPosition()+1); // 마지막 읽은 위치로 이동

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = raf.read()) != -1) {
				if (b == '\n') {
					String line = baos.toString(StandardCharsets.UTF_8);
					saveLogLine(line.trim());
					baos.reset();
				} else {
					baos.write(b);
				}
			}

			// 마지막 줄 처리 (줄바꿈 없이 끝났을 경우)
			if (baos.size() > 0) {
				String line = baos.toString(StandardCharsets.UTF_8);
				saveLogLine(line.trim());
				//raf.seek(raf.getFilePointer() - baos.size());
			}

			offset.setPosition(raf.getFilePointer());
			offset.setLastModified(logFile.lastModified());
			saveOffsetToDB(LOG_FILE, offset);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
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