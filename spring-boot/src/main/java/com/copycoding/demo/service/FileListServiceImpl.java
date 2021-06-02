package com.copycoding.demo.service;

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
			UUID two = UUID.randomUUID();
			String fid = one.toString();
			String pid = two.toString();
			fl.setFid(fid);
			fl.setPid(pid);
			String result = fileListDao.addFile(fl);
			return result;
		}//if~else end
	}
	
	@Override
	public String removeFile(String fname, String fpath) {
		
		System.out.println(fname);
		System.out.println(fpath);
		
		return fileListDao.deleteFile(fname, fpath);
	}
	


}
