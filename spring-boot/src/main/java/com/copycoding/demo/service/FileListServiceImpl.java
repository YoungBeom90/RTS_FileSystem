package com.copycoding.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public String registFile(FileListVO fl) {
		
		//분기걸어서 fullPath확인후 일치하면 prevent
		//select해서 존재하면 return -1
		if(fileListDao.sameFileChk(fl)!=0) {
			fileListDao.updateFile(fl);
			return "기존값";
		}else {
			UUID one = UUID.randomUUID();
//			UUID two = UUID.randomUUID();
			String fid = one.toString();
			String pid = " ";
			fl.setFid(fid);
			fl.setPid(pid);
			String result = fileListDao.addFile(fl);
			return result;
		}//if~else end
	}
	
	@Override
	public String removeFile(String fname, String fpath) {
		
		
		return fileListDao.deleteFile(fname, fpath);
	}
	
	
	@Override
	public String removeDir(String fname, String fpath) {

		return fileListDao.deleteDir(fname, fpath);
	}
	
	@Override
	public String renameFile(String fname, String fpath, String rename) {

		return fileListDao.renameFile(fname, fpath, rename);
	}
	
	@Override
	public String selectFileList(String ppath) {

		return fileListDao.selectFileList(ppath);
	}
	
	@Override
	public List<FileListVO> showFolderTree(String fpath) {

		return fileListDao.showFolderTree(fpath);
	}


}
