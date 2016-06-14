package com.bbz.bigdata.platform.module.cluster.modelview.clustermodel;

import lombok.Data;

/**
 *  集合概况的一些统计数字
 */

@Data
public class ClusterSummaryJM {

    private ClusterChartsJM clusterCharts;
    private ClusterNodeListJM clusterNodeList;

}
