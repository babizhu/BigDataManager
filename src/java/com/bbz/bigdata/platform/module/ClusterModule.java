package com.bbz.bigdata.platform.module;

import com.bbz.bigdata.platform.bean.Cluster;
import com.bbz.bigdata.util.Util;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by liu_k on 2016/5/11.
 * 集群相关操作
 */

@IocBean
@At("api/cluster")
@Ok("json")
@Fail("http:500")
public class ClusterModule{
    @Inject
    protected Dao dao;

    @At
    public int count(){
        System.out.println( dao.count( Cluster.class ) );
        return dao.count( Cluster.class );
//        return 10;
    }

    /**
     * 群数据操作（增删改）统一到这里处理
     *
     * @param op      操作类型1:增 2:、删除 3:改
     * @param cluster 当前要操作的集群
     */
    @At
    public Object operation( @Param("op") int op, @Param("..") Cluster cluster,
                             HttpServletResponse response ) throws IllegalAccessException{
        Object result = null;
        try {
            switch( op ) {
                case 1:
                    result = add( cluster );
                    break;
                case 2:
                    result = delete( cluster );
                    break;
                case 3:
                    result = update( cluster );
                    break;
                default:
                    throw new IllegalAccessException( "operation 不存在:" + op );
            }
            return result;

        } catch( Exception e ) {
            return Util.INSTANCE.buildErrorResponse( response, 500, e.getMessage() );
        }
//        return result;
    }

    private Object add( Cluster cluster ){
        NutMap re = new NutMap();
        String msg = checkCluster( cluster );
        if( msg != null ) {
            return re.setv( "ok", false ).setv( "msg", msg );
        }
        cluster.setCreateTime( new Date() );

        cluster = dao.insert( cluster );
        return re.setv( "ok", true ).setv( "data", cluster );
    }

    private Object update( Cluster cluster ){
        NutMap re = new NutMap();

        cluster.setName( null );// 不允许更新名
        cluster.setCreateTime( null );//也不允许更新创建时间
        dao.updateIgnoreNull( cluster );// 真正更新的其实只有description and ip
        return re.setv( "ok", true );
    }

    private Object delete( Cluster cluster ){

        dao.delete( Cluster.class, cluster.getId() ); // 再严谨一些的话,需要判断是否为>0
        return new NutMap().setv( "ok", true );
    }

    @At
    public Object query( @Param("name") String name, @Param("..") Pager pager ){
        Cnd cnd = Strings.isBlank( name ) ? null : Cnd.where( "name", "like", "%" + name + "%" );
        QueryResult qr = new QueryResult();
        qr.setList( dao.query( Cluster.class, cnd, pager ) );
        pager.setRecordCount( dao.count( Cluster.class, cnd ) );
        qr.setPager( pager );
        return qr; //默认分页是第1页,每页20条
    }

    private String checkCluster( Cluster cluster ){
        return null;
    }
}
