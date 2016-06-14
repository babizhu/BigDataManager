package com.bbz.bigdata.platform.module.cluster.modelview.rrdjsonmodel;

import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class RRDJsonModel {

	public RRDJsonModel(){}

	public RRDJsonModel(RRDModel rrdModel){
		if(rrdModel==null){
			return;
		}
		this.yunit=rrdModel.getYunit()==null?null:rrdModel.getYunit().toString();
		this.total=rrdModel.getTotal();
		this.totalUnit=rrdModel.getTotalUnit()==null?null:rrdModel.getTotalUnit().toString();
		rrdModel.getList().forEach(rdm->{
			this.getList().add(new DataJsonModel(rdm));
		});
	}

	/**
	 * 数据显示单位
	 */
	private String yunit;
	/**
	 * 数据集合
	 */
	private List<DataJsonModel> list=new ArrayList<>();
	/**
	 * 总量
	 */
	private BigDecimal total;
	/**
	 * 总量单位
	 */
	private String totalUnit;
}
