package com.bbz.bigdata.platform.module;

import com.bbz.bigdata.platform.bean.User;
import com.bbz.bigdata.platform.bean.UserProfile;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

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

    @At
    @Aop(TransAop.READ_COMMITTED)
    public Object delete(@Param("id")int id, @Attr("me")int me) {
        if (me == id) {
            return new NutMap().setv("ok", false).setv("msg", "不能删除当前用户!!");
        }
        dao.delete(User.class, id); // 再严谨一些的话,需要判断是否为>0
        dao.clear(UserProfile.class, Cnd.where("userId", "=", me));
        return new NutMap().setv("ok", true);
    }
}
