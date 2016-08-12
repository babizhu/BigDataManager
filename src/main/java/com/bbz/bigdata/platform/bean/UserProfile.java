package com.bbz.bigdata.platform.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

/**
 * Created by liulaoye on 16-8-12.
 * 用户的详细信息
 */
@Table("t_user_profile")
public class UserProfile extends BaseBean {

    /**关联的用户id*/
    @Id(auto=false)
    @Column("uid")
    private int userId;
    /**用户昵称*/
    @Column
    private String nickname;
    /**用户邮箱*/
    @Column
    private String email;
    /**邮箱是否已经验证过*/
    @Column("email_checked")
    private boolean emailChecked;
    /**头像的byte数据*/
    @Column
    @JsonField(ignore=true)
    private byte[] avatar;
    /**性别*/
    @Column
    private String gender;
    /**自我介绍*/
    @Column("dt")
    private String description;
    @Column("loc")
    private String location;

    public int getUserId(){
        return userId;
    }

    public void setUserId( int userId ){
        this.userId = userId;
    }

    public String getNickname(){
        return nickname;
    }

    public void setNickname( String nickname ){
        this.nickname = nickname;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail( String email ){
        this.email = email;
    }

    public boolean isEmailChecked(){
        return emailChecked;
    }

    public void setEmailChecked( boolean emailChecked ){
        this.emailChecked = emailChecked;
    }

    public byte[] getAvatar(){
        return avatar;
    }

    public void setAvatar( byte[] avatar ){
        this.avatar = avatar;
    }

    public String getGender(){
        return gender;
    }

    public void setGender( String gender ){
        this.gender = gender;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription( String description ){
        this.description = description;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation( String location ){
        this.location = location;
    }
}