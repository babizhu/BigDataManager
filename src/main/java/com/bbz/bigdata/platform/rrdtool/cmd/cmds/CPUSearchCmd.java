package com.bbz.bigdata.platform.rrdtool.cmd.cmds;

import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.cmd.ICmd;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.FullJsonModel;

import java.util.Collection;

public class CPUSearchCmd implements ICmd{

	public CPUSearchCmd(String hostName,String startTime,String endTime){
		this.cmdStr=Constant.rrdToolLocation
			+ " xport --start '"+startTime+"' --end '"+endTime
			+ "' DEF:'num_nodes'='"+Constant.rrdDataLocation+hostName+"/cpu_user.rrd':'num':AVERAGE"
			+ " DEF:'cpu_user'='"+Constant.rrdDataLocation+hostName+"/cpu_user.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_nice'='"+Constant.rrdDataLocation+hostName+"/cpu_nice.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_system'='"+Constant.rrdDataLocation+hostName+"/cpu_system.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_idle'='"+Constant.rrdDataLocation+hostName+"/cpu_idle.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_wio'='"+Constant.rrdDataLocation+hostName+"/cpu_wio.rrd':'sum':AVERAGE"
			+ " DEF:'cpu_steal'='"+Constant.rrdDataLocation+hostName+"/cpu_steal.rrd':'sum':AVERAGE"
			+ " CDEF:'ccpu_user'=cpu_user,num_nodes,/"
			+ " CDEF:'ccpu_nice'=cpu_nice,num_nodes,/"
			+ " CDEF:'ccpu_system'=cpu_system,num_nodes,/"
			+ " CDEF:'ccpu_idle'=cpu_idle,num_nodes,/"
			+ " CDEF:'ccpu_wio'=cpu_wio,num_nodes,/"
			+ " CDEF:'ccpu_steal'=cpu_steal,num_nodes,/"
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
	public void handleToPercent(FullJsonModel jsonModel, Collection<String> seleteFullNames) throws BussException {
		// need do nothing
	}

	
}
