package com.bbz.bigdata.platform.rrdtool.measurement.impl;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;

import java.util.HashMap;
import java.util.Map;

public class Disk extends Measurement{

	@Override
	public Unit getResultUnit() {
		return Unit.GB;
	}

	@Override
	public String name() {
		return "Disk";
	}

	@Override
	public Map<String,Detail> allDetails() {
		// TODO Auto-generated method stub
		return new HashMap<>(this.name_detail_map);
	}
	
	public Measurement.Detail Free=this.new Detail(this,"Free");
	public Measurement.Detail Total=this.new Detail(this,"Total");
	
	private HashMap<String, Detail> name_detail_map=new HashMap<>();
	
	public Disk() {
		this.name_detail_map.put(Free.selfName(),Free);
		this.name_detail_map.put(Total.selfName(),Total);
	}
}
