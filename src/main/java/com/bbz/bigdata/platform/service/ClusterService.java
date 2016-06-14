package com.bbz.bigdata.platform.service;

import com.bbz.bigdata.platform.bean.Cluster;
import com.bbz.bigdata.platform.bean.ClusterNode;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.DataModel;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
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
    public RRDModel clusterMemoryInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterCPUInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterNetworkInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterDiskInfo(int clusterId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterMemorySimpleData(int clusterId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterCPUSimpleData(int clusterId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterDiskSimpleData(int clusterId, Integer timePeriod) throws ParseException, BussException {
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
    public Map<ClusterNode,Map<Measurement,RRDModel>> clusterNodesInfo(int clusterId) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        if (cluster==null||cluster.getClusterNode()==null){
            return new TreeMap<>();
        }
        int timePeriod=RRDVisitorProxy.timePeriod;
        LinkedHashMap<ClusterNode,Map<Measurement,RRDModel>> result=new LinkedHashMap<>();
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        cluster.getClusterNode().forEach((node)->{
            Map<Measurement,RRDModel> rrdMap=new HashMap<>();
            result.put(node,rrdMap);
            try {
                RRDModel rrdModel = visitor.clusterNodeCPUInfo(cluster.getName(), node.getHost(), timePeriod);
                DataModel djm = rrdModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.CPU.name()+RRDVisitorProxy.DETAIL_NAME_USED);
                }).findFirst().get();
                if(djm!=null){
                    node.setCpuUsedPercent(newestData(djm.getData(),djm.getPointInterval()));
                    if(node.getCpuUsedPercent()!=null){
                        node.setStatus(1);
                    }
                }
                node.setCpuUnit(rrdModel.getTotalUnit().toString());
                node.setCpuTotal(rrdModel.getTotal());
                rrdMap.put(Metrics.CPU, rrdModel);

                rrdModel = visitor.clusterNodeMemoryInfo(cluster.getName(), node.getHost(), timePeriod);
                djm = rrdModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Memory.name()+RRDVisitorProxy.DETAIL_NAME_USED);
                }).findFirst().get();
                if(djm!=null){
                    node.setMemUsedPercent(newestData(djm.getData(),djm.getPointInterval()));
                    if(node.getMemUsedPercent()!=null){
                        node.setStatus(1);
                    }
                }
                node.setMemTotal(rrdModel.getTotal());
                node.setMemUnit(rrdModel.getTotalUnit().toString());
                rrdMap.put(Metrics.Memory, rrdModel);

                rrdModel = visitor.clusterNodeDiskInfo(cluster.getName(), node.getHost(), timePeriod);
                djm = rrdModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Disk.name()+RRDVisitorProxy.DETAIL_NAME_USED);
                }).findFirst().get();
                if(djm!=null){
                    node.setDiskUsedPercent(newestData(djm.getData(),djm.getPointInterval()));
                    if(node.getDiskUsedPercent()!=null){
                        node.setStatus(1);
                    }
                }
                node.setDiskTotal(rrdModel.getTotal());
                node.setDiskUnit(rrdModel.getTotalUnit().toString());
                rrdMap.put(Metrics.Disk, rrdModel);

                rrdModel = visitor.clusterNodeNetworkInfo(cluster.getName(), node.getHost(), timePeriod);
                djm = rrdModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Network.In.fullName());
                }).findFirst().get();
                if(djm!=null){
                    node.setNetIn(newestData(djm.getData(),djm.getPointInterval()));
                    if(node.getNetIn()!=null){
                        node.setStatus(1);
                    }
                }
                djm = rrdModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.Network.Out.fullName());
                }).findFirst().get();
                if(djm!=null){
                    node.setNetOut(newestData(djm.getData(),djm.getPointInterval()));
                    if(node.getNetOut()!=null){
                        node.setStatus(1);
                    }
                }
                node.setNetUnit(rrdModel.getYunit().toString());
                rrdMap.put(Metrics.Network, rrdModel);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (BussException e) {
                e.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });

        return result;
    }

    private <T> T newestData(T[] data,int dataIntervalTime){
        if(data==null){
            return null;
        }
        for (int i=data.length-1;i>=0&&i>=data.length-RRDVisitorProxy.NO_DATA_LIMIT_FOR_DEAD/dataIntervalTime;i--){
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
    public RRDModel nodeMemoryInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel nodeCPUInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel nodeNetworkInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel nodeDiskInfo(int nodeId, Integer timePeriod) throws ParseException, BussException {
        ClusterNode clusterNode = this.getClusterNode(nodeId);
        if (clusterNode==null) return null;
        Cluster cluster=this.getClusterInfoWithoutNodes(clusterNode.getClusterId());
        if (cluster==null) return null;
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        return visitor.clusterNodeDiskInfo(cluster.getName(), clusterNode.getHost(),timePeriod);
    }
}
