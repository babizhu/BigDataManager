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

public class CPUSearchCmd implements ICmd{

	public CPUSearchCmd(String clusterName, String startTime,String endTime){
		String dataDir=Constant.rrdDataLocation+clusterName+"/__SummaryInfo__";
		this.cmdStr=Constant.rrdToolLocation
			+ " xport --start '"+startTime+"' --end '"+endTime
			+ "' DEF:'num_nodes'='"+dataDir+"/cpu_user.rrd':'num':AVERAGE"
			+ " DEF:'cpu_user'='"+dataDir+"/cpu_user.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_nice'='"+dataDir+"/cpu_nice.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_system'='"+dataDir+"/cpu_system.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_idle'='"+dataDir+"/cpu_idle.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_wio'='"+dataDir+"/cpu_wio.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_steal'='"+dataDir+"/cpu_steal.rrd':'sum':AVERAGE"
//			+ " DEF:'cpu_speed'='"+dataDir+"/cpu_speed.rrd':'sum':AVERAGE"
			+ " CDEF:'ccpu_user'=cpu_user,num_nodes,/"
			+ " CDEF:'ccpu_nice'=cpu_nice,num_nodes,/"
			+ " CDEF:'ccpu_system'=cpu_system,num_nodes,/"
			+ " CDEF:'ccpu_idle'=cpu_idle,num_nodes,/"
			+ " CDEF:'ccpu_wio'=cpu_wio,num_nodes,/"
			+ " CDEF:'ccpu_steal'=cpu_steal,num_nodes,/"
//			+ " CDEF:'ccpu_speed'=cpu_speed,num_nodes,/"
			+ " AREA:'ccpu_user'#3333bb:'User\\g'"
			+ " CDEF:user_pos=ccpu_user,0,INF,LIMIT"
			+ " VDEF:user_last=user_pos,LAST"
			+ " VDEF:user_min=user_pos,MINIMUM"
			+ " VDEF:user_avg=user_pos,AVERAGE"
			+ " VDEF:user_max=user_pos,MAXIMUM"
			+ " GPRINT:'user_last':' Now\\:%5.1lf%%'"
			+ " GPRINT:'user_min':' Min\\:%5.1lf%%'"
			+ " GPRINT:'user_avg':'Avg\\:%5.1lf%%'"
			+ " GPRINT:'user_max':' Max\\:%5.1lf%%\\l'"
			+ " STACK:'ccpu_nice'#ffea00:'Nice\\g'"
			+ " CDEF:nice_pos=ccpu_nice,0,INF,LIMIT"
			+ " VDEF:nice_last=nice_pos,LAST"
			+ " VDEF:nice_min=nice_pos,MINIMUM"
			+ " VDEF:nice_avg=nice_pos,AVERAGE"
			+ " VDEF:nice_max=nice_pos,MAXIMUM"
			+ " GPRINT:'nice_last':' Now\\:%5.1lf%%'"
			+ " GPRINT:'nice_min':' Min\\:%5.1lf%%'"
			+ " GPRINT:'nice_avg':'Avg\\:%5.1lf%%'"
			+ " GPRINT:'nice_max':' Max\\:%5.1lf%%\\l'"
			+ " STACK:'ccpu_system'#dd0000:'System\\g'"
			+ " CDEF:system_pos=ccpu_system,0,INF,LIMIT"
			+ " VDEF:system_last=system_pos,LAST"
			+ " VDEF:system_min=system_pos,MINIMUM"
			+ " VDEF:system_avg=system_pos,AVERAGE"
			+ " VDEF:system_max=system_pos,MAXIMUM"
			+ " GPRINT:'system_last':' Now\\:%5.1lf%%'"
			+ " GPRINT:'system_min':' Min\\:%5.1lf%%'"
			+ " GPRINT:'system_avg':'Avg\\:%5.1lf%%'"
			+ " GPRINT:'system_max':' Max\\:%5.1lf%%\\l'"
			+ " STACK:'ccpu_wio'#ff8a60:'Wait\\g'"
			+ " CDEF:wio_pos=ccpu_wio,0,INF,LIMIT"
			+ " VDEF:wio_last=wio_pos,LAST"
			+ " VDEF:wio_min=wio_pos,MINIMUM"
			+ " VDEF:wio_avg=wio_pos,AVERAGE"
			+ " VDEF:wio_max=wio_pos,MAXIMUM"
			+ " GPRINT:'wio_last':' Now\\:%5.1lf%%'"
			+ " GPRINT:'wio_min':' Min\\:%5.1lf%%'"
			+ " GPRINT:'wio_avg':'Avg\\:%5.1lf%%'"
			+ " GPRINT:'wio_max':' Max\\:%5.1lf%%\\l'"
			+ " STACK:'ccpu_steal'#990099:'Steal\\g'"
			+ " CDEF:steal_pos=ccpu_steal,0,INF,LIMIT"
			+ " VDEF:steal_last=steal_pos,LAST"
			+ " VDEF:steal_min=steal_pos,MINIMUM"
			+ " VDEF:steal_avg=steal_pos,AVERAGE"
			+ " VDEF:steal_max=steal_pos,MAXIMUM"
			+ " GPRINT:'steal_last':' Now\\:%5.1lf%%'"
			+ " GPRINT:'steal_min':' Min\\:%5.1lf%%'"
			+ " GPRINT:'steal_avg':'Avg\\:%5.1lf%%'"
			+ " GPRINT:'steal_max':' Max\\:%5.1lf%%\\l'"
			+ " STACK:'ccpu_idle'#e2e2f2:'Idle\\g'"
			+ " CDEF:idle_pos=ccpu_idle,0,INF,LIMIT"
			+ " VDEF:idle_last=idle_pos,LAST"
			+ " VDEF:idle_min=idle_pos,MINIMUM"
			+ " VDEF:idle_avg=idle_pos,AVERAGE"
			+ " VDEF:idle_max=idle_pos,MAXIMUM"
			+ " GPRINT:'idle_last':' Now\\:%5.1lf%%'"
			+ " GPRINT:'idle_min':' Min\\:%5.1lf%%'"
			+ " GPRINT:'idle_avg':'Avg\\:%5.1lf%%'"
			+ " GPRINT:'idle_max':' Max\\:%5.1lf%%\\l'"
			+ " XPORT:'ccpu_user':'User\\g'"
			+ " XPORT:'ccpu_nice':'Nice\\g'"
			+ " XPORT:'ccpu_system':'System\\g'"
			+ " XPORT:'ccpu_wio':'Wait\\g'"
			+ " XPORT:'ccpu_steal':'Steal\\g'"
			+ " XPORT:'ccpu_idle':'Idle\\g'";
	}

