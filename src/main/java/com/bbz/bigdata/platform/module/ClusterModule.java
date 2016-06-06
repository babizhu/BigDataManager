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
@At("api/cluster")
@Ok("json")
@Fail("http:500")
public class ClusterModule{
    @Inject
    protected ClusterService clusterService;

    @At
    public int count(){
        return clusterService.count();
    }

    /**
     * 获取某个集群的节点信息
     *
     * @param clusterId 要获取的节点id
     * @return 集群及其子节点的信息
     */
    @At
    public Cluster getClusterInfoWithNodes( @Param("clusterId") int clusterId ){
        return clusterService.getClusterInfoWithNodes( clusterId );
    }

    /**
     * 群数据操作（增删改）统一到这里处理
     *
     * @param op      操作类型1:增 改（通过id是否等于-1区分） 2:、删除
     * @param cluster 当前要操作的集群
     */
    @At
    public Object operation( @Param("op") int op, @Param("..") Cluster cluster,
                             HttpServletResponse response ) throws IllegalAccessException{
        Object result;
        try {
            switch( op ) {
                case 1:
                    if( cluster.getId() == -1 ) {
                        result = add( cluster );
                    } else {
                        result = update( cluster );
                    }
                    break;
                case 2:
                    result = delete( cluster );
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

    private Object add( Cluster cluster ){
        NutMap re = new NutMap();

//        if( msg != null ) {
//            return re.setv( "ok", false ).setv( "msg", msg );
//        }
        cluster.setCreateTime( new Date() );


        clusterService.add( cluster );
        return re.setv( "ok", true ).setv( "data", cluster );
    }


    private Object update( Cluster cluster ){
        NutMap re = new NutMap();

        cluster.setName( null );// 不允许更新名
        cluster.setCreateTime( null );//也不允许更新创建时间

        clusterService.updateIgnoreNull( cluster );// 真正更新的其实只有description and ip
        return re.setv( "ok", true ).setv( "data", cluster );
    }

    private Object delete( Cluster cluster ){
        clusterService.delete( cluster.getId() );
//        dao.delete( Cluster.class, cluster.getId() ); // 再严谨一些的话,需要判断是否为>0
        NutMap re = new NutMap();
        return re.setv( "ok", true ).setv( "data", cluster );
    }

    @At
    public QueryResult query( @Param("name") String name, @Param("..") Pager pager ){
        Cnd cnd = Strings.isBlank( name ) ? null : Cnd.where( "name", "like", "%" + name + "%" );
        return clusterService.query( cnd, pager );
    }


//    private ErrorCode checkCluster( Cluster cluster ){
//
//        return cluster == null? ErrorCode.SUCCESS : ErrorCode.STUFF_NOT_ENOUGH;
//    }

    @At
    public Object clusterNodeList(@Param("clusterId") int clusterId,HttpServletResponse response ){
        ClusterNodeListJM jm=new ClusterNodeListJM();
        List<ClusterNode> nodes=null;
        try {
            nodes = clusterService.clusterNodesInfo(clusterId);
        } catch (ParseException e) {
            e.printStackTrace();
            return Util.INSTANCE.buildErrorResponse( response, 500, e.getMessage() );
        } catch (BussException e) {
            e.printStackTrace();
            return Util.INSTANCE.buildErrorResponse( response, 500, e.getMessage() );
        } catch (Exception e){
            e.printStackTrace();
            return Util.INSTANCE.buildErrorResponse( response, 500, e.getMessage() );
        }
        if (nodes!=null){
            List<ClusterNodeJM> nodejms=new ArrayList<>();
            nodes.stream().forEach(node->{
                nodejms.add(new ClusterNodeJM(node));
                jm.setTotalCount(jm.getTotalCount()+1);
                if(node.getStatus()==1){
                    jm.setAliveCount(jm.getAliveCount()+1);
                }
            });
            jm.setNodeList(nodejms);
        }
        return jm;
    }

    @At
    public RRDJsonModel clusterMemeryInfo(@Param("clusterId") int clusterId,@Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        RRDJsonModel rrdJM = clusterService.clusterMemoryInfo(clusterId,timePeriod);
        return rrdJM;
    }

    @At
    public RRDJsonModel clusterMemorySimpleData(@Param("clusterId") int clusterId,@Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        RRDJsonModel rrdJM = clusterService.clusterMemorySimpleData(clusterId,timePeriod);
        return rrdJM;
    }

    @At
    public RRDJsonModel clusterCPUInfo(@Param("clusterId") int clusterId,@Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        RRDJsonModel rrdJM = clusterService.clusterCPUInfo(clusterId,timePeriod);
        return rrdJM;
    }

    @At
    public RRDJsonModel clusterNetworkInfo(@Param("clusterId") int clusterId,@Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        RRDJsonModel rrdJM = clusterService.clusterNetworkInfo(clusterId,timePeriod);
        return rrdJM;
    }

    @At
    public RRDJsonModel clusterDiskInfo(@Param("clusterId") int clusterId,@Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        RRDJsonModel rrdJM = clusterService.clusterDiskInfo(clusterId,timePeriod);
        return rrdJM;
    }

    @At
    public ClusterChartsJM clusterCharts(@Param("clusterId") int clusterId,@Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        ClusterChartsJM cc=new ClusterChartsJM();
        cc.setCpu(clusterService.clusterCPUSimpleData(clusterId,timePeriod));
        cc.setMem(clusterService.clusterMemorySimpleData(clusterId,timePeriod));
        cc.setNetwork(clusterService.clusterNetworkInfo(clusterId,timePeriod));
        cc.setDisk(clusterService.clusterDiskSimpleData(clusterId,timePeriod));
        return cc;
    }

    @At
    public Object clusterSummaryInfo(@Param("clusterId") int clusterId,@Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        ClusterSummaryJM jm=new ClusterSummaryJM();
        ClusterChartsJM cc=new ClusterChartsJM();
        jm.setClusterCharts(cc);
        cc.setCpu(clusterService.clusterCPUSimpleData(clusterId,timePeriod));
        cc.setMem(clusterService.clusterMemorySimpleData(clusterId,timePeriod));
        cc.setNetwork(clusterService.clusterNetworkInfo(clusterId,timePeriod));
        cc.setDisk(clusterService.clusterDiskSimpleData(clusterId,timePeriod));
        Object nodeListJM = this.clusterNodeList(clusterId,null);
        if(nodeListJM instanceof ClusterNodeListJM) {
            jm.setClusterNodeList((ClusterNodeListJM)nodeListJM);
        }else{
            return nodeListJM;
        }
        return jm;
    }
}
