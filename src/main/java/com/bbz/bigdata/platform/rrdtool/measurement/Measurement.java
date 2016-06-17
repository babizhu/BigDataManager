package com.bbz.bigdata.platform.rrdtool.measurement;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.CPU;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Disk;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Memory;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Network;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 检测量 如CPU
 * @author weiran
 *
 */
public abstract class Measurement {
	/**
	 * 测量结果的单位
	 *
	 */
	public abstract Unit getResultUnit();
	/**
	 * 测量名称
	 *
	 */
	public abstract String name();

	/**
	 * 显示名称
	 * @return
     */
//	public abstract String showName();
	/**
	 * 所有详细测量量
	 * @return key:detailName,value:Detail对象
	 */
	public abstract Map<String,Detail> allDetails();

	public Detail[] All(){
		return allDetails().values().toArray(new Detail[]{});
	}

	public String[] AllFullNames(){
		return allDetails().values().stream().map(detail->{
			return detail.fullName();
		}).collect(Collectors.toSet()).toArray(new String[]{});
	}

	/**
	 * 详细测量量 如CPU.User
	 * @author weiran
	 *
	 */
	public class Detail{
		
		public Detail(Measurement measurement,String name){
			this.measurement=measurement;
			this.name=name;
			this.unit=measurement.getResultUnit();
		}

		public Detail(Measurement measurement,String name,Unit unit){
			this.measurement=measurement;
			this.name=name;
			this.unit=unit==null?measurement.getResultUnit():unit;
		}
		
		private String name;
		private Unit unit;
		private Measurement measurement;
		
		public String selfName(){
			return this.name;
		}
		
		public String fullName(){
			return measurement.name()+"."+this.name;
		}
		
		public Measurement getMeasurement(){
			return this.measurement;
		}

		public Unit unit(){return this.unit;}

	}

	public static boolean containsDetail(Collection<Measurement> measurements, String detailName){
		for (Measurement measurement:measurements ) {
			if (measurement.allDetails().containsKey(detailName)){
				return true;
			}
		}
		return false;
	}
}
