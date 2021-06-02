package com.copycoding.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.copycoding.demo.vo.FileListVO;

@Mapper
public interface FileListDao {

	public String addFile(FileListVO fl);
	
	public Integer sameFileChk(FileListVO fl);
	
	public Integer updateFile(FileListVO fl);
	
	public String deleteFile(String fname, String fpath);
}
