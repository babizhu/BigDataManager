package com.bbz.bigdata.platform.rrdtool.resultparser;


import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.DataJsonModel;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.FullJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.resultmodel.DataXMLModel;
import com.bbz.bigdata.platform.rrdtool.resultmodel.FullXMLModel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JsonResultConvertor {

	/**
	 * 把rrd查询数据类转换成json类
	 * @param resultModel rrd结果
	 * @param startTime 开始时间
	 * @param measurementDetails 测量详细量数组
	 * @param showUnit 显示单位
	 * @param changeToPercent 是否需要转换为百分比形式
	 * @return json数据结果
	 * @throws BussException
	 */
	public static FullJsonModel convert( FullXMLModel resultModel, ICmd cmd, Date startTime, List<Measurement.Detail> measurementDetails, Unit showUnit, boolean changeToPercent) throws BussException{
		FullJsonModel jsonModel = new FullJsonModel();
		HashMap<String, Measurement.Detail> name_Detail_Map=new HashMap<>();
		HashMap<String, Measurement.Detail> fullName_Detail_Map=new HashMap<>();
		for (Measurement.Detail detail : measurementDetails) {
			name_Detail_Map.put(detail.selfName(), detail);
			fullName_Detail_Map.put(detail.fullName(), detail);
		}
		Measurement measurement=measurementDetails.get(0).getMeasurement();
		/*
		 * 创建json数据模型
		 */
		for(int i=0; i<resultModel.getDatas().size(); i++) {
			DataXMLModel drm = resultModel.getDatas().get(i);
			DataJsonModel djm=new DataJsonModel();
			if (!measurement.allDetails().containsKey(drm.getName())) {
				continue;
			}
			djm.setName(measurement.allDetails().get(drm.getName()).fullName());
			djm.setPointStart(startTime.getTime());
			djm.setPointInterval(resultModel.getStep()*1000);
			Double[] pointdatas=new Double[drm.getData().length];
			djm.setData(pointdatas);
			jsonModel.getList().add(djm);
			/*
			 * 填入数据
			 */
			for (int j=0;j<pointdatas.length;j++) {
				String drmData=drm.getData()[j];
				if (drmData!=null&&!drmData.isEmpty()&&!drmData.equals("NaN")) {
					BigDecimal data=new BigDecimal(drm.getData()[j]);
					data = data.setScale( Constant.numberScale ,Constant.roundingMode);
					pointdatas[j]=data.doubleValue();
				}
			}
		}
		if (changeToPercent) {
			jsonModel.setYunit(Unit.Perent.toString());
			/*
			 *转为百分比 
			 */
			cmd.handleToPercent(jsonModel,fullName_Detail_Map.keySet());
			filterUserSelect(jsonModel, fullName_Detail_Map.keySet());
		}else if (showUnit!=null&&showUnit!=measurement.getResultUnit()) {
			jsonModel.setYunit(showUnit.toString());
			filterUserSelect(jsonModel, fullName_Detail_Map.keySet());
			/*
			 * 单位转换
			 */
			BigDecimal times=showUnit.timesOf(measurement.getResultUnit());
			jsonModel.getList().forEach((djm)-> {
				for (int i = 0; i < djm.getData().length; i++) {
					if (djm.getData()[i]==null) {
						continue;
					}
					if (times.doubleValue()==0) {  
						/*
						 * 单位换算的精度应足够大，避免现此情况 
						 */
						djm.getData()[i]=Double.MAX_VALUE;
					}else{
						djm.getData()[i]=new BigDecimal(djm.getData()[i]).divide(times, Constant.numberScale ,Constant.roundingMode).doubleValue();
					}
				}
			});
		}else{
			jsonModel.setYunit(measurement.getResultUnit().toString());
			filterUserSelect(jsonModel, fullName_Detail_Map.keySet());
			jsonModel.getList().forEach((djm)-> {
				for (int i = 0; i < djm.getData().length; i++) {
					if (djm.getData()[i]==null) {
						continue;
					}
					djm.getData()[i]=new BigDecimal(djm.getData()[i]).setScale(Constant.numberScale ,Constant.roundingMode).doubleValue();
				}
			});
		}
		return jsonModel;
	}

	/**
	 *过滤用户所选detail 
	 */
	private static void filterUserSelect(FullJsonModel jsonModel,Collection<String> seletedFullName){
		jsonModel.setList(
				jsonModel.getList().stream().filter((djm)->{
						return seletedFullName.contains(djm.getName());
					}).collect( Collectors.toList())
				);
	}

}
