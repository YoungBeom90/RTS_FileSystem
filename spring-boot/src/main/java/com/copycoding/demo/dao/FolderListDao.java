package com.copycoding.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.copycoding.demo.vo.FolderListVO;

@Mapper
public interface FolderListDao {
	//폴더 등록
	public String addFolder(FolderListVO fl);
	//동일 이름 폴더 확인
	public int sameFolderChk(FolderListVO fl);
	//폴더 삭제
	public String deleteDir(String fname, String fpath);
	//폴더명 수정
	public String renameFolder(String fname, String fpath, String rename);
	//이름수정시 DB의 부모경로, 내경로 경로명 수정
	public String renameFolderPath(String fname, String fpath, String rename);
		

}
