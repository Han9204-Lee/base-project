package com.example.entity;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BoardLike extends BaseEntity {
    private Long id;
    private Long boardId;
    private Long userId;
    private LocalDateTime likedAt;
}
