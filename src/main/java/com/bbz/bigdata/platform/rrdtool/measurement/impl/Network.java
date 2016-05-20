package com.bbz.bigdata.platform.rrdtool.measurement.impl;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;

import java.util.HashMap;
import java.util.Map;

public class Network extends Measurement{

	@Override
	public Unit getResultUnit() {
		return Unit.BytePerSecond;
	}

	@Override
	public String name() {
		return "Network";
	}

	@Override
	public Map<String,Detail> allDetails() {
		// TODO Auto-generated method stub
		return new HashMap<>(this.name_detail_map);
	}
	
	public Measurement.Detail In=this.new Detail(this,"In");
	public Measurement.Detail Out=this.new Detail(this,"Out");
	
	private HashMap<String, Detail> name_detail_map=new HashMap<>();
	
	public Network() {
		this.name_detail_map.put(In.selfName(),In);
		this.name_detail_map.put(Out.selfName(),Out);
	}
}
