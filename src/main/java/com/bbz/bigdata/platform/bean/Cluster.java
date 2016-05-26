package com.bbz.bigdata.platform.bean;

import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by liu_k on 2016/5/11.
 * Cluster数据库映射类
 */
@Table("cluster")
public class Cluster extends BaseBean{
    @Id
    private int id;
    @Name
    @Column
    private String name;
    @Column
    private String ip;
    @Column
    private String description;

    @Many(target = ClusterNode.class, field = "clusterId")
    private List<ClusterNode> clusterNodes;

    public List<ClusterNode> getClusterNode() {
        return clusterNodes;
    }

    public void setClusterNodes(List<ClusterNode> pets) {
        this.clusterNodes = pets;
    }


    @Comment("集群中运行的服务，用逗号分割")
    @Column
    private String service;//集群内运行的所有服务，用逗号分隔

    public int getId(){
        return id;
    }

    public void setId( int id ){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public String getIp(){
        return ip;
    }

    public void setIp( String ip ){
        this.ip = ip;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription( String description ){
        this.description = description;
    }



    public String getService(){
        return service;
    }

    public void setService( String service ){
        this.service = service;
    }
}
