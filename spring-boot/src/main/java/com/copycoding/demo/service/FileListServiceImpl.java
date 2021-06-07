package com.copycoding.demo.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.copycoding.demo.dao.FileListDao;
import com.copycoding.demo.vo.FileListVO;

@Service
public class FileListServiceImpl implements FileListService{

	@Autowired
	private FileListDao fileListDao;
	
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
	@Transactional
	public String removeFile(String fname, String fpath) {
		
		String ppath = fpath.substring(0, fpath.lastIndexOf("\\"));
		
		fileListDao.deleteFolderSizeUpdate(fname, ppath);
		
		return fileListDao.deleteFile(fname, fpath);
	}
	
	
	@Override
	public String removeDir(String fname, String fpath) {

		return fileListDao.deleteDir(fname, fpath);
	}
	
	@Override
	@Transactional
	public String renameFile(String fname, String fpath, String rename) {

		String result = fileListDao.renameFile(fname, fpath, rename);
		fileListDao.renameFolderPath(fname, fpath, rename);
		
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
