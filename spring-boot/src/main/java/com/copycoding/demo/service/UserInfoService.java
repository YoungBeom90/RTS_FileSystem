package com.copycoding.demo.service;

import java.util.List;

import com.copycoding.demo.vo.UserInfoVO;

public interface UserInfoService {
	public List<UserInfoVO> selectUserInfo() throws Exception;
	
	public int newSequence();
}
