package com.bbz.bigdata.platform.service;

import com.bbz.bigdata.platform.bean.Cluster;
import com.bbz.bigdata.platform.bean.ClusterNode;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.DataJsonModel;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;
import com.bbz.bigdata.platform.rrdtoolproxy.RRDVisitorProxy;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdNameEntityService;

import java.text.ParseException;
import java.util.*;

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
        Cluster cluster = dao().fetch( Cluster.class,clusterId);
        if(cluster == null) return null;
        cluster = dao().fetchLinks( cluster, null, Cnd.orderBy().asc("host"));
        return cluster;
    }

    public Cluster getClusterInfoWithoutNodes( int clusterId ){
        return dao().fetch( Cluster.class,clusterId);
    }

    public int count(){
        System.out.println( dao().count( Cluster.class ) );
        return dao().count( Cluster.class );
    }

    public void add( Cluster cluster ){
        dao().insert( cluster );
    }

    public void add( ClusterNode clusterNode ){
        dao().insert( clusterNode );
    }

    public void updateIgnoreNull( Cluster cluster ){
        dao().updateIgnoreNull( cluster );
    }

    public void updateIgnoreNull( ClusterNode clusterNode ){
        dao().updateIgnoreNull( clusterNode );
    }

    public int deleteNode(int id){
        return dao().delete(ClusterNode.class,id);
    }

    public ClusterNode getClusterNode(int nodeId){
        return dao().fetch(ClusterNode.class, nodeId);
    }

