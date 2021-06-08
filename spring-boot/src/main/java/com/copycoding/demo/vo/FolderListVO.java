package com.copycoding.demo.vo;

import java.sql.Timestamp;

public class FolderListVO {

	private String fid, fname, fpath, ppath;
	private int fsize;
	private Timestamp fdate;
	
	public FolderListVO() {
		// TODO Auto-generated constructor stub
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getFpath() {
		return fpath;
	}

	public void setFpath(String fpath) {
		this.fpath = fpath;
	}

	public String getPpath() {
		return ppath;
	}

	public void setPpath(String ppath) {
		this.ppath = ppath;
	}

	public int getFsize() {
		return fsize;
	}

	public void setFsize(int fsize) {
		this.fsize = fsize;
	}

	public Timestamp getFdate() {
		return fdate;
	}

	public void setFdate(Timestamp fdate) {
		this.fdate = fdate;
	}
	
	
}
