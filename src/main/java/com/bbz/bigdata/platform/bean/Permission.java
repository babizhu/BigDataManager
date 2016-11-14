package com.bbz.bigdata.platform.bean;

import org.nutz.dao.entity.annotation.*;

/**
 * Created by liulaoye on 16-8-12.
 * 权限
 *
 */
public class Permission extends BaseBean{

    @Id
    private long id;
    @Name
    private String name;
    @Column("al")
    private String alias;
    @Column("dt")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String description;

    public long getId(){
        return id;
    }

    public void setId( long id ){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public String getAlias(){
        return alias;
    }

    public void setAlias( String alias ){
        this.alias = alias;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription( String description ){
        this.description = description;
    }
}