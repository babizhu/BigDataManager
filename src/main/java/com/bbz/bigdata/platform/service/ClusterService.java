package com.bbz.bigdata.platform.service;

import com.bbz.bigdata.platform.bean.Cluster;
import com.bbz.bigdata.platform.bean.ClusterNode;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtoolproxy.RRDVisitorProxy;
import com.bbz.bigdata.platform.rrdtoolproxy.model.ClusterNodesStatusAmountDto;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdNameEntityService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by liu_k on 2016/5/25.
 * cluster集群服务类
 */
@IocBean(fields="dao")
public class ClusterService extends IdNameEntityService<Cluster>{

    public QueryResult query( Cnd cnd, Pager pager ){
        QueryResult qr = new QueryResult();
       qr.setList( dao().query( Cluster.class, cnd, pager ) );


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


//    public Cluster queryOne( Cnd cnd){
//        List<Cluster> list = dao().query(Cluster.class,cnd);
//        return list==null||list.size()==0?null:list.get(0);
//    }

    public ClusterNode getClusterNode(int nodeId){
        return dao().fetch(ClusterNode.class, nodeId);
    }

    /**
     * 集群节点状态数量统计
     * @param clusterId
     * @return
     */
    public ClusterNodesStatusAmountDto clusterNodesStatusAmount(int clusterId){
        ClusterNodesStatusAmountDto dto=new ClusterNodesStatusAmountDto();
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        if (cluster==null||cluster.getClusterNode()==null||cluster.getClusterNode().size()==0){
            return dto;
        }
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        Collection<String> hostNames=new ArrayList<>(cluster.getClusterNode().size());
        cluster.getClusterNode().forEach((node)->{
            hostNames.add(node.getHost());
        });
        return visitor.clusterNodeStateAmount(cluster.getName(),hostNames);
    }

    /**
     * 集群内存监控数据
     * @param clusterId
     * @param timePeriod
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterMemoryInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterMemoryDataValue(cluster.getName(),timePeriod);
    }

    /**
     * 集群CPU监控数据
     * @param clusterId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterCPUInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterCPUDataValue(cluster.getName(),timePeriod);
    }

    /**
     * 集群的网络IO监控数据
     * @param clusterId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterNetworkInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterNetworkDataValue(cluster.getName(),timePeriod);
    }

    /**
     * 集群的硬盘监控数据
     * @param clusterId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterDiskInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterDiskDataValue(cluster.getName(),timePeriod);
    }

    /**
     * 集群内存简单监控数据
     * @param clusterId
     * @param timePeriod
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterMemorySimpleData(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterMemorySimpleData(cluster.getName(),timePeriod);
    }

    /**
     * 集群CPU简单监控数据
     * @param clusterId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterCPUSimpleData(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterCPUSimpleData(cluster.getName(),timePeriod);
    }

    /**
     * 集群的硬盘控件简单监控数据
     * @param clusterId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterDiskSimpleData(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterDiskSimpleData(cluster.getName(),timePeriod);
    }

//    /**
//     * 集群内存监控数据
//     * @param clusterId
//     * @param timePeriod
//     * @return
//     * @throws ParseException
//     * @throws BussException
//     */
//    public RRDJsonModel clusterNodeInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
//        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
//        RRDVisitorProxy visitor = new RRDVisitorProxy();
//        cluster.getClusterNode().parallelStream().forEach((node)->{
//            try {
//                RRDJsonModel rrdJsonModel = visitor.clusterNodeMemoryInfo(cluster.getName(), node.getHost(), timePeriod);
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            } catch (BussException e) {
//                e.printStackTrace();
//            }
//        });
//        return visitor.clusterNodeMemoryInfo(node.getHost(),timePeriod);
//    }

}
