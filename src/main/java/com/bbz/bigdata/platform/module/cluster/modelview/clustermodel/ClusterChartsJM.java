package com.bbz.bigdata.platform.module.cluster.modelview.clustermodel;

import com.bbz.bigdata.platform.module.cluster.modelview.rrdjsonmodel.RRDJsonModel;
import lombok.Data;

/**
 *  集合概况的一些统计数字
 */

@Data
public class ClusterChartsJM {

    private RRDJsonModel cpu;
    private RRDJsonModel mem;
    private RRDJsonModel disk;
    private RRDJsonModel network;

}
