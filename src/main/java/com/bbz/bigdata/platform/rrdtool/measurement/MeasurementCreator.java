package com.bbz.bigdata.platform.rrdtool.measurement;

import com.bbz.bigdata.platform.rrdtool.Constant;

import java.math.BigDecimal;

/**
 * 新数据创建方法
 * @author weiran
 *
 */
public class MeasurementCreator {

	public MeasurementCreator(Measurement.Detail md1, Operator operator, Measurement.Detail md2, String newName){
		this.md1=md1;
		this.md2=md2;
		this.operator=operator;
		this.newName=newName;
	}

	public MeasurementCreator(Measurement.Detail md1, Operator operator, MeasurementCreator nextOp2, String newName){
		this.md1=md1;
		this.operator=operator;
		this.newName=newName;
		this.nextOp2=nextOp2;
	}

	public MeasurementCreator(Measurement.Detail md1, Operator operator, Double d2, String newName){
		this.md1=md1;
		this.operator=operator;
		this.newName=newName;
		this.d2=d2;
	}

	public MeasurementCreator(MeasurementCreator nextOp1, Operator operator, Measurement.Detail md2, String newName){
		this.md2=md2;
		this.operator=operator;
		this.newName=newName;
		this.nextOp1=nextOp1;
	}

	public MeasurementCreator(MeasurementCreator nextOp1, Operator operator, Double d2, String newName){
		this.d2=d2;
		this.operator=operator;
		this.newName=newName;
		this.nextOp1=nextOp1;
	}

	public MeasurementCreator(MeasurementCreator nextOp1, Operator operator, MeasurementCreator nextOp2, String newName){
		this.nextOp1=nextOp1;
		this.operator=operator;
		this.newName=newName;
		this.nextOp2=nextOp2;
	}

	public MeasurementCreator(Double d1, Operator operator, Measurement.Detail md2, String newName){
		this.d1=d1;
		this.md2=md2;
		this.operator=operator;
		this.newName=newName;
	}

	public MeasurementCreator(Double d1, Operator operator, MeasurementCreator nextOp2, String newName){
		this.d1=d1;
		this.operator=operator;
		this.newName=newName;
		this.nextOp2=nextOp2;
	}

	public MeasurementCreator(Double d1, Operator operator, Double d2, String newName){
		this.d1=d1;
		this.operator=operator;
		this.newName=newName;
		this.d2=d2;
	}

	private Measurement.Detail md1;
	private Measurement.Detail md2;
	private Double d1;
	private Double d2;
	private Operator operator;
	private String newName;
	private MeasurementCreator nextOp1;
	private MeasurementCreator nextOp2;

	public enum Operator{
		ADD,
		MINUS
	}

	public Double[] operate(Double[] d1,Double[] d2){
		if (d1==null||d2==null){
			return null;
		}
		Double[] arr=new Double[d1.length];
		for (int i=0;i<arr.length;i++){
			arr[i]=operate(d1[i],d2[i]);
		}
		return arr;
	}

	private Double operate(Double d1,Double d2){
		if (d1==null||d2==null){
			return null;
		}
		if(operator==Operator.ADD){
			return new BigDecimal(d1).add(new BigDecimal(d2)).setScale(Constant.numberScale,Constant.roundingMode).doubleValue();
		}else if(operator==Operator.MINUS){
			return new BigDecimal(d1).subtract(new BigDecimal(d2)).setScale(Constant.numberScale,Constant.roundingMode).doubleValue();
		}else{
			return null;
		}
	}


	public Measurement.Detail getMd1() {
		return md1;
	}

	public void setMd1(Measurement.Detail md1) {
		this.md1 = md1;
	}

	public Measurement.Detail getMd2() {
		return md2;
	}

	public void setMd2(Measurement.Detail md2) {
		this.md2 = md2;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public Double getD1() {
		return d1;
	}

	public void setD1(Double d1) {
		this.d1 = d1;
	}

	public Double getD2() {
		return d2;
	}

	public void setD2(Double d2) {
		this.d2 = d2;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public MeasurementCreator getNextOp1() {
		return nextOp1;
	}

	public void setNextOp1(MeasurementCreator nextOp1) {
		this.nextOp1 = nextOp1;
	}

	public MeasurementCreator getNextOp2() {
		return nextOp2;
	}

	public void setNextOp2(MeasurementCreator nextOp2) {
		this.nextOp2 = nextOp2;
	}
}
