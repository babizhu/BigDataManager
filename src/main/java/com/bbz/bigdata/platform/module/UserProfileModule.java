package com.bbz.bigdata.platform.module;

import com.bbz.bigdata.platform.bean.UserProfile;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import java.util.Date;

/**
 * Created by liulaoye on 16-8-12.
 * UserProfileModule
 */
@IocBean
@At("api/user/profile")
@Filters(@By(type = CheckSession.class, args = {"me", "/"})) // 检查当前Session是否带me这个属性
public class UserProfileModule extends BaseModule{

    @At
    public UserProfile get( @Attr(scope = Scope.SESSION, value = "me") int userId ){
        UserProfile profile = Daos.ext( dao, FieldFilter.locked( UserProfile.class, "avatar" ) ).fetch( UserProfile.class, userId );
        if( profile == null ) {
            profile = new UserProfile();
            profile.setUserId( userId );
            profile.setCreateTime( new Date() );
            profile.setUpdateTime( new Date() );
            dao.insert( profile );
        }
        return profile;
    }

    @At
    @AdaptBy(type = JsonAdaptor.class)
    @Ok("void")
    public void update( @Param("..") UserProfile profile, @Attr(scope = Scope.SESSION, value = "me") int userId ){
        if( profile == null )
            return;
        profile.setUserId( userId );//修正userId,防止恶意修改其他用户的信息
        profile.setUpdateTime( new Date() );
        profile.setAvatar( null ); // 不准通过这个方法更新
        UserProfile old = get( userId );
        // 检查email相关的更新
        if( old.getEmail() == null ) {
            // 老的邮箱为null,所以新的肯定是未check的状态
            profile.setEmailChecked( false );
        } else {
            if( profile.getEmail() == null ) {
                profile.setEmail( old.getEmail() );
                profile.setEmailChecked( old.isEmailChecked() );
            } else if( !profile.getEmail().equals( old.getEmail() ) ) {
                // 设置新邮箱,果断设置为未检查状态
                profile.setEmailChecked( false );
            } else {
                profile.setEmailChecked( old.isEmailChecked() );
            }
        }
        Daos.ext( dao, FieldFilter.create( UserProfile.class, null, "avatar", true ) ).update( profile );
    }
}
