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
		
		UUID one = UUID.randomUUID();
		UUID two = UUID.randomUUID();
		String fid = one.toString();
		String pid = two.toString();
		fl.setFid(fid);
		fl.setPid(pid);

		
		return fileListDao.addFile(fl);
	}
	


}
