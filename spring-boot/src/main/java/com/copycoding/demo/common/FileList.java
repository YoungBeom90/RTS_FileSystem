package com.copycoding.demo.common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.copycoding.demo.service.UserInfoService;

public class FileList  {
	
	String initParentPath = null;
	int folderIdx = 0;

	String globalParentPath= null;
	String isNm = null;
	StringBuffer sb = new StringBuffer();
	List<Map<String, Object>> fileTree = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> folderTree = new ArrayList<Map<String, Object>>();
	
	public List<Map<String, Object>> showFolderTree(String isDir) {
		
		if(folderIdx==0) {
			isNm = isDir;
		}
		
		File folderList = new File(isDir);
		File[] findedList = folderList.listFiles();
		for(File finded : findedList) {
			
			Map<String, Object> folder = new LinkedHashMap<String, Object>();
			
			if(finded.isDirectory()) {
				
				// 폴더 이름
				String folderNm = finded.getName();
				
				// 폴더 주소
				String folderPath = null;
				folderPath = finded.getAbsolutePath();
				
				//폴더 ID
				String folderId = "id_" + folderPath;
				
				// 부모파일 주소
				File parentFile = finded.getParentFile();
				String parentFileNm = parentFile.getAbsolutePath();
				String parentNm = null;
				if(isNm.equals(parentFileNm)) {
					parentNm = "#";
				} else {
					parentNm = "id_" + parentFileNm;
				}
				
				/********* 파일정보 List 저장 시작 ***********/
				folder.put("id", folderId);
				folder.put("parent", parentNm);
				folder.put("text", folderNm); // 파일명
				// 폴더 선택 옵션
				if(parentNm.equals("#") && folderIdx==0) {
					Map<String, Boolean> treeState = new HashMap<String, Boolean>();
					treeState.put("selected", true);
					folder.put("state", treeState);
				}
				folder.put("path", folderPath); // 폴더 주소
				
				folderTree.add(folder);
				folderIdx++;
				
				/********* 파일정보 저장 종료 ***********/
				//현재 파일이 폴더면 재귀
				if(finded.isDirectory()) {
					showFolderTree(finded.getAbsolutePath());
				} 
				
			}
			
		}
		
		
		
		return folderTree;
	}
	
	
	public List<Map<String, Object>> showFilesInDir(String isDir) {
		System.out.println("showFilesInDir 호출");
		int listIndex = 0;
		
		for(File finded : new File(isDir).listFiles()) {
			if(listIndex == 0) {
				initParentPath = isDir;
			}
			Map<String, Object> file = new LinkedHashMap<String, Object>();
			
			// 파일이름
			String fileName = finded.getName();
			
			// 확장자 & type
			String fileExt = "";
			String fileType = "";
			if(finded.isFile()) {
				int index = fileName.lastIndexOf(".");
				fileExt = fileName.substring(index+1, fileName.length());
				fileType = "file";
			} else if(finded.isDirectory()) {
				fileExt = "폴더";
				fileType = "folder";
			}
			
			// 최종 수정날짜
			long lastModifyDate = finded.lastModified();
			String pattern = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date date = new Date(lastModifyDate);
			String lastMdfDate = sdf.format(date);
			
			// 파일 사이즈
			long fileSize = finded.length();
			String size = Long.toString(fileSize);
			
			// 부모 폴더명
			File parentFile = finded.getParentFile();
			String parentNm = parentFile.getName();
			
			//현재 파일 경로
			String findedFilePath = "";
			try {
				findedFilePath = finded.getCanonicalPath();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			file.put("id", fileName + lastMdfDate);
			file.put("parent", parentNm); // 부모폴더이름
			file.put("text", fileName); // 파일명
			file.put("ext", fileExt); // 확장자
			file.put("size", size); // 용량
			file.put("date", lastMdfDate); // 수정날짜
			file.put("url", findedFilePath); // url
			file.put("type", fileType); // jsTree 파일타입
		
			fileTree.add(file);
			listIndex++;
		}
		return fileTree; 
	}

}
