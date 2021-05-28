package com.copycoding.demo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.copycoding.demo.service.UserInfoService;

public class FileList implements WriteFile {
	
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


	@Override
	public String fileUpload(List<MultipartFile> mf, String parent) {
		for(int i =0; i<mf.size(); i++) {
			System.out.println("----------컨트롤러 내부-------------");
			System.out.println(mf.get(i).getOriginalFilename());
		}
	
		for (MultipartFile multipartFile : mf) {
			
			System.out.println(multipartFile.getOriginalFilename());
			
			File fl = new File(parent, multipartFile.getOriginalFilename()); 
			
			try {
				
				if(fl.exists()) {
					//파일이 이미 존재한다면 기존파일을 등록하는 파일로 덮어 씌우기
						File fl2 = new File(parent, multipartFile.getOriginalFilename()); 
						
						try {
							multipartFile.transferTo(fl2);
							fl.getName();
							
						} catch (Exception e) {
							
							e.printStackTrace();
							
						}//reMultipartFile try~catch end
				}else {	
					
					multipartFile.transferTo(fl);
					
				}//multipartFile if~else end
				
					
			} catch (Exception e) {
				
				e.printStackTrace();
				
			}//multipartFile try~catch end
			
		}//forEach end
		
		return "파일 등록 완료";
	}


	@Override
	public String fileDelete(String filePath) {
		File fl = new File(filePath);
		
		fl.delete();
		
		return fl.getName()+"을 삭제하였습니다.";
	}


	@Override
	public String fileModify(String filePath, String fileName) {
		File folder = new File(filePath);
		String result = null;
		
		if(folder.exists()) {
			result =  "동일한 이름의 폴더가 존재합니다.";
		}
		folder.renameTo(new File(fileName));
		
		// 생성여부 확인
		if(!folder.exists()) {
			result =  "-1";
		}
		
		return result;
	}

	@Override
	public String moveFile(List<MultipartFile> mf, String parent, String dstParent) {
		for (MultipartFile multipartFile : mf) {
			
			//현재경로+전체 파일명
			Path srcPath = Paths.get(parent+multipartFile.getOriginalFilename());
			//이동할경로+전체 파일명
			Path dstPath = Paths.get(dstParent+multipartFile.getOriginalFilename());
	
			try {
				
				Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
	
			} catch (Exception e) {
			   e.printStackTrace();
			}
			
		}//forEach end
		
		return "파일이동완료";
	}

}
