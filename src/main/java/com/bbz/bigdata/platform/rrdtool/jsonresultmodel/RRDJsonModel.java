package com.bbz.bigdata.platform.rrdtool.jsonresultmodel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RRDJsonModel {
	/**
	 * 数据显示单位
	 */
	private String yunit;
	/**
	 * 数据集合
	 */
	private List<DataJsonModel> list=new ArrayList<>();

}
