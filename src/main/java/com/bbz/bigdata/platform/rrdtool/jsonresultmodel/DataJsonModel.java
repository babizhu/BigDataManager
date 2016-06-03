package com.bbz.bigdata.platform.rrdtool.jsonresultmodel;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DataJsonModel {

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
