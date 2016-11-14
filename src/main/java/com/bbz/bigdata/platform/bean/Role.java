package com.bbz.bigdata.platform.bean;

import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by liulaoye on 16-8-12.
 * 角色
 */
public class Role extends BaseBean {

    @Id
    private long id;
    @Name
    private String name;
    @Column("al")
    private String alias;
    @Column("dt")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String description;
    @ManyMany(from="role_id", relation="t_role_permission", target=Permission.class, to="permission_id")
    private List<Permission> permissions;

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

    public List<Permission> getPermissions(){
        return permissions;
    }

    public void setPermissions( List<Permission> permissions ){
        this.permissions = permissions;
    }
}
