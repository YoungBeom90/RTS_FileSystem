package com.rts.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.rts.vo.UserInfoVO;

@Mapper
public interface UserInfoDao {
	public List<UserInfoVO> selectUserInfo();
	
	public int newSequence();
}
