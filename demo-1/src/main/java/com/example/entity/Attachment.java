package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Attachment extends BaseEntity {
	private Long id;
    private Long boardId;
    private String originalName;
    private String savedName;
    private String filePath;
    private Long fileSize;
}
