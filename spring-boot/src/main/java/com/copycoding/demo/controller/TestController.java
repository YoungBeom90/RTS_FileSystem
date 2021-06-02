package com.copycoding.demo.controller;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.copycoding.demo.vo.UserInfoVO;

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
		FileList fl = new FileList();
		String isDir = "c:\\mind-one\\test";
		
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
		WriteFile wf = new FileList();
		List<Map<String, Object>> filePath = wf.showFilesInDir(isDir);
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
		System.out.println("여기");
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
		WriteFile wf = new FileList();
		
		String result = wf.fileModify(filePath, fileName);
		
		return result;
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
	
	
	@RequestMapping(value="/ajax/uploadFile.json", 
			method = RequestMethod.POST, 
			produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String addFile(
			@RequestParam (value="file", required=true) List<MultipartFile> list,
			@RequestParam (value="fdate", required=true) Long fdate,
			@RequestParam (value="parent", required=false) String parent)
			
	{
//		System.out.println("파일생성경로");
//		System.out.println(parent);
		FileListVO fl = new FileListVO();
		
		System.out.println("list크기 : "+list.size());
		for(int i =	0; i<list.size(); i++) {
			
//			System.out.println("fname : "+list.get(i).getOriginalFilename().substring(0,list.get(i).getOriginalFilename().lastIndexOf(".")));
//			System.out.println("fext : "+list.get(i).getOriginalFilename().substring(list.get(i).getOriginalFilename().lastIndexOf(".")));
//			System.out.println("ppath : " + parent.substring(0, parent.lastIndexOf("\\")));
//			System.out.println("fpath : "+parent);
			
			//파일이름
			fl.setFname(list.get(i).getOriginalFilename().substring(0,list.get(i).getOriginalFilename().lastIndexOf(".")));
			//파일확장자
			fl.setFext(list.get(i).getOriginalFilename().substring(list.get(i).getOriginalFilename().lastIndexOf(".")));
			//부모경로
			fl.setPpath(parent.substring(0, parent.lastIndexOf("\\")));
			//해당 파일 경로
			fl.setFpath(parent);
			//파일 크기
			fl.setFsize(Long.toString(list.get(i).getSize()));
			

			//업데이트 시간
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				date.setTime(fdate);
				Timestamp timestamp = new Timestamp(date.getTime());
				fl.setFdate(timestamp);
//				System.out.println(timestamp);
			}catch (Exception e) {
				e.printStackTrace();
			}//try~catch end
			System.out.println(fl.getFname());
			System.out.println(fl.getFpath());
			System.out.println(fl.getFsize());
			System.out.println(fl.getPpath());
			System.out.println(fl.getFext());
			System.out.println(fl.getFdate());
			fileListService.registFile(fl);
		}//for end

		WriteFile wf = new FileList();
		
		wf.fileUpload(list, parent);

		
//		System.out.println(result);
		
		
		return "파일 등록 완료"; 
	}
	
	
	@RequestMapping("/axios/deleteFile")
	@ResponseBody
	public String deleteFile(
			@RequestParam(value="parent", required=false) String parent,
			@RequestParam(value="fileName", required=false) String fileName) 
	{
		String filePath = parent+"/"+fileName;
//		System.out.println(parent);
		FileListVO fl = new FileListVO();
		String fname=null;
		
		WriteFile wf = new FileList();

		if(fileName.lastIndexOf(".")==-1)	{
			wf.fileDelete(filePath);
		}else {
			fname = fileName.substring(0, fileName.lastIndexOf("."));
			System.out.println(fname);
			System.out.println(parent);
			fileListService.removeFile(fname, parent.toLowerCase());
			wf.fileDelete(filePath);
		}
		
		return "삭제 완료";
	}
	
	/**
	 * 파일을 이동시키거나 복사 하기위해 다음경로를 모르기 때문에
	 * 임시경로에 잠시 저장후 이동시켜야한다?
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
		
		WriteFile wf = new FileList();
		File prevPath = new File(prevPathStr);
		String fn = prevPath.getName();
		File nextPath = new File(nextPathStr+ "\\"+"새 폴더"+"\\" +fn);
		
		System.out.println(prevPath);
		System.out.println(nextPath);
		String result=wf.fileCopy(prevPath, nextPath);
		System.out.println(result);
		
		wf.fileDelete(prevPathStr);
		
		return "이동 완료";
	}
	
	
	
	
	
	
	
	
}
