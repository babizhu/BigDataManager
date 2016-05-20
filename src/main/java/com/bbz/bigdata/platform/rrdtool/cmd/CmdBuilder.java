package com.bbz.bigdata.platform.rrdtool.cmd;


import com.bbz.bigdata.platform.rrdtool.cmd.cmds.CPUSearchCmd;
import com.bbz.bigdata.platform.rrdtool.cmd.cmds.DiskSearchCmd;
import com.bbz.bigdata.platform.rrdtool.cmd.cmds.MemorySearchCmd;
import com.bbz.bigdata.platform.rrdtool.cmd.cmds.NetworkSearchCmd;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;

public class CmdBuilder {
	
	/**
	 * 创建cmd命令服务类
	 * @param hostName
	 * @param measurement
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static ICmd buildCmd( String hostName, Measurement measurement, String startTime, String endTime){
		if (measurement==Measurement.Memory) {
			return new MemorySearchCmd(hostName, startTime, endTime);
		}else if (measurement==Measurement.CPU) {
			return new CPUSearchCmd(hostName, startTime, endTime);
		}else if (measurement==Measurement.Network) {
			return new NetworkSearchCmd(hostName, startTime, endTime);
		}else if (measurement==Measurement.Disk) {
			return new DiskSearchCmd(hostName, startTime, endTime);
		}else {
			return null;
		}
	}
}
