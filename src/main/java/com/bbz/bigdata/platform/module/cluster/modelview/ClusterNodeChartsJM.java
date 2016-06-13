package com.bbz.bigdata.platform.module.cluster.modelview;

import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import lombok.Data;

/**
 *  集合概况的一些统计数字
 */

@Data
public class ClusterNodeChartsJM {

    private RRDJsonModel cpu;
    private RRDJsonModel mem;
    private RRDJsonModel disk;
    private RRDJsonModel network;

}
