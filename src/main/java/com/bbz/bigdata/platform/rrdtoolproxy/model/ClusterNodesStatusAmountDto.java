package com.bbz.bigdata.platform.rrdtoolproxy.model;

import lombok.Data;

/**
 * 集群节点状态数量统计结果对象
 */
@Data
public class ClusterNodesStatusAmountDto {

	private int totalAmount;
	private int aliveAmount;
	private int deadAmount;
	
}
