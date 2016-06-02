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

public class MemorySearchCmd implements ICmd{

	public MemorySearchCmd(String clusterName, String hostName,String startTime,String endTime){
		String dataDir=Constant.rrdDataLocation+clusterName+"/"+hostName;
		this.cmdStr=Constant.rrdToolLocation
			+" xport --start '"+startTime+"' --end '"+endTime
			+"' DEF:'mem_total'='"+dataDir+"/mem_total.rrd':'sum':AVERAGE"
			+" CDEF:'bmem_total'=mem_total,1024,*"
		    +" DEF:'mem_shared'='"+dataDir+"/mem_shared.rrd':'sum':AVERAGE"
			+" CDEF:'bmem_shared'=mem_shared,1024,*"
			+" DEF:'mem_free'='"+dataDir+"/mem_free.rrd':'sum':AVERAGE"
			+" CDEF:'bmem_free'=mem_free,1024,*"
			+" DEF:'mem_cached'='"+dataDir+"/mem_cached.rrd':'sum':AVERAGE"
			+" CDEF:'bmem_cached'=mem_cached,1024,*" 
			+" DEF:'mem_buffers'='"+dataDir+"/mem_buffers.rrd':'sum':AVERAGE"
			+" CDEF:'bmem_buffers'=mem_buffers,1024,*"
			+" CDEF:'bmem_used'='bmem_total','bmem_free',-,'bmem_cached',-,'bmem_shared',-,'bmem_buffers',-"
			+" AREA:'bmem_used'#5555cc:'Use\\g'"
			+" CDEF:used_pos=bmem_used,0,INF,LIMIT"
			+" VDEF:used_last=used_pos,LAST"
			+" VDEF:used_min=used_pos,MINIMUM"
			+" VDEF:used_avg=used_pos,AVERAGE"
			+" VDEF:used_max=used_pos,MAXIMUM"
			+" GPRINT:'used_last':'    Now\\:%6.1lf%s'"
			+" GPRINT:'used_min':' Min\\:%6.1lf%s'"
			+" GPRINT:'used_avg':'Avg\\:%6.1lf%s'"
			+" GPRINT:'used_max':' Max\\:%6.1lf%s\\l'"
			+" STACK:'bmem_shared'#0000aa:'Share\\g'"
			+" CDEF:shared_pos=bmem_shared,0,INF,LIMIT"
			+" VDEF:shared_last=shared_pos,LAST"
			+" VDEF:shared_min=shared_pos,MINIMUM"
			+" VDEF:shared_avg=shared_pos,AVERAGE"
			+" VDEF:shared_max=shared_pos,MAXIMUM"
			+" GPRINT:'shared_last':'  Now\\:%6.1lf%s'"
			+" GPRINT:'shared_min':' Min\\:%6.1lf%s'"
			+" GPRINT:'shared_avg':'Avg\\:%6.1lf%s'"
			+" GPRINT:'shared_max':' Max\\:%6.1lf%s\\l'"
			+" STACK:'bmem_cached'#33cc33:'Cache\\g'"
			+" CDEF:cached_pos=bmem_cached,0,INF,LIMIT"
			+" VDEF:cached_last=cached_pos,LAST"
			+" VDEF:cached_min=cached_pos,MINIMUM"
			+" VDEF:cached_avg=cached_pos,AVERAGE"
			+" VDEF:cached_max=cached_pos,MAXIMUM"
			+" GPRINT:'cached_last':'  Now\\:%6.1lf%s'"
			+" GPRINT:'cached_min':' Min\\:%6.1lf%s'"
			+" GPRINT:'cached_avg':'Avg\\:%6.1lf%s'"
			+" GPRINT:'cached_max':' Max\\:%6.1lf%s\\l'"
			+" STACK:'bmem_buffers'#99ff33:'Buffer\\g'"
			+" CDEF:buffers_pos=bmem_buffers,0,INF,LIMIT"
			+" VDEF:buffers_last=buffers_pos,LAST"
			+" VDEF:buffers_min=buffers_pos,MINIMUM"
			+" VDEF:buffers_avg=buffers_pos,AVERAGE"
			+" VDEF:buffers_max=buffers_pos,MAXIMUM"
			+" GPRINT:'buffers_last':' Now\\:%6.1lf%s'"
			+" GPRINT:'buffers_min':' Min\\:%6.1lf%s'"
			+" GPRINT:'buffers_avg':'Avg\\:%6.1lf%s'"
			+" GPRINT:'buffers_max':' Max\\:%6.1lf%s\\l'"
			+" STACK:'bmem_free'#f0ffc0:'Free\\g'"
			+" CDEF:free_pos=bmem_free,0,INF,LIMIT"
			+" VDEF:free_last=free_pos,LAST"
			+" VDEF:free_min=free_pos,MINIMUM"
			+" VDEF:free_avg=free_pos,AVERAGE"
			+" VDEF:free_max=free_pos,MAXIMUM"
			+" GPRINT:'free_last':'  Now\\:%6.1lf%s'"
			+" GPRINT:'free_min':' Min\\:%6.1lf%s'"
			+" GPRINT:'free_avg':'Avg\\:%6.1lf%s'"
			+" GPRINT:'free_max':' Max\\:%6.1lf%s\\l'"
			+" DEF:'swap_total'='"+dataDir+"/swap_total.rrd':'sum':AVERAGE"
			+" DEF:'swap_free'='"+dataDir+"/swap_free.rrd':'sum':AVERAGE"
			+" CDEF:'bmem_swapped'='swap_total','swap_free',-,1024,*"
			+" STACK:'bmem_swapped'#9900CC:'Swap\\g'"
			+" CDEF:swapped_pos=bmem_swapped,0,INF,LIMIT"
			+" VDEF:swapped_last=swapped_pos,LAST"
			+" VDEF:swapped_min=swapped_pos,MINIMUM"
			+" VDEF:swapped_avg=swapped_pos,AVERAGE"
			+" VDEF:swapped_max=swapped_pos,MAXIMUM"
			+" GPRINT:'swapped_last':'   Now\\:%6.1lf%s'"
			+" GPRINT:'swapped_min':' Min\\:%6.1lf%s'"
			+" GPRINT:'swapped_avg':'Avg\\:%6.1lf%s'"
			+" GPRINT:'swapped_max':' Max\\:%6.1lf%s\\l'"
			+" LINE2:'bmem_total'#FF0000:'Total\\g'"
			+" CDEF:total_pos=bmem_total,0,INF,LIMIT"
			+" VDEF:total_last=total_pos,LAST"
			+" VDEF:total_min=total_pos,MINIMUM"
			+" VDEF:total_avg=total_pos,AVERAGE"
			+" VDEF:total_max=total_pos,MAXIMUM"
			+" GPRINT:'total_last':'  Now\\:%6.1lf%s'"
			+" GPRINT:'total_min':' Min\\:%6.1lf%s'"
			+" GPRINT:'total_avg':'Avg\\:%6.1lf%s'"
			+" GPRINT:'total_max':' Max\\:%6.1lf%s\\l'"
			+" XPORT:'bmem_used':'Use\\g'"
			+" XPORT:'bmem_shared':'Share\\g'"
			+" XPORT:'bmem_cached':'Cache\\g'"
			+" XPORT:'bmem_buffers':'Buffer\\g'"
			+" XPORT:'bmem_free':'Free\\g'"
			+" XPORT:'bmem_swapped':'Swap\\g'"
			+" XPORT:'bmem_total':'Total\\g'";
	}
	
	private String cmdStr;
	
	@Override
	public String getCmd() {
		return cmdStr;
	}

	@Override
	public Measurement measurement() {
		return Metrics.Memory;
	}

	@Override
	public boolean canChangeToPercent() {
		return true;
	}
	
	@Override
	public void handleToPercent(RRDJsonModel jsonModel, Collection<String> seleteFullNames) throws BussException{
		ICmd.handleToPercent(jsonModel, seleteFullNames, Metrics.Memory.Total.fullName());
	}

	@Override
	public boolean hasTotal() {return true;}

	@Override
	public void handleTotal(RRDJsonModel jsonModel, Unit showUnit) throws BussException {
		ICmd.handleTotal(jsonModel,Metrics.Memory.Total.fullName(), showUnit,measurement().getResultUnit());
	}
}
