package com.bbz.bigdata.platform.rrdtool.cmd.cmds.hdfs;

import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;

import java.util.Collection;

public class HDSFCapacityCmd implements ICmd{

	public HDSFCapacityCmd(String clusterName, String hostName, String startTime, String endTime){
		String dataDir=Constant.rrdDataLocation+clusterName+"/"+hostName;
		this.cmdStr=Constant.rrdToolLocation
				+ " xport --start '"+startTime+"' --end '"+endTime
				+ "' DEF:'a0'='"+dataDir+"/dfs.FSNamesystem.CapacityTotalGB.rrd':'sum':AVERAGE"
				+ " DEF:'a1'='"+dataDir+"/dfs.FSNamesystem.CapacityRemainingGB.rrd':'sum':AVERAGE"
				+ " DEF:'a2'='"+dataDir+"/dfs.FSNamesystem.CapacityUsedGB.rrd':'sum':AVERAGE"
				+ " XPORT:'a0':'Total'"
				+ " XPORT:'a1':'Remaining'"
				+ " XPORT:'a2':'Used'";
	}

	private String cmdStr;
	
	@Override
	public String getCmd() {
		return cmdStr;
	}

	@Override
	public Measurement measurement() {
		return Metrics.Disk;
	}

	@Override
	public boolean canChangeToPercent() {
		return false;
	}
	
	@Override
	public void handleToPercent(RRDModel jsonModel, Collection<String> seleteFullNames) throws BussException{
//		ICmd.handleToPercent(jsonModel, seleteFullNames, Metrics.Disk.Total.fullName());
	}

	@Override
	public boolean hasTotal() {return true;}

	@Override
	public void handleTotal(RRDModel jsonModel, Unit showUnit) throws BussException {
		ICmd.handleTotal(jsonModel,Metrics.HDFSCapacity.Total.fullName(),showUnit,measurement().getResultUnit());
	}
}
