package com.bbz.bigdata.platform;

import com.bbz.bigdata.platform.bean.Cluster;
import com.bbz.bigdata.platform.bean.User;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import java.util.Date;

/**
 * Created by liu_k on 2016/4/15.
 */
public class MainSetup implements Setup{
    public void init( NutConfig conf ){
        Ioc ioc = conf.getIoc();
        Dao dao = ioc.get( Dao.class );
        Daos.createTablesInPackage( dao, "com.bbz.bigdata.platform.bean", false );

        if( dao.count( Cluster.class ) == 0 ) {
            Cluster cluster = new Cluster();
            cluster.setCreateTime( new Date() );
            cluster.setDescription( "运行在虚拟机上用于学习的集群" );
            cluster.setIp( "192.168.1.5" );
            cluster.setName( "测试集群A" );

            dao.insert( cluster );
        }
        if( dao.count( User.class ) == 0 ) {
            User user = new User();
            user.setName( "admin" );
            user.setPassword( "123456" );
            user.setCreateTime( new Date() );
            user.setUpdateTime( new Date() );
            dao.insert( user );
        }

        ioc.get( NutQuartzCronJobFactory.class );
    }

    public void destroy( NutConfig conf ){

    }
}
