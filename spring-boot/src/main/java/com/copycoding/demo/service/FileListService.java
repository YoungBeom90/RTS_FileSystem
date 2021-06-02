package com.copycoding.demo.service;

import com.copycoding.demo.vo.FileListVO;

public interface FileListService {

	public String registFile(FileListVO fl);
	
	public String removeFile(String fname, String fpath);
	
	public String removeDir(String fname, String fpath);
	
}
