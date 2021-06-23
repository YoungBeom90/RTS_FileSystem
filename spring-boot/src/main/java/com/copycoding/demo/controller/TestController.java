package com.copycoding.demo.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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

@Component
@Controller
public class TestController { 
	
	@Autowired
	private UserInfoService userInfoService;
	private FileListService fileListService;
	
	@Value("${file.upload.directory}")
	public String filePath;
	
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
		String isDir = filePath;
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
			@RequestParam(value="fileExt", required=false) String fileExt
)
	{
		if(fileExt.equals("폴더"))	{
			fileListService.removeDir(fname, fpath);
			//해당경로에 있는 파일도 삭제
			
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
	@RequestMapping(value = "/axios/downloadFile")
	@ResponseBody
	public void downloadFile(HttpServletResponse response,
			@RequestParam(value = "fileName") String[] fname, 
			@RequestParam(value = "parent") String fpath) throws IOException {
		System.out.println("다운로드로직시작");
		URLDecoder.decode(fpath, "utf-8");
		System.out.println(fpath);
		System.out.println(fname.length);
		for (String string : fname) {
			System.out.println("fname is :" + string);
			System.out.println("파일명 : "+ URLDecoder.decode(string,"utf-8"));
		}

			WriteFile wf = new FileList();
			Thread thread1 = new Thread(wf.donwloadFile(response, fname, fpath));
			//Thread thread2 = new Thread(wf.fileDelete(fpath+"\\\\"+fname[0].substring(0,fname[0].lastIndexOf("."))+".zip"));

			try {
				thread1.start();
				thread1.join();
				
				//thread2.start();
				//thread2.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
				
	}//downloadFile end
	
	@RequestMapping("/ajax/deleteZip")
	@ResponseBody
	public void deleteZip(@RequestParam(value = "fileName") String[] fname, 
		@RequestParam(value = "parent") String fpath) throws IOException {
		System.out.println("zip삭제 : "+URLDecoder.decode(fname[0].substring(0,fname[0].lastIndexOf(".")),"utf-8"));
		System.out.println("zip삭제 : "+fpath);
		WriteFile wf = new FileList();
		wf.fileDelete(URLDecoder.decode(fpath, "utf-8")+"\\\\"+URLDecoder.decode(fname[0].substring(0,fname[0].lastIndexOf(".")),"utf-8")+".zip");
	
	}
	
	@RequestMapping("/ajax/searchFile")
	@ResponseBody
	public List<FileListVO> searchFile(
			@RequestParam(value ="fileName") String fileName,
			@RequestParam(value ="fpath") String fpath) {
		
		List<FileListVO> list = new ArrayList<FileListVO>();
		list = fileListService.searchFileList(fileName, fpath);
		return list;
	}
	
	@RequestMapping(value="/ajax/searchSubmit", method=RequestMethod.POST)
	@ResponseBody
	public List<FileListVO> searchSubmit(@RequestBody String fileName){
		List<FileListVO> list = new ArrayList<FileListVO>();
		System.out.println("fileName:" + fileName);
		return list;
	}
	
	
	
}
