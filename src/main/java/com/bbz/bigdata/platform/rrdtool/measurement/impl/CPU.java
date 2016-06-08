package com.bbz.bigdata.platform.rrdtool.measurement.impl;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;

import java.util.HashMap;
import java.util.Map;



public class CPU extends Measurement{

	@Override
	public Unit getResultUnit() {
		return Unit.Perent;
	}

	@Override
	public String name() {
		return "CPU";
	}

	@Override
	public Map<String, Measurement.Detail> allDetails() {
		return new HashMap<>(name_detail_map);
	}
	
	public Measurement.Detail User=this.new Detail(this,"User");
	public Measurement.Detail Nice=this.new Detail(this,"Nice");
	public Measurement.Detail System=this.new Detail(this,"System");
	public Measurement.Detail Wait=this.new Detail(this,"Wait");
	public Measurement.Detail Steal=this.new Detail(this,"Steal");
	public Measurement.Detail Idle=this.new Detail(this,"Idle");
	public Measurement.Detail Speed=this.new Detail(this,"Speed",Unit.MHz);
	
	private HashMap<String, Detail> name_detail_map=new HashMap<>();

	public CPU(){
		this.name_detail_map.put(User.selfName(),User);
		this.name_detail_map.put(Nice.selfName(),Nice);
		this.name_detail_map.put(System.selfName(),System);
		this.name_detail_map.put(Wait.selfName(),Wait);
		this.name_detail_map.put(Steal.selfName(),Steal);
		this.name_detail_map.put(Idle.selfName(),Idle);
		this.name_detail_map.put(Speed.selfName(),Speed);
	}
}
