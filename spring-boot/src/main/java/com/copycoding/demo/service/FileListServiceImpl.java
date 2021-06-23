package com.copycoding.demo.service;

import java.io.File;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.copycoding.demo.common.FileList;
import com.copycoding.demo.common.WriteFile;
import com.copycoding.demo.dao.FileListDao;
import com.copycoding.demo.dao.FolderListDao;
import com.copycoding.demo.vo.FileListVO;
import com.copycoding.demo.vo.FolderListVO;

@Service
public class FileListServiceImpl implements FileListService{

	@Autowired
	private FileListDao fileListDao;
	@Autowired
	private FolderListDao folderListDao;
	
	@Value("${file.upload.directory}")
	public String filePath;
	
	public void setFileListDao(FileListDao fileListDao) {
		this.fileListDao = fileListDao;
	}
	
	/**
	 * 다중파일 list 등록 및 DB등록 
	 */
	@Override
	@Transactional
	public String registFile(List<MultipartFile> list, Long fdate, String parent) {
		
		FileListVO fl = new FileListVO();
		for (int i = 0; i < list.size(); i++) {
			
			// 파일이름
			fl.setFname(list.get(i).getOriginalFilename().substring(0, list.get(i).getOriginalFilename().lastIndexOf(".")));
			// 파일확장자
			fl.setFext(list.get(i).getOriginalFilename().substring(list.get(i).getOriginalFilename().lastIndexOf(".")));
			// 부모경로
			fl.setPpath(parent.substring(0, parent.lastIndexOf("\\\\")));
			// 해당 파일 경로
			fl.setFpath(parent);
			// 파일 크기
			fl.setFsize(Long.toString(list.get(i).getSize()));
			
			// 업데이트 시간
			try {
				Date date = new Date();
				date.setTime(fdate);
				Timestamp timestamp = new Timestamp(date.getTime());
				fl.setFdate(timestamp);
			} catch (Exception e) {
				e.printStackTrace();
			} // try~catch end
			
			// 분기걸어서 fullPath확인후 일치하면 prevent
			// select해서 존재하면 return -1
			if (fileListDao.sameFileChk(fl) != 0) {
				try {
					fileListDao.updateFile(fl);
				} catch (Exception e) {
					e.printStackTrace();
				} // try~catch end
			} else {

				UUID one = UUID.randomUUID();
				String fid = one.toString();
				String pid =null;
				if(fl.getPpath().equals(filePath)) {
					pid = folderListDao.getPpath(fl.getFpath());
				}else {
					pid = folderListDao.getPpath(fl.getPpath());
				}
				fl.setFid(fid);
				fl.setPid(pid);

				String result = fileListDao.addFile(fl);
			} // if~else end
			
		} // for end
		
		//경로에 파일생성
		WriteFile wf = new FileList();
		wf.fileUpload(list, parent);
		
		return "";
	}
	
	/**
	 * 폴더생성 및 folderlist table 등록
	 */
	@Override
	@Transactional
	public String createFolder(String value, String path) {
		
		String filePath = path+"\\\\"+value;
		String fileName = value;
		
		File folder = new File(filePath);
		
		FolderListVO fl = new FolderListVO();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
		fl.setFname(fileName);
		fl.setFdate(timestamp);
		fl.setFpath(filePath);
		fl.setPpath(filePath.substring(0,filePath.lastIndexOf("\\\\")));
		fl.setFsize(0);
		
		if(folder.exists()) {
			return "0";
		}
		folder.mkdir();
		// 생성여부 확인
		if(!folder.exists()) {
			return "-1";
		}
		
		if(folderListDao.sameFolderChk(fl)==0) {
			
			UUID key = UUID.randomUUID();
			String fid = key.toString();
			fl.setFid(fid);
			folderListDao.addFolder(fl);
		}//if~else end
		
		return "1";
	}
	
	/**
	 * 경로에 파일삭제 및 해당 DB삭제
	 */
	@Override
	@Transactional
	public String removeFile(String fname, String fpath, String fileExt) {
		
		String filePath = fpath+"\\\\"+fname+fileExt;
		System.out.println(filePath);
		WriteFile wf = new FileList();
		wf.fileDelete(filePath);
		String result = fileListDao.deleteFile(fname, fpath);

		return result;
	}
	
	/**
	 * 경로에 폴더삭제 및 해당 DB삭제
	 */
	@Override
	@Transactional
	public String removeDir(String fname, String fpath) {

		String filePath = fpath+"\\\\"+fname;
		WriteFile wf = new FileList();
		wf.fileDelete(filePath);
		folderListDao.deleteDirsFile(fpath);
		String result = folderListDao.deleteDir(fname, fpath + "\\\\" + fname);
		return result;
	}
	
	/**
	 * 폴더명 변경
	 */
	@Override
	@Transactional
	public String renameFile(String fname, String fpath, String rename) {
		String parent;
		WriteFile wf = new FileList();
		if(fpath.lastIndexOf("\\\\")==-1) {
			parent = fpath;
		}else {
			parent = fpath.substring(0, fpath.lastIndexOf("\\\\"));
		}
		String result = wf.fileModify(fpath, rename);
		if(result.equals("-1")) {
			folderListDao.renameFolder(fname, fpath, rename);
			folderListDao.renameFolderPath(fname, parent, rename);
			fileListDao.renameFolderPath(fname, parent, rename);
		}
		return result;
	}
	
	@Override
	public List<FileListVO> selectFileList(String ppath) {
		List<FileListVO> list =  fileListDao.selectFileList(ppath);

		return list;
	}
	
	@Override
	public List<FolderListVO> selectFolderList(String fpath){
		if(fpath.lastIndexOf("\\\\")==-1) {
			fpath = "#";
		}
		fpath.substring(fpath.lastIndexOf("\\")+1,fpath.length());
		List<FolderListVO> list = folderListDao.selectFolderList(fpath);
		
		return list;
	}
	
	@Override
	public List<FolderListVO> showFolderTree(String fpath) {
		
		List<FolderListVO> list = folderListDao.showFolderTree(fpath);
		
		for (FolderListVO folderListVO : list) {

			if(folderListVO.getPpath().equals("#")) {
				folderListVO.setFpath("id_"+folderListVO.getFpath());
			}else {
				folderListVO.setFpath("id_"+folderListVO.getFpath());
				folderListVO.setPpath("id_"+folderListVO.getPpath());
			}//if~else
			
		}//for each end
		
		return list;
	}
	
	@Override
	@Transactional
	public int moveFile(String prevPath, String nextPath) {

		//file이동하기 위한 interface 오버로딩
		//파일 이동시 복사 or 이동 클릭시 해당 경로 및 파일명  param가져오기
		WriteFile wf = new FileList();
		File prevPathFile = new File(prevPath);
		String fileName = prevPathFile.getName();
		File nextPathFile = new File(nextPath+fileName);
		wf.fileCopy(prevPathFile, nextPathFile);
		wf.fileDelete(prevPath);
		
		//DB에 파일 이동시 경로 변경
		return fileListDao.moveFile(prevPath, nextPath, fileName);
	}
	
	@Override
	public List<FileListVO> searchFileList(String fileName, String fpath) {
		
		return fileListDao.searchFile(fileName, fpath);
	}
	
	@Override
	public List<FolderListVO> searchFolderList(String param) {

		return folderListDao.searchFolder(param);
	}
	
}
