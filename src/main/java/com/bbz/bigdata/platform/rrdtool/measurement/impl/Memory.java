package com.bbz.bigdata.platform.rrdtool.measurement.impl;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;

import java.util.HashMap;
import java.util.Map;

public class Memory extends Measurement{

	@Override
	public Unit getResultUnit() {
		return Unit.Byte;
	}
	
	@Override
	public String name() {
		return "Memory";
	}
	
	@Override
	public Map<String,Detail> allDetails() {
		// TODO Auto-generated method stub
		return new HashMap<>(this.name_detail_map);
	}
	
	public Measurement.Detail Use=this.new Detail(this,"Use");
	public Measurement.Detail Share=this.new Detail(this,"Share");
	public Measurement.Detail Cache=this.new Detail(this,"Cache");
	public Measurement.Detail Buffer=this.new Detail(this,"Buffer");
	public Measurement.Detail Free=this.new Detail(this,"Free");
	public Measurement.Detail Swap=this.new Detail(this,"Swap");
	public Measurement.Detail Total=this.new Detail(this,"Total");
	
	private HashMap<String, Detail> name_detail_map=new HashMap<>();
	
	public Memory() {
		this.name_detail_map.put(Use.selfName(),Use);
		this.name_detail_map.put(Share.selfName(),Share);
		this.name_detail_map.put(Cache.selfName(),Cache);
		this.name_detail_map.put(Buffer.selfName(),Buffer);
		this.name_detail_map.put(Free.selfName(),Free);
		this.name_detail_map.put(Swap.selfName(),Swap);
		this.name_detail_map.put(Total.selfName(),Total);
	}
}
