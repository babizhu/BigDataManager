package com.bbz.bigdata.platform.module.modelview;

import com.bbz.bigdata.platform.bean.ClusterNode;
import com.bbz.bigdata.platform.rrdtool.Constant;
import lombok.Data;

import java.math.BigDecimal;

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
        this.status=node.getStatus();

        this.cpuUsedPercent=node.getCpuUsedPercent()==null?-1:node.getCpuUsedPercent().setScale(0, Constant.roundingMode).intValue();
        this.memUsedPercent=node.getMemUsedPercent()==null?-1:node.getMemUsedPercent().setScale(0, Constant.roundingMode).intValue();
        this.diskUsedPercent=node.getDiskUsedPercent()==null?-1:node.getDiskUsedPercent().setScale(0, Constant.roundingMode).intValue();
        this.netIn=node.getNetIn()==null?-1:node.getNetIn().setScale(0, Constant.roundingMode).intValue();
        this.netOut=node.getNetOut()==null?-1:node.getNetOut().setScale(0, Constant.roundingMode).intValue();
        this.netUnit=node.getNetUnit();
        this.cpuTotal=node.getCpuTotal();
        this.cpuUnit=node.getCpuUnit();
        this.memTotal=node.getMemTotal();
        this.memUnit=node.getMemUnit();
        this.diskTotal=node.getDiskTotal();
        this.diskUnit=node.getDiskUnit();
    }

    private int id;
    private String host;
    private int clusterId;
    private String ip;
    private String description;
    private String service;
    private int status;

    private Integer cpuUsedPercent;
    private BigDecimal cpuTotal;
    private String cpuUnit;
    private Integer memUsedPercent;
    private BigDecimal memTotal;
    private String memUnit;
    private Integer diskUsedPercent;
    private BigDecimal diskTotal;
    private String diskUnit;
    private Integer netIn;
    private Integer netOut;
    private String netUnit;

    private ClusterNodeChartsJM charts=new ClusterNodeChartsJM();

}
