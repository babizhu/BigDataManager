package com.bbz.bigdata.platform.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * Created by liu_k on 2016/5/11.
 * Cluster数据库映射类
 */
@Table("cluster")
public class Cluster{
    @Id
    private int id;
    @Name
    @Column
    private String name;
    @Column
    private String ip;
    @Column
    private String description;
    @Column
    private Date createTime;

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

    public Date getCreateTime(){
        return createTime;
    }

    public void setCreateTime( Date createTime ){
        this.createTime = createTime;
    }
}
