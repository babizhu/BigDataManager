package com.bbz.bigdata.platform.rrdtool.cmd;


import com.bbz.bigdata.platform.rrdtool.cmd.cmds.CPUSearchCmd;
import com.bbz.bigdata.platform.rrdtool.cmd.cmds.DiskSearchCmd;
import com.bbz.bigdata.platform.rrdtool.cmd.cmds.MemorySearchCmd;
import com.bbz.bigdata.platform.rrdtool.cmd.cmds.NetworkSearchCmd;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;

public class CmdBuilder {
	
	/**
	 * 创建cmd命令服务类
	 * @param clusterName          集群名称
	 * @param hostName 主机名称，空字符串表示sumarryinfo
	 * @param measurement  测量详细量数组 [Metrics.CPU.Free,Metrics.Memory.Total]
	 * @param startTime 开始时间 MM/dd/yyyy HH:mm
	 * @param endTime 结束时间 MM/dd/yyyy HH:mm
	 * @return 查询指令对象
	 */
	public static ICmd buildCmd(String clusterName, String hostName, Measurement measurement, String startTime, String endTime){
		if(hostName==null||hostName.isEmpty()){
			hostName="__SummaryInfo__";
		}else{
			hostName=hostName.toLowerCase();
		}
		if (measurement==Metrics.Memory) {
			return new MemorySearchCmd(clusterName, hostName, startTime, endTime);
		}else if (measurement==Metrics.CPU) {
			if(hostName.equals("__SummaryInfo__")) {
				return new CPUSearchCmd(clusterName, startTime, endTime);
			}else{
				return new CPUSearchCmd(clusterName, hostName, startTime, endTime);
			}
		}else if (measurement==Metrics.Network) {
			return new NetworkSearchCmd(clusterName, hostName, startTime, endTime);
		}else if (measurement==Metrics.Disk) {
			return new DiskSearchCmd(clusterName, hostName, startTime, endTime);
		}else {
			return null;
		}
	}
}
