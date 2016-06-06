package com.bbz.bigdata.platform.service;

import com.bbz.bigdata.platform.bean.Cluster;
import com.bbz.bigdata.platform.bean.ClusterNode;
import com.bbz.bigdata.platform.rrdtool.Constant;
import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.DataJsonModel;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;
import com.bbz.bigdata.platform.rrdtoolproxy.RRDVisitorProxy;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.IdNameEntityService;

import java.math.BigDecimal;
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

    public void add( ClusterNode clusterNode ){
        dao().insert( clusterNode );
    }

    public void updateIgnoreNull( Cluster cluster ){
        dao().updateIgnoreNull( cluster );
    }

    public void updateIgnoreNull( ClusterNode clusterNode ){
        dao().updateIgnoreNull( clusterNode );
    }

//    public Cluster queryOne( Cnd cnd){
//        List<Cluster> list = dao().query(Cluster.class,cnd);
//        return list==null||list.size()==0?null:list.get(0);
//    }

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

    /**
     * 节点列表信息
     * @param clusterId
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public List<ClusterNode> clusterNodesInfo(int clusterId) throws ParseException, BussException {
        Cluster cluster=this.getClusterInfoWithNodes(clusterId);
        if (cluster==null||cluster.getClusterNode()==null){
            return null;
        }
        int timePeriod=RRDVisitorProxy.timePeriodNewestInfo;
        RRDVisitorProxy visitor = new RRDVisitorProxy();
        cluster.getClusterNode().parallelStream().forEach((node)->{
            try {
                RRDJsonModel rrdJsonModel = visitor.clusterNodeMemoryInfo(cluster.getName(), node.getHost(), timePeriod);
                DataJsonModel djm = rrdJsonModel.getList().stream().filter(drm -> {
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
                rrdJsonModel = visitor.clusterNodeCPUInfo(cluster.getName(), node.getHost(), timePeriod);
                djm = rrdJsonModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.CPU.name()+RRDVisitorProxy.DETAIL_NAME_USED);
                }).findFirst().get();
                if(djm!=null){
                    node.setCpuUsedPercent(newestData(djm.getData()));
                    if(node.getCpuUsedPercent()!=null){
                        node.setStatus(1);
                    }
                }
                djm = rrdJsonModel.getList().stream().filter(drm -> {
                    return drm.getName() != null && drm.getName().equals(Metrics.CPU.Speed.fullName());
                }).findFirst().get();
                if(djm!=null){
                    node.setCpuTotal(newestData(djm.getData()));
                    if (node.getCpuTotal()!=null){
                        node.setCpuTotal(node.getCpuTotal().divide(new BigDecimal(10), Constant.numberScale,Constant.roundingMode));
                    }
                    node.setCpuUnit(Unit.GHz.toString());
                    if(node.getCpuTotal()!=null){
                        node.setStatus(1);
                    }
                }
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
//                Random random=new Random();
//                node.setMemUsedPercent(new BigDecimal(random.nextInt(99)+1));
//                node.setCpuUsedPercent(new BigDecimal(random.nextInt(99)+1));
//                node.setDiskUsedPercent(new BigDecimal(random.nextInt(99)+1));
//                node.setNetIn(new BigDecimal(random.nextInt(2234)));
//                node.setNetOut(new BigDecimal(random.nextInt(123)));
//                node.setNetUnit("K/S");
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (BussException e) {
                e.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
//                throw ex;
            }
        });
        return cluster.getClusterNode();
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
}
