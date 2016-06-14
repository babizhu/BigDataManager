package com.bbz.bigdata.platform.rrdtool.xmlmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询rrd数据的结果类
 * @author weiran
 *
 */
public class FullXMLModel {
	
	private long start;
	private long end;
	private int step;
	private int rows;
	private int columns;
	
	private List<DataXMLModel> datas=new ArrayList<>();

	
	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public List<DataXMLModel> getDatas() {
		return datas;
	}

	public void setDatas(List<DataXMLModel> datas) {
		this.datas = datas;
	}

	
	
	
}
