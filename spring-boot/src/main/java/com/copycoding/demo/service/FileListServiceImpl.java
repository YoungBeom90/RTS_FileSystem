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
	public int registFile(FileListVO fl) {
		
//분기걸어서 fullPath확인후 일치하면 prevent
//select해서 존재하면 return
//		if()
		
		return fileListDao.addFile(fl);
	}
	


}
