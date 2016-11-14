package com.bbz.bigdata.platform.bean;

import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by liulaoye on 16-7-28.
 * 用户信息
 */


@Table("t_user")
public class User extends BaseBean{

    @Id
    private int id;

    @Name
    @Column
    private String name;

    @Column("passwd")
    @ColDefine(width = 128)
    private String password;

    @Column
    private String salt;

    @Column
    private boolean locked;

    @ManyMany(from="u_id", relation="t_user_role", target=Role.class, to="role_id")
    private List<Role> roles;

    @ManyMany(from="u_id", relation="t_user_permission", target=Permission.class, to="permission_id")
    private List<Permission> permissions;

    @One(target=UserProfile.class, field="id", key="userId")
    private UserProfile profile;

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

    public String getPassword(){
        return password;
    }

    public void setPassword( String password ){
        this.password = password;
    }

    public String getSalt(){
        return salt;
    }

    public void setSalt( String salt ){
        this.salt = salt;
    }

    public UserProfile getProfile(){
        return profile;
    }

    public void setProfile( UserProfile profile ){
        this.profile = profile;
    }
}
