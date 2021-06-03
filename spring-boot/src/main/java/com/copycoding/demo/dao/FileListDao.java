package com.copycoding.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.copycoding.demo.vo.FileListVO;

@Mapper
public interface FileListDao {
	//파일업로드
	public String addFile(FileListVO fl);
	//업로드시 같은파일이 DB에 있나 확인
	public Integer sameFileChk(FileListVO fl);
	//업로드시 같은파일이 존재할 경우 update
	public Integer updateFile(FileListVO fl);
	//파일삭제
	public String deleteFile(String fname, String fpath);
	//폴더삭제
	public String deleteDir(String fname, String fpath);
	//해당 경로의 파일 이름수정
	public String rename(String fname, String fpath, String rename);
	//JTree 폴더 목록 불러오기
	public List<FileListVO> showFolderTree(String fpath);
	//해당파일경로의 파일목록 불러오기
	public String selectFileList(String ppath);
}
