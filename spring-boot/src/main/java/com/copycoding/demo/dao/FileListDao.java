package com.copycoding.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.copycoding.demo.vo.FileListVO;

@Mapper
public interface FileListDao {

	public int addFile(FileListVO fl);
	
}
