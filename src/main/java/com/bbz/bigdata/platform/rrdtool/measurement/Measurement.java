package com.bbz.bigdata.platform.rrdtool.measurement;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.CPU;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Disk;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Memory;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Network;

import java.util.Collection;
import java.util.Map;

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
	 * 所有详细测量量
	 * @return key:detailName,value:Detail对象
	 */
	public abstract Map<String,Detail> allDetails();

	/**
	 * 详细测量量 如CPU.User
	 * @author weiran
	 *
	 */
	public class Detail{
		
		public Detail(Measurement measurement,String name){
			this.measurement=measurement;
			this.name=name;
		}
		
		private String name;
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
