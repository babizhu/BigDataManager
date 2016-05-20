package com.bbz.bigdata.platform.rrdtool;

import com.bbz.bigdata.platform.rrdtool.exception.BussException;

import java.math.BigDecimal;



/**
 * 测量数据的单位
 * @author weiran
 *
 */
public class Unit {
	
	public enum Type{
		UNIT_OF_STORAGE,
		FRACTIONS,
		TIME
	}

	private  Unit(String word,Type type,BigDecimal weight){
		this.word=word;
		this.type=type;
		this.weight=weight;
	}
	
	private  Unit(String word,Unit numerator,Unit denominator){
		this.word=word;
		this.type=numerator.type;
		this.weight=numerator.weight;
		this.denominator=denominator;
	}
	
	private String word;
	private Type type;
	private BigDecimal weight;
	private Unit denominator;

	private static final BigDecimal M_1024=new BigDecimal(1024);
	
	public static Unit Byte=new Unit("Byte",Type.UNIT_OF_STORAGE,BigDecimal.valueOf(1));
	public static Unit KB=new Unit("KB",Type.UNIT_OF_STORAGE,M_1024);
	public static Unit MB=new Unit("MB",Type.UNIT_OF_STORAGE,M_1024.multiply(M_1024));
	public static Unit GB=new Unit("GB",Type.UNIT_OF_STORAGE,M_1024.multiply(M_1024).multiply(M_1024));
	public static Unit TB=new Unit("TB",Type.UNIT_OF_STORAGE,M_1024.multiply(M_1024).multiply(M_1024).multiply(M_1024));
	
	public static Unit Second=new Unit("sec", Type.TIME, BigDecimal.valueOf(1));
	public static Unit Minute=new Unit("sec", Type.TIME, BigDecimal.valueOf(60));
	
	public static Unit Perent=new Unit("%", Type.FRACTIONS, BigDecimal.valueOf(0.01));

	public static Unit BytePerSecond=new Unit("B/sec", Byte, Second);
	public static Unit KBPerSecond=new Unit("K/sec", KB, Second);
	public static Unit MBPerSecond=new Unit("K/sec", MB, Second);
	
	/**
	 * 当前单位与目标单位之比
	 * example: MB.timesOf(KB)==1024
	 * @param tar
	 * @return
	 * @throws BussException
	 */
	public BigDecimal timesOf(Unit tar) throws BussException{
		if (this==tar) {
			return new BigDecimal(1);
		}
		if (this.type!=tar.type) {
			throw new BussException(BussException.UNITTYPE_NOT_MATCHED);
		}
		if (this.denominator==null) {
			if (tar.denominator!=null) {
				throw new BussException(BussException.UNITTYPE_NOT_MATCHED);
			}
			return this.weight.divide(tar.weight, Constant.unitNumberScale ,Constant.roundingMode);
		}else{
			if (tar.denominator==null) {
				throw new BussException(BussException.UNITTYPE_NOT_MATCHED);
			}
			return this.weight.multiply(tar.denominator.timesOf(this.denominator)).divide(tar.weight, Constant.unitNumberScale ,Constant.roundingMode);
		}
	}
	
	@Override
	public String toString() {
		return word;
	}
	
	public Type getType() {
		return type;
	}
	
	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj==null) {
//			return false;
//		}
//		if (obj instanceof Unit) {
//			Unit unit=(Unit)obj;
//			if (this!=unit) {
//				return false;
//			}else{
//				if (this.denominator==null) {
//					return unit.denominator==null;
//				}else{
//					return this.denominator.equals(unit.denominator);
//				}
//			}
//			
//		}else{
//			return false;
//		}
//	}

}
