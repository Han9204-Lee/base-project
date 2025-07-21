package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Comment extends BaseEntity {
    private Long id;
    private Long boardId;
    private Long writer;
    private String content;
}
