//package com.bbz.bigdata.platform.rrdtool.resultparser;
//
//
//import com.bbz.bigdata.platform.rrdtool.Constant;
//import com.bbz.bigdata.platform.rrdtool.Unit;
//import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
//import com.bbz.bigdata.platform.rrdtool.exception.BussException;
//import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.DataJsonModel;
//import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
//import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
//import com.bbz.bigdata.platform.rrdtool.measurement.MeasurementCreator;
//import com.bbz.bigdata.platform.rrdtool.resultmodel.DataXMLModel;
//import com.bbz.bigdata.platform.rrdtool.resultmodel.FullXMLModel;
//import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class JsonResultConvertor_bak {
//
//	/**
//	 * 把rrd查询数据类转换成json类
//	 * @param resultModel rrd结果
//	 * @param startTime 开始时间
//	 * @param measurementDetails 测量详细量数组，属于一个Measurement
//	 * @param showUnit 显示单位
//	 * @param changeToPercent 是否需要转换为百分比形式
//	 * @param measurementCreators 新数据创建方法
//	 * @param measurementDetailsForShow 显示结果，默认全显示 （功能未实现）
//	 * @return json数据结果
//	 * @throws BussException
//	 */
//	public static RRDJsonModel convert(FullXMLModel resultModel, ICmd cmd, Date startTime, List<Measurement.Detail> measurementDetails, Unit showUnit, boolean changeToPercent
//			, MeasurementCreator[] measurementCreators,Measurement.Detail... measurementDetailsForShow) throws BussException{
//		RRDJsonModel jsonModel = new RRDJsonModel();
//		HashMap<String, Measurement.Detail> name_Detail_Map=new HashMap<>();
//		HashMap<String, Measurement.Detail> fullName_Detail_Map=new HashMap<>();
//		for (Measurement.Detail detail : measurementDetails) {
//			name_Detail_Map.put(detail.selfName(), detail);
//			fullName_Detail_Map.put(detail.fullName(), detail);
//		}
//		Measurement measurement=measurementDetails.get(0).getMeasurement();
//		/*
//		 * 创建json数据模型
//		 */
//		for(int i=0; i<resultModel.getDatas().size(); i++) {
//			DataXMLModel drm = resultModel.getDatas().get(i);
//			DataJsonModel djm=new DataJsonModel();
//			if (!measurement.allDetails().containsKey(drm.getName())) {
//				continue;
//			}
//			djm.setName(measurement.allDetails().get(drm.getName()).fullName());
//			djm.setPointStart(startTime.getTime());
//			djm.setPointInterval(resultModel.getStep()*1000);
//			BigDecimal[] pointdatas=new BigDecimal[drm.getData().length];
//			djm.setData(pointdatas);
//			/*
//			 * 填入数据
//			 */
//			for (int j=0;j<pointdatas.length;j++) {
//				String drmData=drm.getData()[j];
//				if (drmData!=null&&!drmData.isEmpty()&&!drmData.equals("NaN")) {
//					BigDecimal data=new BigDecimal(drm.getData()[j]);
//					data = data.setScale( Constant.numberScale ,Constant.roundingMode);
//					pointdatas[j]=data;
//				}
//			}
//			jsonModel.getList().add(djm);
//		}
//
//		cmd.handleTotal(jsonModel,showUnit);
//
//		if (changeToPercent) {
//			jsonModel.setYunit(Unit.Perent.toString());
//			/*
//			 *转为百分比
//			 */
//			cmd.handleTotal(jsonModel,showUnit);
//			cmd.handleToPercent(jsonModel,fullName_Detail_Map.keySet());
//			filterUserSelect(jsonModel, fullName_Detail_Map.keySet());
//		}else {
//			if (showUnit!=null&&showUnit!=measurement.getResultUnit()){ //给定了showUnit
//				jsonModel.setYunit(showUnit.toString());
//				filterUserSelect(jsonModel, fullName_Detail_Map.keySet());
//				/*
//				 * 单位转换
//				 */
//				jsonModel.getList().forEach((djm)-> {
//					djm.setData(showUnit.convertValue(measurement.getResultUnit(),djm.getData()));
//				});
//			}else {
//				filterUserSelect(jsonModel, fullName_Detail_Map.keySet());
//				List<BigDecimal[]> tempList = jsonModel.getList().stream().map(djm -> {
//					return djm.getData();
//				}).collect(Collectors.toList());
//				Unit.SuitableUnitAndValue suitableUnitAndValue = measurement.getResultUnit().toSuitableUnitAndValue(tempList);
//				for (int i=0;i<suitableUnitAndValue.values.size();i++){
//					jsonModel.getList().get(i).setData(suitableUnitAndValue.values.get(i));
//				}
//				jsonModel.setYunit(measurement.getResultUnit().toString());
//			}
//		}
//
//		/**
//		 * 用户创建的测量数据
//		 */
//		List<DataJsonModel> newCreatedData=new ArrayList<>();
//		if(measurementCreators!=null) {
//			Stream.of(measurementCreators).forEach((mc) -> {
//				if(jsonModel.getList().size()==0){
//					return;
//				}
//				DataJsonModel tempDjm = jsonModel.getList().iterator().next();
//				int dataLength = tempDjm.getData().length;
//				BigDecimal[] resData = createMeasureData(jsonModel, dataLength, mc);
//				DataJsonModel djm = new DataJsonModel();
//				djm.setData(resData);
//				djm.setPointStart(tempDjm.getPointStart());
//				djm.setPointInterval(tempDjm.getPointInterval());
//				djm.setName(mc.getNewName());
//				newCreatedData.add(djm);
//			});
//		}
//		/**
//		 * 保留显示数据
//		 */
//		if(measurementDetailsForShow!=null){
//			jsonModel.setList(jsonModel.getList().stream().filter((drm)->{
//
//				for (Measurement.Detail md:
//						measurementDetailsForShow) {
//					if(md!=null&&md.fullName().equals(drm.getName())){
//						return true;
//					}
//				}
//				return false;
//			}).collect(Collectors.toList()));
//		}
//
//		jsonModel.getList().addAll(newCreatedData);
//		/**
//		 * 处理小数点
//		 */
//		jsonModel.getList().stream().forEach(djm->{
//			for (int i=0;i<djm.getData().length;i++){
//				if(djm.getData()[i]==null){
//					continue;
//				}
//				djm.getData()[i]=djm.getData()[i].setScale(Constant.numberScale,Constant.roundingMode);
//			}
//		});
//		return jsonModel;
//	}
//
//	/**
//	 * 创建新的测量数据，放入结果
//     */
//	private static BigDecimal[] createMeasureData(RRDJsonModel jsonModel,int dataLength,MeasurementCreator mc){
//		BigDecimal[] firstData, secondData;
//		{
//			if (mc.getD1()!=null){
//				firstData=new BigDecimal[dataLength];
//				for (int i=0;i<dataLength;i++){
//					firstData[i]=mc.getD1();
//				}
//			}else if(mc.getMd1()!=null){
//				DataJsonModel djm1 = jsonModel.getList().stream().filter((djm) -> {
//					return mc.getMd1().fullName().equals(djm.getName());
//				}).findFirst().get();
//				if (djm1==null){
//					throw new ParameterNamesNotFoundException("first data not found");
//				}else{
//					firstData=djm1.getData();
//				}
//			}else if(mc.getNextOp1()!=null){
//				firstData=createMeasureData(jsonModel,dataLength,mc.getNextOp1());
//			}else{
//				throw new ParameterNamesNotFoundException("first data not found");
//			}
//		}
//		{
//			if (mc.getD2()!=null){
//				secondData=new BigDecimal[dataLength];
//				for (int i=0;i<dataLength;i++){
//					secondData[i]=mc.getD2();
//				}
//			}else if(mc.getMd2()!=null){
//				DataJsonModel djm2 = jsonModel.getList().stream().filter((djm) -> {
//					return mc.getMd2().fullName().equals(djm.getName());
//				}).findFirst().get();
//				if (djm2==null){
//					throw new ParameterNamesNotFoundException("second data not found");
//				}else{
//					secondData=djm2.getData();
//				}
//			}else if(mc.getNextOp2()!=null){
//				secondData=createMeasureData(jsonModel,dataLength,mc.getNextOp2());
//			}else{
//				throw new ParameterNamesNotFoundException("second data not found");
//			}
//		}
//		return mc.operate(firstData,secondData);
//	}
//
//
//	/**
//	 *过滤用户所选detail
//	 */
//	private static void filterUserSelect(RRDJsonModel jsonModel, Collection<String> seletedFullName){
//		jsonModel.setList(
//				jsonModel.getList().stream().filter((djm)->{
//						return seletedFullName.contains(djm.getName());
//					}).collect( Collectors.toList())
//				);
//	}
//
////	private static BigDecimal unitValue(BigDecimal value,BigDecimal times){
////		return value.divide(times, Constant.numberScale ,Constant.roundingMode);
////	}
//
//}
