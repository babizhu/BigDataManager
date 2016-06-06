package com.bbz.bigdata.platform.module;

import com.bbz.bigdata.platform.bean.Cluster;
import com.bbz.bigdata.platform.bean.ClusterNode;
import com.bbz.bigdata.platform.module.modelview.ClusterChartsJM;
import com.bbz.bigdata.platform.module.modelview.ClusterNodeJM;
import com.bbz.bigdata.platform.module.modelview.ClusterNodeListJM;
import com.bbz.bigdata.platform.module.modelview.ClusterSummaryJM;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.service.ClusterService;
import com.bbz.bigdata.util.Util;
import org.nutz.dao.Cnd;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liu_k on 2.
 * 集群相关操作
 */

@IocBean
@At("api/clusterNode")
@Ok("json")
@Fail("http:500")
public class ClusterNodeModule {
    @Inject
    protected ClusterService clusterService;

    /**
     * 集群节点操作（增删改）统一到这里处理
     *
     * @param op      操作类型1:增 改（通过id是否等于-1区分） 2:、删除
     * @param clusterNode 当前要操作的节点
     */
    @At
    public Object operation( @Param("op") int op, @Param("..") ClusterNode clusterNode,
                             HttpServletResponse response ) throws IllegalAccessException{
        Object result;
        try {
            switch( op ) {
                case 1:
                    if( clusterNode.getId() == -1 ) {
                        result = add( clusterNode );
                    } else {
                        result = update( clusterNode );
                    }
                    break;
                case 2:
                    result = delete( clusterNode );
                    break;

                default:
                    throw new IllegalAccessException( "operation 不存在:" + op );
            }
            return result;

        } catch( Exception e ) {
            e.printStackTrace();
            return Util.INSTANCE.buildErrorResponse( response, 500, e.getMessage() );
        }
//        return result;
    }

    private Object add( ClusterNode clusterNode ){
        NutMap re = new NutMap();

//        if( msg != null ) {
//            return re.setv( "ok", false ).setv( "msg", msg );
//        }
        clusterNode.setCreateTime( new Date() );


        clusterService.add( clusterNode );
        return re.setv( "ok", true ).setv( "data", clusterNode );
    }


    private Object update( ClusterNode clusterNode ){
        NutMap re = new NutMap();

        clusterNode.setHost( null );// 不允许更新名
        clusterNode.setCreateTime( null );//也不允许更新创建时间

        clusterService.updateIgnoreNull( clusterNode );// 真正更新的其实只有description and ip
        return re.setv( "ok", true ).setv( "data", clusterNode );
    }

    private Object delete( ClusterNode clusterNode ){
        clusterService.delete( clusterNode.getId() );
//        dao.delete( Cluster.class, cluster.getId() ); // 再严谨一些的话,需要判断是否为>0
        NutMap re = new NutMap();
        return re.setv( "ok", true ).setv( "data", clusterNode );
    }

}
