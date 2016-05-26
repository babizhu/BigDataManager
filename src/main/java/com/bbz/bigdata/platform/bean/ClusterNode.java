package com.bbz.bigdata.platform.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

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



}
