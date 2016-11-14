package com.bbz.bigdata.platform.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import java.math.BigDecimal;

/**
 * Created by liu_k on 2016/5/26.
 * ClusterNode
 */
@Table("cluster_node")
@Data
@EqualsAndHashCode(callSuper = false)
public class ClusterNode extends BaseBean{
    @Id
    private int id;
    @Name
    @Column
    private String host;

    @Column
    private int clusterId;

    @Column
    private String ip;
    @Column
    private String description;
    @Column
    private String service;

    @Column
    private int type;//1为服务器，2为收银机

    private BigDecimal cpuTotal;
    private BigDecimal cpuUsed;
    private String cpuUnit;
    private BigDecimal cpuUsedPercent;
    private BigDecimal memTotal;
    private BigDecimal memUsed;
    private String memUnit;
    private BigDecimal memUsedPercent;
    private BigDecimal diskTotal;
    private BigDecimal diskUsed;
    private String diskUnit;
    private BigDecimal diskUsedPercent;
    private BigDecimal netIn;
    private BigDecimal netOut;
    private String netUnit;

    /**
     * 节点状态 0：宕机，1：正常
     */
    private int status;

    public final static int STATUS_DEAD=0;
    public final static int STATUS_ALIVE=1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
