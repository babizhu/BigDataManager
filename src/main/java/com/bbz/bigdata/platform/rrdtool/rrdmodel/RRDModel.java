package com.bbz.bigdata.platform.rrdtool.rrdmodel;

import com.bbz.bigdata.platform.rrdtool.Unit;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class RRDModel {
	/**
	 * 数据显示单位
	 */
	private Unit yunit;
	/**
	 * 数据集合
	 */
	private List<DataModel> list=new ArrayList<>();
	/**
	 * 总量
	 */
	private BigDecimal total;
	/**
	 * 总量单位
	 */
	private Unit totalUnit;

	/////*********************//////

}
