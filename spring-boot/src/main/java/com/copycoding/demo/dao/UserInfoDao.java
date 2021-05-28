package com.copycoding.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.copycoding.demo.vo.UserInfoVO;

@Mapper
public interface UserInfoDao {
	public List<UserInfoVO> selectUserInfo();
	
	public int newSequence();
}
