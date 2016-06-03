package com.bbz.bigdata.platform.rrdtool.cmd;

import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.DataJsonModel;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface ICmd {
	/**
	 * 获取cmd字符串
	 */
	String getCmd();

	Measurement measurement();
	/**
	 * 实现如何把结果转换为百分比的方法
	 * @param jsonModel 当次查询json结果
	 * @param seleteFullNames 要转换的detail全名
	 * @throws BussException CAN_NOT_TO_PERCENT
	 */
	void handleToPercent(RRDJsonModel jsonModel, Collection<String> seleteFullNames ) throws BussException;
	/**
	 * 结果是否可转换为百分比形式
	 */
	boolean canChangeToPercent();

	void handleTotal(RRDJsonModel jsonModel,Unit tarUnit) throws BussException;
	/**
	 * 结果是否可转换为百分比形式
	 */
	boolean hasTotal();
	/**
	 * 较为通用的转百分比处理默认方法
	 * @param jsonModel rrd数据模型
	 * @param seleteFullNames 保留的数据名全称
	 * @param totalDataFullName 分母或总量数据的全称
	 * @throws BussException
	 */
	static void handleToPercent(RRDJsonModel jsonModel, Collection<String> seleteFullNames, String totalDataFullName ) throws BussException{
		List<DataJsonModel> totaljsonModel = jsonModel.getList().stream().filter((djm)->{
			return totalDataFullName.equals(djm.getName());
		}).collect(Collectors.toList());
		if (totaljsonModel.size()>=1) {
			DataJsonModel totalDjm = totaljsonModel.get(0);
			final BigDecimal num_100=new BigDecimal(100);
			jsonModel.getList().stream().filter((djm)->{
				return seleteFullNames.contains(djm.getName());
			}).forEach((djm)->{
				for (int j = 0; j < djm.getData().length; j++) {
					if (djm.getData()[j]==null||totalDjm.getData()[j]==null||totalDjm.getData()[j].equals(BigDecimal.ZERO)) {
						djm.getData()[j]=null;
						continue;
					}
					djm.getData()[j]=djm.getData()[j].multiply(num_100)
							.divide(totalDjm.getData()[j], Constant.numberScale ,Constant.roundingMode);
				}
			});
		}else{
			throw new BussException(BussException.CAN_NOT_TO_PERCENT,"未找到Total数据，不能转换为百分比");
		}
	}

	/**
	 * 较为通用的转百分比处理默认方法
	 * @param jsonModel rrd数据模型
	 * @param totalDataFullName 分母或总量数据的全称
	 * @throws BussException
	 */
	static void handleTotal(RRDJsonModel jsonModel, String totalDataFullName,Unit tarUnit,Unit sourceUnit ) throws BussException{
		List<DataJsonModel> totaljsonModel = jsonModel.getList().stream().filter((djm)->{
			return totalDataFullName.equals(djm.getName());
		}).collect(Collectors.toList());
		if (totaljsonModel.size()>=1) {
			DataJsonModel totalDjm = totaljsonModel.get(0);
			for(int i=totalDjm.getData().length-1;i>=0;i--){
				if (totalDjm.getData()[i]!=null){
					jsonModel.setTotal(totalDjm.getData()[i].setScale(Constant.numberScale,Constant.roundingMode));
					BigDecimal value;
					if (tarUnit==null){
						List<BigDecimal[]> tempList=new ArrayList<>();
						tempList.add(new BigDecimal[]{totalDjm.getData()[i]});
						Unit.SuitableUnitAndValue suitableUnitAndValue = sourceUnit.toSuitableUnitAndValue(tempList);
						value=suitableUnitAndValue.values.get(0)[0].setScale(Constant.numberScale,Constant.roundingMode);
						jsonModel.setTotal(value);
						jsonModel.setTotalUnit(suitableUnitAndValue.unit.toString());
					}else{
						value=jsonModel.getTotal().divide(tarUnit.timesOf(sourceUnit),Constant.numberScale,Constant.roundingMode);
						jsonModel.setTotal(value);
						jsonModel.setTotalUnit(tarUnit.toString());
					}
					return;
				}
			}
		}else{
			throw new BussException(BussException.CAN_NOT_FIND_TOTAL,"未找到Total数据，不能转换为百分比");
		}
	}
}
