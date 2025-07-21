package com.example.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.config.FileStorageProperties;
import com.example.entity.Attachment;
import com.example.mapper.AttachmentMapper;
import com.example.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentService {
	private final AttachmentMapper attachmentMapper;
	private final FileStorageProperties fileStorageProperties;

	public void saveFiles(Long boardId, List<MultipartFile> files) throws IOException {
		String uploadDir = fileStorageProperties.getUploadDir();
		Files.createDirectories(Paths.get(uploadDir));
		
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				String originalName = file.getOriginalFilename();

	            // 맥 한글깨짐 방지
	            originalName = FileUtil.normalizeFileName(originalName);
	            
				String savedName = UUID.randomUUID() + "_" + originalName;
				Path path = Paths.get(uploadDir + File.separator + savedName);
				Files.copy(file.getInputStream(), path);

				Attachment attachment = new Attachment();
				attachment.setBoardId(boardId);
				attachment.setOriginalName(originalName);
				attachment.setSavedName(savedName);
				attachment.setFilePath(path.toString());
				attachment.setFileSize(file.getSize());

				attachmentMapper.insertAttachment(attachment);
			}
		}
	}

	public Attachment getFile(Long boardId, Long fileId) {
		return attachmentMapper.findByAttachmentId(boardId, fileId);
	}
	
	public List<Attachment> getFiles(Long boardId) {
		return attachmentMapper.selectAttachmentsByBoardId(boardId);
	}

	public void deleteFile(Long id) {
		attachmentMapper.deleteAttachment(id);
	}
}