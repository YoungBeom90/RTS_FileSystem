package com.copycoding.demo.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
				fileListDao.updateFolderSizeUpdate(fl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "기존값";
		}else {
			UUID one = UUID.randomUUID();
//			UUID two = UUID.randomUUID();
			String fid = one.toString();
			String pid = " ";
			fl.setFid(fid);
			fl.setPid(pid);

			String result = fileListDao.addFile(fl);
			fileListDao.addFolderSizeUpdate(fl);
			return result;
		}//if~else end
	}
	
	@Override
	public String createFolder(FolderListVO fl) {
		
		System.out.println(fl.getFname());
		System.out.println(fl.getFpath());
		System.out.println(fl.getPpath());
		System.out.println(fl.getFsize());
		System.out.println(fl.getFdate());
		
		if(folderListDao.sameFolderChk(fl)!=0) {
			
			return "동일 폴더명이 존재";
		}else {
			
			UUID key = UUID.randomUUID();
			String fid = key.toString();
			fl.setFid(fid);
			System.out.println(fl.getFid());
			
			String result = folderListDao.addFolder(fl);
			return result;
		}//if~else end
		
	}
	
	
	@Override
	@Transactional
	public String removeFile(String fname, String fpath) {
		
		String ppath = fpath.substring(0, fpath.lastIndexOf("\\"));
		
		fileListDao.deleteFolderSizeUpdate(fname, ppath);
		
		return fileListDao.deleteFile(fname, fpath);
	}
	
	
	@Override
	public String removeDir(String fname, String fpath) {

		return folderListDao.deleteDir(fname, fpath);
	}
	
	@Override
	@Transactional
	public String renameFile(String fname, String fpath, String rename) {

		String result = folderListDao.renameFolder(fname, fpath, rename);
		System.out.println(rename);
		System.out.println(fname);
		System.out.println(fpath);
		folderListDao.renameFolderPath(fname, fpath, rename);
		
		return result;
	}
	
	@Override
	public int moveFile(String prevPath, String nextPath, String fname) {

		return fileListDao.moveFile(prevPath, nextPath, fname);
	}
	
	@Override
	public List<FileListVO> selectFileList(String ppath) {
		
		return fileListDao.selectFileList(ppath);
	}
	
	@Override
	public List<FileListVO> showFolderTree(String fpath) {

		return fileListDao.showFolderTree(fpath);
	}


}
