package com.copycoding.demo.service;

import java.util.List;
import java.util.Map;

import com.copycoding.demo.vo.FileListVO;

public interface FileListService {
	//파일,폴더 등록
	public String registFile(FileListVO fl);
	//파일삭제
	public String removeFile(String fname, String fpath);
	//폴더삭제
	public String removeDir(String fname, String fpath);
	//파일, 폴더 이름 수정
	public String renameFile(String fname, String fpath, String rename);
	//파일목록
	public List<FileListVO> selectFileList(String ppath);
	//폴더트리목록
	public List<FileListVO> showFolderTree(String fpath);
	//파일 이동
	public int moveFile(String prevPath, String nextPath, String fname);
	
}
