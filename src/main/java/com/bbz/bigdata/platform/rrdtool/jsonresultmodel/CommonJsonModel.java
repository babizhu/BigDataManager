package com.bbz.bigdata.platform.rrdtool.jsonresultmodel;

import java.util.ArrayList;
import java.util.List;

public class CommonJsonModel {
	/**
	 * 数据显示单位
	 */
	private String yunit;
	/**
	 * 数据集合
	 */
	private List<DataJsonModel> list=new ArrayList<>();

	public String getYunit() {
		return yunit;
	}

	public void setYunit(String yUnit) {
		this.yunit = yUnit;
	}

	public List<DataJsonModel> getList() {
		return list;
	}

	public void setList(List<DataJsonModel> list) {
		this.list = list;
	}

	
}
