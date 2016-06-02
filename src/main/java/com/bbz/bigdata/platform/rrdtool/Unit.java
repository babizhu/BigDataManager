package com.bbz.bigdata.platform.rrdtool;

import com.bbz.bigdata.platform.rrdtool.exception.BussException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * 测量数据的单位
 * @author weiran
 *
 */
public class Unit {

	private interface Operator{
		BigDecimal operate(BigDecimal num1,BigDecimal num2);
	}

	private static Operator oper_divide=(num1,num2) -> {
			return num1.divide(num2,Constant.unitNumberScale,Constant.roundingMode);
	};

	private static class Type{

		public static Type Binary=new Type();
		public static Type Time=new Type();
		public static Type Fraction=new Type();

		public Type(){}
		public Type(Type type1,Operator operator,Type type2){
			this.type1=type1;
			this.operator=operator;
			this.type2=type2;
		}
		private Type type1;
		private Operator operator;
		private Type type2;

		@Override
		public int hashCode() {
			if (operator==null){
				return super.hashCode();
			}else{
				return (type1.hashCode()+"-"+operator.hashCode()+"-"+type2.hashCode()).hashCode();
			}
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Type)){
				return false;
			}
			Type tar=(Type) obj;
			if (operator==null){
				return this==tar;
			}else{
				return this.operator==tar.operator&&this.type1.equals(tar.type1)&&this.type2.equals(tar.type2);
			}
		}
	}

	private  Unit(String word,Type type,BigDecimal weight){
		this.word=word;
		this.type=type;
		this.weightGetter= ()->{return weight;};
		putToType_units_map(this);
	}

	private  Unit(String word,Unit numerator,Operator operator, Unit denominator){
		this.word=word;
		this.type=new Type(numerator.type,operator,denominator.type);
		this.weightGetter=()->{
			return operator.operate(numerator.weightGetter.get(),denominator.weightGetter.get());
		};
		putToType_units_map(this);
	}
	
	private String word;
	private Type type;
	private Supplier<BigDecimal> weightGetter;
//	private Unit denominator;

	private static final BigDecimal NUM_1024 =new BigDecimal(1024);

	private static HashMap<Type,Collection<Unit>> type_units_map=new HashMap<>();

	private static void putToType_units_map(Unit unit){
		Collection<Unit> units = type_units_map.get(unit.type);
		if (units==null){
			units=new LinkedList<>();
			type_units_map.put(unit.type,units);
		}
		units.add(unit);
	}
	
	public static Unit Byte=new Unit("Byte",Type.Binary,BigDecimal.valueOf(1));
	public static Unit KB=new Unit("KB",Type.Binary, NUM_1024);
	public static Unit MB=new Unit("MB",Type.Binary, NUM_1024.multiply(NUM_1024));
	public static Unit GB=new Unit("GB",Type.Binary, NUM_1024.multiply(NUM_1024).multiply(NUM_1024));
	public static Unit TB=new Unit("TB",Type.Binary, NUM_1024.multiply(NUM_1024).multiply(NUM_1024).multiply(NUM_1024));
	
	public static Unit Second=new Unit("sec", Type.Time, BigDecimal.valueOf(1));
	public static Unit Minute=new Unit("min", Type.Time, BigDecimal.valueOf(60));
	
	public static Unit Perent=new Unit("%", Type.Fraction, BigDecimal.valueOf(0.01));

	public static Unit BytePerSecond=new Unit("B/S", Byte, oper_divide, Second);
	public static Unit KBPerSecond=new Unit("K/S", KB, oper_divide, Second);
	public static Unit MBPerSecond=new Unit("M/S", MB, oper_divide, Second);

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
		if (!this.type.equals(tar.type)) {
			throw new BussException(BussException.UNITTYPE_NOT_MATCHED);
		}
		return this.weightGetter.get().divide(tar.weightGetter.get(), Constant.unitNumberScale ,Constant.roundingMode);
	}

	public boolean sameType(Unit tar){
		return this.type.equals(tar.type);
	}

	public static boolean sameType(Iterable<Unit> units){
		Unit first=null;
		for (Unit unit:units ) {
			if (first==null){
				first=unit;
			}else{
				if (!first.sameType(unit)){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return word;
	}
	
//	public Type getType() {
//		return type;
//	}
	
	
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
