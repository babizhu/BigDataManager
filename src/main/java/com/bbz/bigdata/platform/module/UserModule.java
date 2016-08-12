package com.bbz.bigdata.platform.module;

import com.bbz.bigdata.platform.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpSession;

/**
 * Created by liulaoye on 16-7-28.
 * user 管理模块
 */

@IocBean
@At("/api/user")
@Ok("json")
@Fail("http:500")
public class UserModule extends BaseModule{
    @At
    public Object login( @Param("name") String name, @Param("passwd") String password, HttpSession session ){
        User user = dao.fetch( User.class, Cnd.where( "name", "=", name ).and( "password", "=", password ) );
        if( user == null ) {
            return false;
        } else {
            session.setAttribute( "me", user.getId() );
            return true;
        }
    }

    @At
    @Ok(">>:/")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
