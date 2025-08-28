package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.entity.LogOffset;
import com.example.entity.SwgLogs;

@Mapper
public interface LogOffsetMapper {
	LogOffset findByFileName(@Param("fileName") String fileName);
	int insertLogOffset(LogOffset logOffset);
	int updateLogOffset(LogOffset logOffset);
	int insertLog(SwgLogs swgLogs);
}
