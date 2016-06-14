package com.bbz.bigdata.platform.module.cluster.modelview.clustermodel;

import lombok.Data;

import java.util.Collection;

/**
 *  集合概况的一些统计数字
 */

@Data
public class ClusterNodeListJM {

    private Collection<ClusterNodeJM> nodeList;

    private int totalCount;
    private int aliveCount;
//    private int deadCount;
//    private int taskTotalCount;
//    private int taskSuccessCount;
//    private int taskFailCount;
//    private int taskRunningCount;
//    private int serviceTotalCount;
//    private int serviceRunningCount;
//    private int serviceStopCount;
}
