package com.bbz.bigdata.platform.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import java.util.Date;

/**
 * Created by liu_k on 2016/5/26.
 */
public class BaseBean{
    @Column("createTime")
    protected Date createTime;
    @Column("updateTime")
    protected Date updateTime;

    public String toString() {
        return String.format("/*%s*/%s", super.toString(), Json.toJson(this, JsonFormat.compact()));
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

