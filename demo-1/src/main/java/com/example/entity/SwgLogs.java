package com.example.entity;

import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SwgLogs extends BaseEntity{
	private long id;
    private Timestamp logTime;
    private String level;
    private String message;
}