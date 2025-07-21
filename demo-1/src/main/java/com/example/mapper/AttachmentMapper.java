package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.entity.Attachment;

@Mapper
public interface AttachmentMapper {
	int insertAttachment(Attachment attachment);
	Attachment findByAttachmentId(@Param("boardId") Long boardId, @Param("id") Long id);
    List<Attachment> selectAttachmentsByBoardId(Long boardId);
    void deleteAttachment(Long id);
}
