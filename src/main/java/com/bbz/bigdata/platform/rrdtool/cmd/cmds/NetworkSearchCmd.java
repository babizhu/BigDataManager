package com.bbz.bigdata.platform.rrdtool.cmd.cmds;

import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;

import java.math.BigDecimal;
import java.util.Collection;

public class NetworkSearchCmd implements ICmd{

	public NetworkSearchCmd(String clusterName, String hostName,String startTime,String endTime){
		String dataDir=Constant.rrdDataLocation+clusterName+"/"+hostName;
		this.cmdStr=Constant.rrdToolLocation
				+ " xport --start '"+startTime+"' --end '"+endTime
				+ "' DEF:'a0'='"+dataDir+"/bytes_in.rrd':'sum':AVERAGE"
				+ " DEF:'a1'='"+dataDir+"/bytes_out.rrd':'sum':AVERAGE"
				+ " LINE2:'a0'#33cc33:'In '"
				+ " VDEF:a0_last=a0,LAST"
				+ " VDEF:a0_min=a0,MINIMUM"
				+ " VDEF:a0_avg=a0,AVERAGE"
				+ " VDEF:a0_max=a0,MAXIMUM"
				+ " GPRINT:'a0_last':'Now\\:%5.1lf%s'"
				+ " GPRINT:'a0_min':'Min\\:%5.1lf%s'"
				+ " GPRINT:'a0_avg':'Avg\\:%5.1lf%s'"
				+ " GPRINT:'a0_max':'Max\\:%5.1lf%s\\l'"
				+ " LINE2:'a1'#5555cc:'Out'"
				+ " VDEF:a1_last=a1,LAST"
				+ " VDEF:a1_min=a1,MINIMUM"
				+ " VDEF:a1_avg=a1,AVERAGE"
				+ " VDEF:a1_max=a1,MAXIMUM"
				+ " GPRINT:'a1_last':'Now\\:%5.1lf%s'"
				+ " GPRINT:'a1_min':'Min\\:%5.1lf%s'"
				+ " GPRINT:'a1_avg':'Avg\\:%5.1lf%s'"
				+ " GPRINT:'a1_max':'Max\\:%5.1lf%s\\l'"
				+ " XPORT:'a0':'In '"
				+ " XPORT:'a1':'Out'";
	}
	
	private String cmdStr;
	
	@Override
	public String getCmd() {
		return cmdStr;
	}

	@Override
	public Measurement measurement() {
		return Metrics.Network;
	}

	@Override
	public boolean canChangeToPercent() {
		return false;
	}
	
	@Override
	public void handleToPercent(RRDJsonModel jsonModel, Collection<String> seleteFullNames) throws BussException{
		throw new BussException(BussException.CAN_NOT_TO_PERCENT);
	}

	@Override
	public boolean hasTotal() {return false;}

	@Override
	public void handleTotal(RRDJsonModel jsonModel, Unit showUnit) throws BussException {
		//need to nothing
	}
}
