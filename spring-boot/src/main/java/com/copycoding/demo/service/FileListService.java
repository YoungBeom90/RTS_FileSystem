package com.copycoding.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.copycoding.demo.vo.FileListVO;
import com.copycoding.demo.vo.FolderListVO;

public interface FileListService {
	//파일 등록
	public String registFile(List<MultipartFile> list, Long fdate, String parent);
	//폴더 생성
	public String createFolder(String value, String path);
	//파일삭제
	public String removeFile(String fname, String fpath, String fileExt);
	//폴더삭제
	public String removeDir(String fname, String fpath);
	//파일, 폴더 이름 수정
	public String renameFile(String fname, String fpath, String rename);
	//파일목록, 폴더목록 불러오기
	public List<FileListVO> selectFileList(String ppath);
	public List<FolderListVO> selectFolderList(String fpath);

	//폴더트리목록(경로)
	//public List<FileListVO> showFolderTree(String fpath);

	//폴더트리목록(DB)
	public List<FolderListVO> showFolderTree(String fpath);
	//파일 이동
	public int moveFile(String prevPath, String nextPath);
	
}