	public CPUSearchCmd(String clusterName, String hostName,String startTime,String endTime){
		String dataDir=Constant.rrdDataLocation+clusterName+"/"+hostName;
		this.cmdStr=Constant.rrdToolLocation
				+ " xport --start '"+startTime+"' --end '"+endTime
				+ "' DEF:'cpu_user'='"+dataDir+"/cpu_user.rrd':'sum':AVERAGE" +
				" DEF:'cpu_nice'='"+dataDir+"/cpu_nice.rrd':'sum':AVERAGE" +
				" DEF:'cpu_system'='"+dataDir+"/cpu_system.rrd':'sum':AVERAGE" +
				" DEF:'cpu_idle'='"+dataDir+"/cpu_idle.rrd':'sum':AVERAGE" +
				" DEF:'cpu_wio'='"+dataDir+"/cpu_wio.rrd':'sum':AVERAGE" +
				" DEF:'cpu_steal'='"+dataDir+"/cpu_steal.rrd':'sum':AVERAGE" +
				" DEF:'cpu_speed'='"+dataDir+"/cpu_speed.rrd':'sum':AVERAGE" +
				" AREA:'cpu_user'#3333bb:'User\\g'" +
				" CDEF:user_pos=cpu_user,0,INF,LIMIT" +
				" VDEF:user_last=user_pos,LAST" +
				" VDEF:user_min=user_pos,MINIMUM" +
				" VDEF:user_avg=user_pos,AVERAGE" +
				" VDEF:user_max=user_pos,MAXIMUM" +
				" GPRINT:'user_last':' Now\\:%5.1lf%%'" +
				" GPRINT:'user_min':' Min\\:%5.1lf%%'" +
				" GPRINT:'user_avg':' Avg\\:%5.1lf%%'" +
				" GPRINT:'user_max':' Max\\:%5.1lf%%\\l'" +
				" STACK:'cpu_nice'#ffea00:'Nice\\g'" +
				" CDEF:nice_pos=cpu_nice,0,INF,LIMIT" +
				" VDEF:nice_last=nice_pos,LAST" +
				" VDEF:nice_min=nice_pos,MINIMUM" +
				" VDEF:nice_avg=nice_pos,AVERAGE" +
				" VDEF:nice_max=nice_pos,MAXIMUM" +
				" GPRINT:'nice_last':' Now\\:%5.1lf%%'" +
				" GPRINT:'nice_min':' Min\\:%5.1lf%%'" +
				" GPRINT:'nice_avg':' Avg\\:%5.1lf%%'" +
				" GPRINT:'nice_max':' Max\\:%5.1lf%%\\l'" +
				" STACK:'cpu_system'#dd0000:'System\\g'" +
				" CDEF:system_pos=cpu_system,0,INF,LIMIT" +
				" VDEF:system_last=system_pos,LAST" +
				" VDEF:system_min=system_pos,MINIMUM" +
				" VDEF:system_avg=system_pos,AVERAGE" +
				" VDEF:system_max=system_pos,MAXIMUM" +
				" GPRINT:'system_last':' Now\\:%5.1lf%%'" +
				" GPRINT:'system_min':' Min\\:%5.1lf%%'" +
				" GPRINT:'system_avg':' Avg\\:%5.1lf%%'" +
				" GPRINT:'system_max':' Max\\:%5.1lf%%\\l'" +
				" STACK:'cpu_wio'#ff8a60:'Wait\\g'" +
				" CDEF:wio_pos=cpu_wio,0,INF,LIMIT" +
				" VDEF:wio_last=wio_pos,LAST" +
				" VDEF:wio_min=wio_pos,MINIMUM" +
				" VDEF:wio_avg=wio_pos,AVERAGE" +
				" VDEF:wio_max=wio_pos,MAXIMUM" +
				" GPRINT:'wio_last':' Now\\:%5.1lf%%'" +
				" GPRINT:'wio_min':' Min\\:%5.1lf%%'" +
				" GPRINT:'wio_avg':' Avg\\:%5.1lf%%'" +
				" GPRINT:'wio_max':' Max\\:%5.1lf%%\\l'" +
				" STACK:'cpu_steal'#990099:'Steal\\g'" +
				" CDEF:steal_pos=cpu_steal,0,INF,LIMIT" +
				" VDEF:steal_last=steal_pos,LAST" +
				" VDEF:steal_min=steal_pos,MINIMUM" +
				" VDEF:steal_avg=steal_pos,AVERAGE" +
				" VDEF:steal_max=steal_pos,MAXIMUM" +
				" GPRINT:'steal_last':' Now\\:%5.1lf%%'" +
				" GPRINT:'steal_min':' Min\\:%5.1lf%%'" +
				" GPRINT:'steal_avg':' Avg\\:%5.1lf%%'" +
				" GPRINT:'steal_max':' Max\\:%5.1lf%%\\l'" +
				" STACK:'cpu_idle'#e2e2f2:'Idle\\g'" +
				" CDEF:idle_pos=cpu_idle,0,INF,LIMIT" +
				" VDEF:idle_last=idle_pos,LAST" +
				" VDEF:idle_min=idle_pos,MINIMUM" +
				" VDEF:idle_avg=idle_pos,AVERAGE" +
				" VDEF:idle_max=idle_pos,MAXIMUM" +
				" GPRINT:'idle_last':' Now\\:%5.1lf%%'" +
				" GPRINT:'idle_min':' Min\\:%5.1lf%%'" +
				" GPRINT:'idle_avg':' Avg\\:%5.1lf%%'" +
				" GPRINT:'idle_max':' Max\\:%5.1lf%%\\l'" +
				" STACK:'cpu_speed'#bb3344:'Speed\\g'" +
				" CDEF:speed_pos=cpu_speed,0,INF,LIMIT" +
				" VDEF:speed_last=speed_pos,LAST" +
				" VDEF:speed_min=speed_pos,MINIMUM" +
				" VDEF:speed_avg=speed_pos,AVERAGE" +
				" VDEF:speed_max=speed_pos,MAXIMUM" +
				" GPRINT:'speed_last':' Now\\:%5.1lf%%'" +
				" GPRINT:'speed_min':' Min\\:%5.1lf%%'" +
				" GPRINT:'speed_avg':' Avg\\:%5.1lf%%'" +
				" GPRINT:'speed_max':' Max\\:%5.1lf%%\\l'" +
				" XPORT:'cpu_user':'User\\g'" +
				" XPORT:'cpu_nice':'Nice\\g'" +
				" XPORT:'cpu_system':'System\\g'" +
				" XPORT:'cpu_wio':'Wait\\g'" +
				" XPORT:'cpu_steal':'Steal\\g'" +
				" XPORT:'cpu_idle':'Idle\\g'" +
				" XPORT:'cpu_speed':'Speed\\g'";
	}
	private String cmdStr;
	
	@Override
	public boolean canChangeToPercent() {
		return true;
	}

	@Override
	public String getCmd() {
		return cmdStr;
	}

	@Override
	public Measurement measurement() {
		return Metrics.CPU;
	}

	@Override
	public void handleToPercent(RRDJsonModel jsonModel, Collection<String> seleteFullNames) throws BussException {
		// need do nothing
	}

	@Override
	public boolean hasTotal() {return false;}

	@Override
	public void handleTotal(RRDJsonModel jsonModel, Unit showUnit) throws BussException {
		//need do noting
	}
}
