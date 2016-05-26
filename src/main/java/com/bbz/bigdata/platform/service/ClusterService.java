package com.bbz.bigdata.platform.service;

import com.bbz.bigdata.platform.bean.Cluster;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdNameEntityService;

/**
 * Created by liu_k on 2016/5/25.
 * cluster集群服务类
 */
@IocBean(fields="dao")
public class ClusterService extends IdNameEntityService<Cluster>{

    public QueryResult query( Cnd cnd, Pager pager ){
        QueryResult qr = new QueryResult();
//        qr.setList( dao().query( Cluster.class, cnd, pager ) );


        pager.setRecordCount( dao().count( Cluster.class, cnd ) );
        qr.setPager( pager );
        return qr;
    }

    public Cluster getClusterInfoWithNodes( int clusterId ){
        Cluster cluster = dao().fetchLinks( dao().fetch( Cluster.class,clusterId ), null );
        return cluster;
    }
    public int count(){
        System.out.println( dao().count( Cluster.class ) );
        return dao().count( Cluster.class );
    }

    public void add( Cluster cluster ){
        dao().insert( cluster );
    }

    public void updateIgnoreNull( Cluster cluster ){
        dao().updateIgnoreNull( cluster );
    }
}
