package com.bbz.bigdata.platform.service;

import com.bbz.bigdata.platform.bean.User;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.service.IdNameEntityService;

import java.util.Date;

/**
 * Created by liulaoye on 16-8-16.
 * UserService
 */
@IocBean(fields = "dao")
public class UserService extends IdNameEntityService<User>{

    public User add( String name, String password ){
        User user = new User();
        user.setName( name.trim() );
        user.setSalt( R.UU16() );
        user.setPassword( new Sha256Hash( password, user.getSalt() ).toHex() );
        user.setCreateTime( new Date() );
        user.setUpdateTime( new Date() );

        return dao().insert( user );
    }

    public int fetch( String username, String password ){
        User user = fetch( username );
        if( user == null ) {
            return -1;
        }
        String _pass = new Sha256Hash( password, user.getSalt() ).toHex();
        if( _pass.equalsIgnoreCase( user.getPassword() ) ) {
            return user.getId();
        }
        return -1;
    }

    public void updatePassword( int userId, String password ){
        User user = fetch( userId );
        if( user == null ) {
            return;
        }
        user.setSalt( R.UU16() );
        user.setPassword( new Sha256Hash( password, user.getSalt() ).toHex() );
        user.setUpdateTime( new Date() );
        dao().update( user, "^(password|salt|updateTime)$" );
    }
}
