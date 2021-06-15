package com.copycoding.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.copycoding.demo.common.FileList;
import com.copycoding.demo.common.WriteFile;
import com.copycoding.demo.service.FileListService;
import com.copycoding.demo.service.UserInfoService;
import com.copycoding.demo.vo.FileListVO;
import com.copycoding.demo.vo.FolderListVO;
import com.copycoding.demo.vo.UserInfoVO;

import lombok.val;

@Component
@Controller
public class TestController { 
	
	@Autowired
	private UserInfoService userInfoService;
	private FileListService fileListService;
	
	@Autowired
	public void setRtsService(FileListService fileListService) {
		this.fileListService = fileListService;
	};
	
	@RequestMapping("/user")
	@ResponseBody
	public ModelAndView selectUserInfo() throws Exception {
		ModelAndView mv = new ModelAndView();
		List<UserInfoVO> list = userInfoService.selectUserInfo();
		mv.addObject("list", list);
		mv.setViewName("/users/userInfo");
		return mv;
	}
	
	@RequestMapping("/fileList")
	public String goFileList() {
		return "/users/fileList";
	}
	
	/**
	 * jsTree 폴더 리스트 가져오기
	 * @return
	 */
	@RequestMapping("/axios/showFolderTree")
	public ModelAndView showFolderTree() {
		ModelAndView mv = new ModelAndView("jsonView");
		String isDir = "z:";
		List<FolderListVO> folderList  = fileListService.showFolderTree(isDir);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		for (FolderListVO folderListVO : folderList) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("id", folderListVO.getFpath());
			map.put("parent", folderListVO.getPpath());
			map.put("text", folderListVO.getFname());
			map.put("path", folderListVO.getFpath().substring(3));
			list.add(map);
		}
		
		mv.addObject("folderList", list);
		return mv;
	}
	
	/**
	 * 선택된 폴더에 대한 자식요소 리스트 불러오기
	 * @param isDir
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping("/ajax/selectFileList")
	public ModelAndView selectFileList(@RequestParam(value="isDir", required=true) String isDir) throws Exception {
//		System.out.println("isDir: " + isDir);
		ModelAndView mv = new ModelAndView("jsonView");
		//DB에 저장된 값
		List<FileListVO> fp = fileListService.selectFileList(isDir);
		List<FolderListVO> fo = fileListService.selectFolderList(isDir);
		List<Object> all = new ArrayList<Object>();
		all.addAll(fo);
		all.addAll(fp);
		mv.addObject("filePath", all);
		return mv;
	}
	
	/**
	 * 폴더 생성
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/axios/createFolder")
	@ResponseBody
	public String createFolder(@RequestParam(value="value", required=true) String value, String path) throws Exception {
		
		String result = fileListService.createFolder(value, path);	

		return result;
	}
	
	/**
	 * 폴더명 변경
	 * @param value 현재 파일이름
	 * @param path 현재 파일경로
	 * @param rename 바꿀이름
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/axios/renameFolder")
	@ResponseBody
	public String renameFolder(@RequestParam(value="value", required=true) String value, String path, String rename) throws Exception{
		
			fileListService.renameFile(value, path, rename);
		
		return "이름 수정";
	}
	
	/**
	 * 
	 * @param sfp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/axios/selectFolderChildList")
	public ModelAndView selectFolderChildList(@RequestParam(value="sfp", required=false) String sfp) throws Exception {
		ModelAndView mv = new ModelAndView("jsonView");
		List<Map<String, Object>> folderList = null;
		FileList fl = new FileList();
		
		if(sfp != null) {
			String isDir = sfp;
			folderList = fl.showFilesInDir(isDir);
		}
		
//		System.out.println(folderList);
		mv.addObject("folderList", folderList);
		
		return mv;
	}
	
	
	/**
	 * 파일 업로드
	 * @param list 업로드하는 파일목록
	 * @param fdate 파일날짜
	 * @param parent 부모경로
	 * @return
	 */
	@RequestMapping(value="/ajax/uploadFile.json", 
			method = RequestMethod.POST, 
			produces = "application/json; charset=UTF-8")
	@ResponseBody
	public void addFile(
			@RequestParam (value="file", required=true) List<MultipartFile> list,
			@RequestParam (value="fdate", required=true) Long fdate,
			@RequestParam (value="parent", required=true) String parent) {
		
		fileListService.registFile(list, fdate, parent);

	}
	
	/**
	 * 파일 및 폴더 삭제
	 * @param parent
	 * @param fileName
	 * @return
	 */
	@RequestMapping("/axios/deleteFile")
	@ResponseBody
	public String deleteFile(
			@RequestParam(value="parent", required=false) String fpath,
			@RequestParam(value="fileName", required=false) String fname,
			@RequestParam(value="fileExt", required=false) String fileExt)
	{
		if(fileExt.equals("폴더"))	{
			fileListService.removeDir(fname, fpath);
		}else {
			fileListService.removeFile(fname, fpath, fileExt);
		}//if~else end
		
	return "삭제 완료";
	}
	
	/**
	 * 파일 이동
	 * 
	 * @param fileList
	 * @param prevPathStr
	 * @param nextPathStr
	 * @return
	 */
	@RequestMapping("/ajax/moveFile")
	@ResponseBody
	public String moveFile(
			@RequestParam(value="allFilePath", required = true) String prevPathStr,
			@RequestParam(value="selectParentPath")String nextPathStr) {
		
		fileListService.moveFile(prevPathStr, nextPathStr);
		
		return "파일 이동";
	}
	
	/**
	 * 파일 다운로드
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/axios/downloadFile")
	@ResponseBody
	public String downloadFile(HttpServletResponse response,
			@RequestParam(value = "fileName") String fname, 
			@RequestParam(value = "fileExt") String fext,
			@RequestParam(value = "parent") String fpath) throws Exception {
		
		System.out.println(fname);
		System.out.println(fext);
		System.out.println(fpath);
		if(!fext.equals("폴더")) {
			WriteFile wf = new FileList();
			return wf.donwloadFile(response, fname+fext, fpath);
		}else {
			return "폴더는 다운받을 수 없습니다.";
		}
	}
}
