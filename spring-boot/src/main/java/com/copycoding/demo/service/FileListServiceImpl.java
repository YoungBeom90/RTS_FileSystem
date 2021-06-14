package com.copycoding.demo.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	public void setFileListDao(FileListDao fileListDao) {
		this.fileListDao = fileListDao;
	}
	
	@Override
	@Transactional
	public String registFile(FileListVO fl) {
		
		//분기걸어서 fullPath확인후 일치하면 prevent
		//select해서 존재하면 return -1
		if(fileListDao.sameFileChk(fl)!=0) {
			try {
				fileListDao.updateFile(fl);
			} catch (Exception e) {
				e.printStackTrace();
			}//try~catch end
			return "기존값";
		}else {
			
			UUID one = UUID.randomUUID();
			String fid = one.toString();
			String pid = folderListDao.getPpath(fl.getPpath());
			fl.setFid(fid);
			fl.setPid(pid);

			String result = fileListDao.addFile(fl);
			folderListDao.folderSizeUpdate(pid);
			return result;
		}//if~else end
	}
	
	@Override
	@Transactional
	public String createFolder(FolderListVO fl) {
		
		if(folderListDao.sameFolderChk(fl)!=0) {
			
			return "동일 폴더명이 존재";
		}else {
			
			UUID key = UUID.randomUUID();
			String fid = key.toString();
			fl.setFid(fid);
			String result = folderListDao.addFolder(fl);
			return result;
		}//if~else end
		
	}
	
	
	@Override
	@Transactional
	public String removeFile(String fname, String fpath) {
		
		
		String result = fileListDao.deleteFile(fname, fpath);
//		String ppath = fpath.substring(0, fpath.lastIndexOf("\\\\"));
//		String pid = folderListDao.getPpath(ppath);
//		folderListDao.folderSizeUpdate(pid);
		return result;
	}
	
	
	@Override
	public String removeDir(String fname, String fpath) {
		
		return folderListDao.deleteDir(fname, fpath+"\\\\"+fname);
	}
	
	@Override
	@Transactional
	public String renameFile(String fname, String fpath, String rename, String parent) {

		String result = folderListDao.renameFolder(fname, fpath, rename);
		folderListDao.renameFolderPath(fname, parent, rename);
		
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
		String fname = fpath.substring(fpath.lastIndexOf("\\")+1,fpath.length());
		System.out.println("dyrldyrl : "+fname);
		List<FolderListVO> list = folderListDao.selectFolderList(fpath);
		
		return list;
	}
	
	
	@Override
	@Transactional
	public int moveFile(String prevPath, String nextPath) {

		//file이동하기 위한 interface 오버로딩
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
	public List<FolderListVO> showFolderTree(String fpath) {
		
		System.out.println("폴더경로 : "+fpath);
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

}
