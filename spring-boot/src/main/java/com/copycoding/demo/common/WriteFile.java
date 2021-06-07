package com.copycoding.demo.common;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


public interface WriteFile {

	//파일 정보 불러오기
	public List<Map<String, Object>> showFilesInDir(String isDir);
	
	//파일 업로드
	public String fileUpload(List<MultipartFile> mf, String parent);
	
	//파일 삭제
	public String fileDelete(String filePath);
	
	//파일명 수정
	public String fileModify(String filePath, String reFileName);
	
	//파일 이동
	public String moveFile(List<MultipartFile> mf, String parent, String dstParent);
	
	//파일 복사
	public String fileCopy(File prevFile, File nextFile);
	
	//파일 다운
	public String donwloadFile(String filePath);
	
}
