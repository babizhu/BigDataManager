package com.bbz.bigdata.platform.rrdtool;

import com.bbz.bigdata.platform.rrdtool.exception.BussException;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


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

		public static Type Number=new Type();
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
	private static final BigDecimal NUM_1000 =new BigDecimal(1000);

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
	public static Unit PB=new Unit("TB",Type.Binary, NUM_1024.multiply(NUM_1024).multiply(NUM_1024).multiply(NUM_1024).multiply(NUM_1024));

	public static Unit Second=new Unit("sec", Type.Time, BigDecimal.valueOf(1));
	public static Unit Minute=new Unit("min", Type.Time, BigDecimal.valueOf(60));

	public static Unit BytePerSecond=new Unit("B/S", Byte, oper_divide, Second);
	public static Unit KBPerSecond=new Unit("K/S", KB, oper_divide, Second);
	public static Unit MBPerSecond=new Unit("M/S", MB, oper_divide, Second);
	public static Unit GBPerSecond=new Unit("G/S", GB, oper_divide, Second);

	public static Unit Perent=new Unit("%", Type.Fraction, BigDecimal.valueOf(0.01));

	public static Unit Num=new Unit("", Type.Number, BigDecimal.ONE);
	public static Unit K=new Unit("K", Type.Number, NUM_1000);
	public static Unit M=new Unit("M", Type.Number, NUM_1000.multiply(NUM_1000));
	public static Unit G=new Unit("G", Type.Number, NUM_1000.multiply(NUM_1000).multiply(NUM_1000));
	public static Unit T=new Unit("T", Type.Number, NUM_1000.multiply(NUM_1000).multiply(NUM_1000).multiply(NUM_1000));

	public static Unit Hz=new Unit("Hz", Num, oper_divide, Second);
	public static Unit MHz=new Unit("MHz", M, oper_divide, Second);
	public static Unit GHz=new Unit("GHz", G, oper_divide, Second);

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

	/**
	 * 单位转换后的值
	 * @return
	 */
	public BigDecimal convertValue(Unit sourceUnit,BigDecimal value){
		if (value==null){
			return null;
		}
		BigDecimal times= null;
		try {
			// 此处不应该抛异常
			times = sourceUnit.timesOf(this);
		} catch (BussException e) {
			e.printStackTrace();
		}
		return value.multiply(times).setScale(Constant.numberScale,Constant.roundingMode);
	}

	/**
	 * 单位转换后的值
	 * @param values
	 * @return
     */
	public BigDecimal[] convertValue(Unit sourceUnit,BigDecimal[] values){
		if (values==null){
			return null;
		}
		BigDecimal times= null;
		try {
			// 此处不应该抛异常
			times = sourceUnit.timesOf(this);
		} catch (BussException e) {
			e.printStackTrace();
		}
		BigDecimal[] newValues=new BigDecimal[values.length];
		for (int i=0;i<newValues.length;i++) {
			if (values[i]==null){
				continue;
			}
			newValues[i] = values[i].multiply(times).setScale(Constant.numberScale,Constant.roundingMode);
		}
		return newValues;
	}

	/**
	 * 计算合适的单位和值
	 * @param values
	 * @return
	 */
	public SuitableUnitAndValue toSuitableUnitAndValue(List<BigDecimal[]> values){
		Unit unit=this;
		/**
		 * 寻找同类型的unit
		 */
		Collection<Unit> sameTypeUnits = type_units_map.get(unit.type);
		if (sameTypeUnits.size()==1){
			return new SuitableUnitAndValue(unit,values);
		}
		/**
		 * 计算最大数值
		 */
		if (values==null||values.size()==0){
			return new SuitableUnitAndValue(unit,new ArrayList<>());
		}
		BigDecimal maxValue=null;
		for (BigDecimal[] arr:values) {
			if (arr==null) continue;
			for (int i=0;i<arr.length;i++) {
				if (arr[i] == null) continue;
				BigDecimal abs = arr[i].abs();
				if (maxValue==null||maxValue.compareTo(abs) == -1){
					maxValue=abs;
				}
			}
		}
		if(maxValue==null){
			return new SuitableUnitAndValue(unit,values);
		}
		HashMap<Unit,BigDecimal> unit_value_map=new HashMap<>();
		/**
		 * 计算和比较权重比
		 */
		for (Unit u:sameTypeUnits
				) {
			BigDecimal convertedValue = maxValue.multiply(unit.weightGetter.get()).divide(u.weightGetter.get());
			unit_value_map.put(u,convertedValue);
		}
		Iterator<Map.Entry<Unit, BigDecimal>> iterator = unit_value_map.entrySet().stream()
				.sorted((e1, e2) -> {
					return e1.getValue().compareTo(e2.getValue());
				})
				.collect(Collectors.toList())
				.iterator();
		Map.Entry<Unit, BigDecimal> entry=null;
		Map.Entry<Unit, BigDecimal> lastEntry=null;
		BigDecimal threshold=BigDecimal.valueOf(1);
		while(iterator.hasNext()){
			entry = iterator.next();
			if (entry.getValue().compareTo(threshold)>=0){
				break;
			}
			lastEntry=entry;
		}
		if (entry==null){
			entry=lastEntry;
		}
		if (entry.getKey()==unit){
			return new SuitableUnitAndValue(unit,values);
		}
		/**
		 * 根据新单位计算结果数值
		 */
		List<BigDecimal[]> list=new ArrayList<>();
		for(BigDecimal[] arr:values){
			list.add(entry.getKey().convertValue(unit,arr));
		}
		return new SuitableUnitAndValue(entry.getKey(),list);
	}
	public static void main(String[] args) {
		BigDecimal[] arr1=new BigDecimal[]{new BigDecimal(100),new BigDecimal(3000),new BigDecimal(0.1)};
		BigDecimal[] arr2=new BigDecimal[]{new BigDecimal(100),new BigDecimal(-4000),new BigDecimal(0.1)};
		List<BigDecimal[]> list=new ArrayList<>();
		list.add(arr1);
		list.add(arr2);
		SuitableUnitAndValue res = MB.toSuitableUnitAndValue(list);
		System.out.println(res.unit);
		for (BigDecimal[] arr:res.values
			 ) {

			for (BigDecimal b:arr
					) {
				System.out.println(b);
			}
		}
	}

	public class SuitableUnitAndValue{
		private SuitableUnitAndValue(){}
		private SuitableUnitAndValue(Unit unit,List<BigDecimal[]> values){
			this.unit=unit;
			this.values=values;
		}
		public Unit unit;
		public List<BigDecimal[]> values;
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
