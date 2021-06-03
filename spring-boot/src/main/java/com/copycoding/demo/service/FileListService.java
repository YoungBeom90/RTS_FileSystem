package com.copycoding.demo.service;

import java.util.List;

import com.copycoding.demo.vo.FileListVO;

public interface FileListService {

	public String registFile(FileListVO fl);
	
	public String removeFile(String fname, String fpath);
	
	public String removeDir(String fname, String fpath);
	
	public String renameFile(String fname, String fpath, String rename);
	
	public String selectFileList(String ppath);
	
	public List<FileListVO> showFolderTree(String fpath);
}
