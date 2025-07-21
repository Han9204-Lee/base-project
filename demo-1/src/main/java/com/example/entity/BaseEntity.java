package com.example.entity;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private boolean isActive;
    
    // 페이징 처리를 위한 기본값
    private int page = 1;
    private int pageSize = 10;
    private int offset = (page - 1) * pageSize;
}
