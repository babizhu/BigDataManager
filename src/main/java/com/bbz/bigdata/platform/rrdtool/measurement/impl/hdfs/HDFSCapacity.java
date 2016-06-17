package com.bbz.bigdata.platform.rrdtool.measurement.impl.hdfs;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;

import java.util.HashMap;
import java.util.Map;


public class HDFSCapacity extends Measurement{

	@Override
	public Unit getResultUnit() {
		return Unit.GB;
	}

	@Override
	public String name() {
		return "HDFSCapacity";
	}

	@Override
	public Map<String, Detail> allDetails() {
		return new HashMap<>(name_detail_map);
	}

	public Detail Total=this.new Detail(this,"Total");
	public Detail Used=this.new Detail(this,"Used");
	public Detail Remaining=this.new Detail(this,"Remaining");

	private HashMap<String, Detail> name_detail_map=new HashMap<>();

	public HDFSCapacity(){
		this.name_detail_map.put(Total.selfName(),Total);
		this.name_detail_map.put(Used.selfName(),Used);
		this.name_detail_map.put(Remaining.selfName(),Remaining);
	}

}
