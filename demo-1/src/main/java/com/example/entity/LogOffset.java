package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LogOffset extends BaseEntity{
	private long id;
	private String fileName;
	private long position;
    private long lastModified;
    
    public LogOffset() {
        // MyBatis용 기본 생성자
    }
    
    public LogOffset(long position, long lastModified) {
        this.position = position;
        this.lastModified = lastModified;
    }
}