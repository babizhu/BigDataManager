package com.bbz.bigdata.platform.module.modelview;

import com.bbz.bigdata.platform.bean.ClusterNode;
import com.bbz.bigdata.platform.rrdtool.Constant;
import lombok.Data;

/**
 * Created by weiran on 2016/6/3.
 */
@Data
public class ClusterNodeJM {

    public ClusterNodeJM(){}
    public ClusterNodeJM(ClusterNode node){
        this.id=node.getId();
        this.host=node.getHost();
        this.clusterId=node.getClusterId();
        this.ip=node.getIp();
        this.description=node.getDescription();
        this.service=node.getService();

        this.cpuUsedPercent=node.getCpuUsedPercent()==null?null:node.getCpuUsedPercent().setScale(0, Constant.roundingMode).intValue();
        this.memUsedPercent=node.getMemUsedPercent()==null?null:node.getMemUsedPercent().setScale(0, Constant.roundingMode).intValue();
        this.diskUsedPercent=node.getDiskUsedPercent()==null?null:node.getDiskUsedPercent().setScale(0, Constant.roundingMode).intValue();
        this.netIn=node.getNetIn()==null?null:node.getNetIn().setScale(0, Constant.roundingMode).intValue();
        this.netOut=node.getNetOut()==null?null:node.getNetOut().setScale(0, Constant.roundingMode).intValue();
        this.netUnit=node.getNetUnit();
    }

    private int id;
    private String host;
    private int clusterId;
    private String ip;
    private String description;
    private String service;

    private Integer cpuUsedPercent;
    private Integer memUsedPercent;
    private Integer diskUsedPercent;
    private Integer netIn;
    private Integer netOut;
    private String netUnit;
}
