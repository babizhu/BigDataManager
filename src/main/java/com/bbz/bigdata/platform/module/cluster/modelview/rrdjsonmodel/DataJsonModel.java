package com.bbz.bigdata.platform.module.cluster.modelview.rrdjsonmodel;

import com.bbz.bigdata.platform.rrdtool.rrdmodel.DataModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DataJsonModel {

	public  DataJsonModel(){}

	public  DataJsonModel(DataModel dataModel){
		this.name=dataModel.getName();
		this.data=dataModel.getData();
		this.pointInterval=dataModel.getPointInterval()*1000;
		this.pointStart=dataModel.getPointStart();
	}

	/**
	 * 数据名称 （比如 Memory.Free）
	 */
	private String name;
	/**
	 * 间隔时间
	 */
	private int pointInterval;
	private long pointStart;
	private BigDecimal[] data;
	
	
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public int getPointInterval() {
//		return pointInterval;
//	}
//	public void setPointInterval(int pointInterval) {
//		this.pointInterval = pointInterval;
//	}
//	public long getPointStart() {
//		return pointStart;
//	}
//	public void setPointStart(long pointStart) {
//		this.pointStart = pointStart;
//	}
//	public Double[] getData() {
//		return data;
//	}
//	public void setData(Double[] data) {
//		this.data = data;
//	}
	
	
}
