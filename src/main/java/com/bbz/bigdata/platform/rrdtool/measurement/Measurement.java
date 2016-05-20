package com.bbz.bigdata.platform.rrdtool.measurement;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.CPU;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Disk;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Memory;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Network;

import java.util.Map;

/**
 * 检测量 如CPU
 * @author weiran
 *
 */
public abstract class Measurement {
	/**
	 * 测量结果的单位
	 * @return
	 */
	public abstract Unit getResultUnit();
	/**
	 * 测量名称
	 * @return
	 */
	public abstract String name();
	
	/**
	 * 所有详细测量量
	 * @return key:detailName,value:Detail对象
	 */
	public abstract Map<String,Detail> allDetails();
	
	public static final CPU CPU=new CPU();
	public static final Memory Memory=new Memory();
	public static final Network Network=new Network();
	public static final Disk Disk=new Disk();
	
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
	
}
