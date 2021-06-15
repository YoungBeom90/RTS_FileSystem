package com.copycoding.demo.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

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
		System.out.println(isDir);
		for(File finded : new File(isDir).listFiles()) {
			
			System.out.println(finded);
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

	
	/**
	 * 파일삭제, 폴더일경우 내부의 파일들을 삭제하고
	 * 해당 폴더를 삭제, 파일일 경우 바로삭제
	 * @return fileName
	 * @param filePath
	 */
	@Override
	public String fileDelete(String filePath) {

		File fl = new File(filePath);
		
		try {
			//파일이 존재하는동안 계속 삭제
			while(fl.exists()) {
				//삭제전 파일인지 폴더인지 구분
				if(fl.isDirectory()) {
					//폴더라면 배열로 생성
					File[] flList = fl.listFiles();
					//폴더인데 내부에 파일이 없을 경우
					if(fl.isDirectory()&&flList.length==0) {
						fl.delete();
					//폴더안에 파일이 존재할 경우
					}else {
						//리스트 내부의 파일들 삭제
						for (File file : flList) {
							file.delete();
							if(file.isDirectory())
								//하위에 폴더가 존재할경우 재귀
								fileDelete(file.getAbsolutePath());
						}
						//폴더삭제
						fl.delete();
					}//if~else end
				}else {
					fl.delete();
				}//if~else
			}//while end
		}catch (Exception e) {
			e.printStackTrace();
		}//try~catch end
		
		return fl.getName()+"을 삭제하였습니다.";
	}


	@Override
	public String fileModify(String filePath, String fileName) {
		File folder = new File(filePath);
		String result = null;
		String parent = filePath.substring(0, filePath.lastIndexOf("\\"));
		// 생성여부 확인
		if(folder.exists()) {
			folder.renameTo(new File(parent+"\\"+fileName));
			result =  "-1";
		}else 
			result =  "동일한 이름의 폴더가 존재합니다.";
		
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
	
	@Override
	public String fileCopy(File prevFile, File nextFile) {
		
		File[] fileList = prevFile.listFiles();
		for (File file : fileList) {
			
			File temp = new File(nextFile.getAbsolutePath()+File.separator+file.getName());
			if(file.isDirectory()) {
				temp.mkdir();
				fileCopy(file,temp);
			}else {
				FileInputStream fis=null; 
				FileOutputStream fos=null;
				
				try {
					fis = new FileInputStream(file);
					fos = new FileOutputStream(temp);
					byte[] b = new byte[4096];
					int cnt = 0;
					
					while((cnt=fis.read(b)) != -1){
						fos.write(b, 0, cnt);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						fis.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}//try~catch end
					
				}//try~catch~finally end
				
			}//if~else end
			
		}//for end
		
		return "복사 완료";
	}

	@Override
	public String donwloadFile(HttpServletResponse response, String fname, String fpath) throws Exception {
		
		File downloadFile = new File(fpath+"\\\\"+fname);
		response.setContentLength((int)downloadFile.length());
				
		response.setContentType("application/donwload; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ new String(fname.getBytes(), "iso-8859-1"));
		response.setHeader("Content-Transfer-Encoding","binary");
		try {
			ServletOutputStream sos=response.getOutputStream();
			FileInputStream fis = new FileInputStream(downloadFile);
			
			int count=-1;
			byte[] bytes = new byte[1024];
			
			while((count=fis.read(bytes,0,bytes.length))!=-1) {
				sos.write(bytes,0,count);
			}//while end
			//fis.close();
			//sos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "다운로드 완료";
	}
	
}
