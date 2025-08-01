package com.example.entity;

import java.util.List;

import com.example.enums.PostType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Board extends BaseEntity {
	private Long id;
	private PostType postType;
	private String title;
	private String content;
	private Long writer;

	private List<Attachment> attachments;
	private List<Comment> comments;
}
