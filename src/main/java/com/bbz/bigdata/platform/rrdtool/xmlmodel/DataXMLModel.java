package com.bbz.bigdata.platform.rrdtool.xmlmodel;

/**
 * 查询rrd数据的一条数据表示类
 * @author weiran
 *
 */
public class DataXMLModel {

	private String name;
	private String now;
	private String min;
	private String avg;
	private String max;
	private String[] data;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNow() {
		return now;
	}
	public void setNow(String now) {
		this.now = now;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getAvg() {
		return avg;
	}
	public void setAvg(String avg) {
		this.avg = avg;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String[] getData() {
		return data;
	}
	public void setData(String[] data) {
		this.data = data;
	}
	
	
}
