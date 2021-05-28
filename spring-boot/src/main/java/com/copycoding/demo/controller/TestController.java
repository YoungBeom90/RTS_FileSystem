package com.copycoding.demo.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.copycoding.demo.common.FileList;
import com.copycoding.demo.service.UserInfoService;
import com.copycoding.demo.vo.UserInfoVO;

@Controller
public class TestController {
	
	@Autowired
	private UserInfoService userInfoService;
	
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
		FileList fl = new FileList();
		String isDir = "c:\\testfile";
		
		List<Map<String, Object>> folderList = fl.showFolderTree(isDir);
		mv.addObject("folderList", folderList);
		System.out.println("folderList : " + mv);
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
		System.out.println("isDir: " + isDir);
		ModelAndView mv = new ModelAndView("jsonView");
		FileList fl = new FileList();
		List<Map<String, Object>> filePath = fl.showFilesInDir(isDir);
		System.out.println(filePath);
		mv.addObject("filePath", filePath);
//		mv.setViewName("/users/fileList");
		
		return mv;
	}
	
	/**
	 * 폴더 생성(테스트중)
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/axios/createFolder")
	@ResponseBody
	public String createFolder(@RequestParam(value="value", required=true) String value, String path) throws Exception {
		String filePath = path;
		String fileName = value;
		System.out.println(filePath + "\\" + fileName);
		File folder = new File(filePath + "\\" + fileName);
		
		if(folder.exists()) {
			return "동일한 이름의 폴더가 존재합니다.";
		}
		folder.mkdir();
		
		// 생성여부 확인
		if(!folder.exists()) {
			return "-1";
		}
				
		return "폴더 생성 완료.";
	}
	
	@RequestMapping("/axios/renameFolder")
	@ResponseBody
	public String renameFolder(@RequestParam(value="value", required=true) String value, String path) throws Exception{
		String filePath = path;
		String fileName = value;
		System.out.println(filePath + "\\" + fileName);
		File folder = new File(filePath + "\\" + fileName);
		
		if(folder.exists()) {
			return "동일한 이름의 폴더가 존재합니다.";
		}
		folder.renameTo(new File(fileName));
		
		// 생성여부 확인
		if(!folder.exists()) {
			return "-1";
		}
		
		return "이름 수정 완료";
	}
	
	/**
	 * 
	 * @param sfp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/axios/selectFolderChildList")
	public ModelAndView selectFolderChildList(@RequestParam(value="sfp", required=false) String sfp) throws Exception {
		System.out.println("/axios/selectFolderChildList 호출");
		System.out.println(sfp);
		ModelAndView mv = new ModelAndView("jsonView");
		List<Map<String, Object>> folderList = null;
		FileList fl = new FileList();
		
		if(sfp != null) {
			String isDir = sfp;
			folderList = fl.showFilesInDir(isDir);
		}
		
		System.out.println(folderList);
		mv.addObject("folderList", folderList);
		
		return mv;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
