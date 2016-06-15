package com.bbz.bigdata.platform.rrdtool.resultparser;


import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.Util;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.DataModel;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.MeasurementCreator;
import com.bbz.bigdata.platform.rrdtool.xmlmodel.DataXMLModel;
import com.bbz.bigdata.platform.rrdtool.xmlmodel.FullXMLModel;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RRDResultConvertor {

	/**
	 * 把rrd查询数据类转换成json类
	 * @param resultModel rrd结果
	 * @param startTime 开始时间
	 * @param measurementDetails 测量详细量数组，属于一个Measurement
	 * @param showUnit 显示单位
	 * @param changeToPercent 是否需要转换为百分比形式
	 * @param measurementCreators 新数据创建方法
	 * @param measurementDetailsForShow 显示结果，默认全显示 （功能未实现）
	 * @return json数据结果
	 * @throws BussException
	 */
	public static RRDModel convert(FullXMLModel resultModel, ICmd cmd, Date startTime, List<Measurement.Detail> measurementDetails, Unit showUnit, boolean changeToPercent
			, MeasurementCreator[] measurementCreators, Measurement.Detail... measurementDetailsForShow) throws BussException{
		RRDModel rrdModel = new RRDModel();
		HashMap<String, Measurement.Detail> name_Detail_Map=new HashMap<>();
		HashMap<String, Measurement.Detail> fullName_Detail_Map=new HashMap<>();
		for (Measurement.Detail detail : measurementDetails) {
			name_Detail_Map.put(detail.selfName(), detail);
			fullName_Detail_Map.put(detail.fullName(), detail);
		}
		Measurement measurement=measurementDetails.get(0).getMeasurement();
		/**
		 * 创建json数据模型
		 */
		for(int i=0; i<resultModel.getDatas().size(); i++) {
			DataXMLModel drm = resultModel.getDatas().get(i);
			DataModel djm=new DataModel();
			if (!measurement.allDetails().containsKey(drm.getName())) {
				continue;
			}
			djm.setName(measurement.allDetails().get(drm.getName()).fullName());
			djm.setPointStart(startTime.getTime());
			djm.setPointInterval(resultModel.getStep());
			BigDecimal[] pointdatas=new BigDecimal[drm.getData().length];
			djm.setData(pointdatas);
			/**
			 * 填入数据
			 */
			for (int j=0;j<pointdatas.length;j++) {
				String drmData=drm.getData()[j];
				if (drmData!=null&&!drmData.isEmpty()&&!drmData.equals("NaN")) {
					BigDecimal data=new BigDecimal(drm.getData()[j]);
					data = data.setScale( Constant.numberScale ,Constant.roundingMode);
					pointdatas[j]=data;
				}
			}
			djm.setNewestData(Util.newestData(pointdatas,djm.getPointInterval()));
			rrdModel.getList().add(djm);
		}
		/**
		 * 计算总量
		 */
		cmd.handleTotal(rrdModel,showUnit);

		/**
		 * 单位转换，筛选用户所选
		 */
		if (changeToPercent) {
			rrdModel.setYunit(Unit.Perent);
			/**
			 *转为百分比 
			 */
			cmd.handleToPercent(rrdModel,fullName_Detail_Map.keySet());
			filteUserSelect(rrdModel, fullName_Detail_Map.keySet());
		}else {
			if (showUnit!=null&&showUnit!=measurement.getResultUnit()){ //给定了showUnit
				rrdModel.setYunit(showUnit);
				filteUserSelect(rrdModel, fullName_Detail_Map.keySet());
				/**
				 * 单位转换
				 */
				rrdModel.getList().forEach((djm)-> {
					djm.setData(showUnit.convertValue(measurement.getResultUnit(),djm.getData()));
					if(djm.getNewestData()!=null){
						djm.setNewestData(showUnit.convertValue(measurement.getResultUnit(),djm.getNewestData()));
					}
				});
			}else {
				filteUserSelect(rrdModel, fullName_Detail_Map.keySet());
				List<BigDecimal[]> tempList = rrdModel.getList().stream().map(djm -> {
					return djm.getData();
				}).collect(Collectors.toList());
				/**
				 * 自适应单位转换
				 */
				Unit.SuitableUnitAndValue suitableUnitAndValue = measurement.getResultUnit().toSuitableUnitAndValue(tempList);
				for (int i=0;i<suitableUnitAndValue.values.size();i++){
					DataModel djm = rrdModel.getList().get(i);
					djm.setData(suitableUnitAndValue.values.get(i));
					if(djm.getNewestData()!=null){
						djm.setNewestData(suitableUnitAndValue.unit.convertValue(measurement.getResultUnit(),djm.getNewestData()));
					}
				}
				rrdModel.setYunit(suitableUnitAndValue.unit);
			}
		}

		/**
		 * 用户创建的测量数据
		 */
		List<DataModel> newCreatedData=new ArrayList<>();
		if(measurementCreators!=null) {
			Stream.of(measurementCreators).forEach((mc) -> {
				if(rrdModel.getList().size()==0){
					return;
				}
				DataModel tempDjm = rrdModel.getList().iterator().next();
				int dataLength = tempDjm.getData().length;
				BigDecimal[] resData = createMeasureData(rrdModel, dataLength, mc);
				DataModel djm = new DataModel();
				djm.setData(resData);
				djm.setPointStart(tempDjm.getPointStart());
				djm.setPointInterval(tempDjm.getPointInterval());
				djm.setName(mc.getNewName());
				djm.setNewestData(Util.newestData(resData,djm.getPointInterval()));
				newCreatedData.add(djm);
			});
		}
		/**
		 * 保留显示数据
		 */
		if(measurementDetailsForShow!=null){
			rrdModel.setList(rrdModel.getList().stream().filter((drm)->{

				for (Measurement.Detail md:
						measurementDetailsForShow) {
					if(md!=null&&md.fullName().equals(drm.getName())){
						return true;
					}
				}
				return false;
			}).collect(Collectors.toList()));
		}

		rrdModel.getList().addAll(newCreatedData);

		/**
		 * 数据点采样
		 */
		filteDataInterval(rrdModel,null);
		/**
		 * 处理小数点
		 */
		rrdModel.getList().stream().forEach(djm->{
			for (int i=0;i<djm.getData().length;i++){
				if(djm.getData()[i]==null){
					continue;
				}
				djm.getData()[i]=djm.getData()[i].setScale(Constant.numberScale,Constant.roundingMode);
			}
		});
		return rrdModel;
	}

	/**
	 * 创建新的测量数据，放入结果
     */
	private static BigDecimal[] createMeasureData(RRDModel rrdModel, int dataLength, MeasurementCreator mc){
		BigDecimal[] firstData, secondData;
		{
			if (mc.getD1()!=null){
				firstData=new BigDecimal[dataLength];
				for (int i=0;i<dataLength;i++){
					firstData[i]=mc.getD1();
				}
			}else if(mc.getMd1()!=null){
				DataModel djm1 = rrdModel.getList().stream().filter((djm) -> {
					return mc.getMd1().fullName().equals(djm.getName());
				}).findFirst().get();
				if (djm1==null){
					throw new ParameterNamesNotFoundException("first data not found");
				}else{
					firstData=djm1.getData();
				}
			}else if(mc.getNextOp1()!=null){
				firstData=createMeasureData(rrdModel,dataLength,mc.getNextOp1());
			}else{
				throw new ParameterNamesNotFoundException("first data not found");
			}
		}
		{
			if (mc.getD2()!=null){
				secondData=new BigDecimal[dataLength];
				for (int i=0;i<dataLength;i++){
					secondData[i]=mc.getD2();
				}
			}else if(mc.getMd2()!=null){
				DataModel djm2 = rrdModel.getList().stream().filter((djm) -> {
					return mc.getMd2().fullName().equals(djm.getName());
				}).findFirst().get();
				if (djm2==null){
					throw new ParameterNamesNotFoundException("second data not found");
				}else{
					secondData=djm2.getData();
				}
			}else if(mc.getNextOp2()!=null){
				secondData=createMeasureData(rrdModel,dataLength,mc.getNextOp2());
			}else{
				throw new ParameterNamesNotFoundException("second data not found");
			}
		}
		return mc.operate(firstData,secondData);
	}


	/**
	 *过滤用户所选detail 
	 */
	private static void filteUserSelect(RRDModel rrdModel, Collection<String> seletedFullName){
		rrdModel.setList(
				rrdModel.getList().stream().filter((djm)->{
						return seletedFullName.contains(djm.getName());
					}).collect( Collectors.toList())
				);
	}

	private static void filteDataInterval(RRDModel rrdModel, Integer pointAmountThreshold){
		if (rrdModel ==null|| rrdModel.getList()==null|| rrdModel.getList().size()==0){
			return;
		}
		rrdModel.getList().forEach(djm->{
			int interval=calDataPiontInterval(djm.getData().length,pointAmountThreshold);
			if (interval==1){
				return;
			}
			int intervalTime=djm.getPointInterval()*interval;
			BigDecimal[] newData=new BigDecimal[(djm.getData().length-1)/interval+1];
			long newStart=djm.getPointStart()+(djm.getData().length-((newData.length-1)*interval+1))*djm.getPointInterval()*1000;
			/**
			 * i:data从右往左第几个数，n：第几个筛选出来的数
			 */
			for (int i=1,n=1;i<=djm.getData().length;i++){
				if((i-1)%interval==0){
					newData[newData.length-n]=djm.getData()[djm.getData().length-i];
					n++;
				}
			}
			djm.setData(newData);
			djm.setPointInterval(intervalTime);
			djm.setPointStart(newStart);
		});
	}

	private static int calDataPiontInterval(int totalDataAmount,Integer pointAmountThreshold){
		if (pointAmountThreshold==null||pointAmountThreshold<=0){
			pointAmountThreshold=Constant.defaultPointAmountThreshold;
		}
		if(totalDataAmount<=pointAmountThreshold){
			return 1;
		}
		int interval = totalDataAmount/pointAmountThreshold;
		return interval;
	}

//	public static void main(String[] args) {
//		RRDModel rrdModel=new RRDModel();
//		rrdModel.setList(new ArrayList<>());
//		DataModel djm=new DataModel();
//		rrdModel.getList().add(djm);
//		djm.setPointStart(10000);
//		djm.setPointInterval(1);
//		djm.setData(new BigDecimal[11]);
//		for (int i=1;i<12;i++){
//			djm.getData()[i-1]=new BigDecimal(i);
//		}
//		filteDataInterval(rrdModel,3);
//		for (BigDecimal b:djm.getData()
//			 ) {
//			System.out.println(b);
//		}
//		System.out.println(djm.getPointStart());
//		System.out.println(djm.getPointInterval());
//	}
}