//    /**
//     * 集群节点状态数量统计
//     * @param clusterId
//     * @return
//     */
//    public ClusterNodesStatusAmountDto clusterNodesStatusAmount(int clusterId){
//        ClusterNodesStatusAmountDto dto=new ClusterNodesStatusAmountDto();
//        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
//        if (cluster==null||cluster.getClusterNode()==null||cluster.getClusterNode().size()==0){
//            return dto;
//        }
//        RRDVisitorProxy visitor = new RRDVisitorProxy();
//        Collection<String> hostNames=new ArrayList<>(cluster.getClusterNode().size());
//        cluster.getClusterNode().forEach((node)->{
//            hostNames.add(node.getHost());
//        });
//        return visitor.clusterNodeStateAmount(cluster.getName(),hostNames);
//    }

    /**
     * 集群内存监控数据
     * @param clusterId
     * @param timePeriod
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterMemoryInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterId);
        if (cluster==null) return null;
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
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterId);
        if (cluster==null) return null;
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
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterId);
        if (cluster==null) return null;
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
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterId);
        if (cluster==null) return null;
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
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterId);
        if ( cluster==null) return null;
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
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterId);
        if (cluster==null) return null;
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
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterId);
        if (cluster==null) return null;
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterDiskSimpleData(cluster.getName(),timePeriod);
    }

    /**
     * 节点列表信息
     * @param clusterId
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public Map<ClusterNode,Map<Measurement,RRDJsonModel>> clusterNodesInfo(int clusterId) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        if (cluster==null||cluster.getClusterNode()==null){
            return new TreeMap<>();
        }
        int timePeriod=RRDVisitorProxy.timePeriod;
        LinkedHashMap<ClusterNode,Map<Measurement,RRDJsonModel>> result=new LinkedHashMap<>();
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        cluster.getClusterNode().forEach((node)->{
            Map<Measurement,RRDJsonModel> rrdMap=new HashMap<>();
            result.put(node,rrdMap);
            try {
                RRDJsonModel rrdJsonModel = visitor.clusterNodeCPUInfo(cluster.getName(), node.getHost(), timePeriod);
                DataJsonModel djm = rrdJsonModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.CPU.name()+RRDVisitorProxy.DETAIL_NAME_USED);
                }).findFirst().get();
                if(djm!=null){
                    node.setCpuUsedPercent(newestData(djm.getData()));
                    if(node.getCpuUsedPercent()!=null){
                        node.setStatus(1);
                    }
                }
                node.setCpuUnit(rrdJsonModel.getTotalUnit());
                node.setCpuTotal(rrdJsonModel.getTotal());
                rrdMap.put(Metrics.CPU,rrdJsonModel);

                rrdJsonModel = visitor.clusterNodeMemoryInfo(cluster.getName(), node.getHost(), timePeriod);
                djm = rrdJsonModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Memory.name()+RRDVisitorProxy.DETAIL_NAME_USED);
                }).findFirst().get();
                if(djm!=null){
                    node.setMemUsedPercent(newestData(djm.getData()));
                    if(node.getMemUsedPercent()!=null){
                        node.setStatus(1);
                    }
                }
                node.setMemTotal(rrdJsonModel.getTotal());
                node.setMemUnit(rrdJsonModel.getTotalUnit());
                rrdMap.put(Metrics.Memory,rrdJsonModel);

                rrdJsonModel = visitor.clusterNodeDiskInfo(cluster.getName(), node.getHost(), timePeriod);
                djm = rrdJsonModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Disk.name()+RRDVisitorProxy.DETAIL_NAME_USED);
                }).findFirst().get();
                if(djm!=null){
                    node.setDiskUsedPercent(newestData(djm.getData()));
                    if(node.getDiskUsedPercent()!=null){
                        node.setStatus(1);
                    }
                }
                node.setDiskTotal(rrdJsonModel.getTotal());
                node.setDiskUnit(rrdJsonModel.getTotalUnit());
                rrdMap.put(Metrics.Disk,rrdJsonModel);

                rrdJsonModel = visitor.clusterNodeNetworkInfo(cluster.getName(), node.getHost(), timePeriod);
                djm = rrdJsonModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Network.In.fullName());
                }).findFirst().get();
                if(djm!=null){
                    node.setNetIn(newestData(djm.getData()));
                    if(node.getNetIn()!=null){
                        node.setStatus(1);
                    }
                }
                djm = rrdJsonModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Network.Out.fullName());
                }).findFirst().get();
                if(djm!=null){
                    node.setNetOut(newestData(djm.getData()));
                    if(node.getNetOut()!=null){
                        node.setStatus(1);
                    }
                }
                node.setNetUnit(rrdJsonModel.getYunit());
                rrdMap.put(Metrics.Network,rrdJsonModel);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (BussException e) {
                e.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
//                throw ex;
            }
        });
//        LinkedHashMap<ClusterNode,Map<Measurement,RRDJsonModel>> sortedResult=new LinkedHashMap<>();
//        result.entrySet().stream().sorted((e1,e2)->{
//            return e1.getKey().getHost().compareTo(e2.getKey().getHost());
//        }).forEach(entry->{
//            sortedResult.put(entry.getKey(),entry.getValue());
//        });
        return result;
    }

    private <T> T newestData(T[] data){
        if(data==null){
            return null;
        }
        for (int i=data.length-1;i>=0&&i>=data.length-RRDVisitorProxy.NULL_COUNT_OF_DEAD_LIMIT;i--){
            if (data[i]!=null){
                return data[i];
            }
        }
        return null;
    }

    /**
     * 节点内存监控数据
     * @param nodeId
     * @param timePeriod
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel nodeMemoryInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
        ClusterNode clusterNode = this.getClusterNode(nodeId);
        if (clusterNode==null) return null;
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterNode.getClusterId());
        if (cluster==null) return null;
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterNodeMemoryInfo(cluster.getName(),clusterNode.getHost(),timePeriod);
    }

    /**
     * 节点CPU监控数据
     * @param nodeId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel nodeCPUInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
        ClusterNode clusterNode = this.getClusterNode(nodeId);
        if (clusterNode==null) return null;
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterNode.getClusterId());
        if (cluster==null) return null;
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterNodeCPUInfo(cluster.getName(), clusterNode.getHost(),timePeriod);
    }

    /**
     * 节点的网络IO监控数据
     * @param nodeId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel nodeNetworkInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
        ClusterNode clusterNode = this.getClusterNode(nodeId);
        if (clusterNode==null) return null;
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterNode.getClusterId());
        if (cluster==null) return null;
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterNodeNetworkInfo(cluster.getName(), clusterNode.getHost(),timePeriod);
    }

    /**
     * 节点的硬盘监控数据
     * @param nodeId
     * @param timePeriod 查询时间段，为空则用默认时长
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel nodeDiskInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
        ClusterNode clusterNode = this.getClusterNode(nodeId);
        if (clusterNode==null) return null;
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterNode.getClusterId());
        if (cluster==null) return null;
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterNodeDiskInfo(cluster.getName(), clusterNode.getHost(),timePeriod);
    }
}
