package com.copycoding.demo.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.copycoding.demo.vo.FileListVO;

@Mapper
public interface FileListDao {
	//파일업로드
	public String addFile(FileListVO fl);
	
	//파일 업로드시 폴더 size 증가
	public String addFolderSizeUpdate(FileListVO fl);
	
	//업로드시 같은파일이 DB에 있나 확인
	public Integer sameFileChk(FileListVO fl);
	
	//업로드시 같은파일이 존재할 경우 update
	public Integer updateFile(FileListVO fl);
	
	//업데이트시 부모 폴더 사이즈 수정
	public Integer updateFolderSizeUpdate(FileListVO fl);
	
	//파일삭제
	public String deleteFile(String fname, String fpath);
	
	//삭제시 부모폴더 사이즈 수정
	public String deleteFolderSizeUpdate(String fname, String ppath);

	//폴더삭제
	public String deleteDir(String fname, String fpath);
	
	//해당 경로의 파일 이름수정
	public String renameFile(String fname, String fpath, String rename);
	
	//이름수정시 DB의 부모경로, 내경로 경로명 수정
	public String renameFolderPath(String fname, String fpath, String rename);
	
	//JTree 폴더 목록 불러오기
	public List<FileListVO> showFolderTree(String fpath);
	
	//해당파일경로의 파일목록 불러오기
	public List<FileListVO> selectFileList(String ppath);
	
	//파일 이동
	public int moveFile(String prevPath, String nextPath, String fname);
}
