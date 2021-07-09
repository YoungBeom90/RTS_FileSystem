package com.rts.service;

import java.util.List;

import com.rts.vo.UserInfoVO;

public interface UserInfoService {
	public List<UserInfoVO> selectUserInfo() throws Exception;
	
	public int newSequence();
}
