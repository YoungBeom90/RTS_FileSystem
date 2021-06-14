package com.copycoding.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.copycoding.demo.vo.FileListVO;
import com.copycoding.demo.vo.FolderListVO;

@Mapper
public interface FolderListDao {
	//폴더 등록
	public String addFolder(FolderListVO fl);
	// 파일등록시 폴더ID 확인
	public String getPpath(String ppath);
	//동일 이름 폴더 확인
	public int sameFolderChk(FolderListVO fl);
	//폴더 크기 수정
	public Integer folderSizeUpdate(String pid);
	//폴더 삭제
	public String deleteDir(String fname, String fpath);
	//폴더명 수정
	public String renameFolder(String fname, String fpath, String rename);
	//이름수정시 DB의 부모경로, 내경로 경로명 수정
	public String renameFolderPath(String fname, String fpath, String rename);
	//해당 경로의 폴더목록 불러오기
	public List<FolderListVO> selectFolderList(String ppath);
	//JTree 폴더 목록 불러오기
	public List<FolderListVO> showFolderTree(String fpath);

}
