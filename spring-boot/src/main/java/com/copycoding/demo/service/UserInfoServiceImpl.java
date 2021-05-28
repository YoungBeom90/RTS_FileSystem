package com.copycoding.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.copycoding.demo.dao.UserInfoDao;
import com.copycoding.demo.vo.UserInfoVO;

@Service
public class UserInfoServiceImpl implements UserInfoService {
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Override
	public List<UserInfoVO> selectUserInfo() throws Exception {
		return userInfoDao.selectUserInfo();
	}

	@Override
	public int newSequence() {
		int result = 0;
		try{
			userInfoDao.newSequence();
		} catch(NullPointerException e) {
			System.out.println("error : [" + e + "]");
		}
		return result;
	}
	
}
