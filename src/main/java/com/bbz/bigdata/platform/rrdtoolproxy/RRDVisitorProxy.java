package com.bbz.bigdata.platform.rrdtoolproxy;

import com.bbz.bigdata.platform.rrdtool.Unit;
import com.bbz.bigdata.platform.rrdtool.api.Visitor;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.jsonresultmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.MeasurementCreator;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;
import com.bbz.bigdata.platform.rrdtoolproxy.model.ClusterNodesStatusAmountDto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;

/**
 * Created by weiran on 2016/5/27.
 */
public class RRDVisitorProxy {
    /**
     * 默认查询时间段 单位：秒
     */
    private static final int timePeriod=1800;
    /**
     * 默认查询时间段 单位：秒
     */
    public static final int timePeriodNewestInfo=180;

    /**
     * 判断间节点为dead时连续无数据个数
     */
    public static final int NULL_COUNT_OF_DEAD_LIMIT=4;

    public static final String DETAIL_NAME_USED=".Used";

    /**
     * 查询集群中节点的数量和状态信
     * @param clusterName 集群名称
     * @param hostNames 群内节点主机名
     * @throws ParseException
     * @throws BussException
     */
    public ClusterNodesStatusAmountDto clusterNodeStateAmount(String clusterName,Collection<String> hostNames){
        ClusterNodesStatusAmountDto dto=new ClusterNodesStatusAmountDto();
        dto.setTotalAmount(hostNames.size());
        Visitor visitor=new Visitor();
        for (String hostName:hostNames ) {
            try {
                RRDJsonModel jm = visitor.visit(clusterName, hostName, timePeriod, new Measurement.Detail[]{Metrics.Disk.Free}, null, false,null);
                if(this.isAlive(jm.getList().get(0).getData())){
                    dto.setAliveAmount(dto.getAliveAmount()+1);
                }else{
                    dto.setDeadAmount(dto.getDeadAmount()+1);
                }
            }catch(Exception e){
                dto.setDeadAmount(dto.getDeadAmount()+1);
            }
        }
        return dto;
    }

    private boolean isAlive(BigDecimal[] data){
        if (data==null) return false;
        if (data.length< NULL_COUNT_OF_DEAD_LIMIT) return true;
        for (int i=data.length-1;i>=data.length-NULL_COUNT_OF_DEAD_LIMIT;i--){
            if (data[i]!=null){
                return true;
            }
        }
        return false;
    }

    /**
     * 集群内存信息图数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterMemoryDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.Memory.Free,Metrics.Memory.Use,Metrics.Memory.Total,Metrics.Memory.Share
        ,Metrics.Memory.Cache,Metrics.Memory.Swap,Metrics.Memory.Buffer},null,false,null,Metrics.Memory.Free,Metrics.Memory.Use,Metrics.Memory.Share
                ,Metrics.Memory.Cache,Metrics.Memory.Swap,Metrics.Memory.Buffer);
//        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.Memory.Free,Metrics.Memory.Cache},null,true
//                ,new MeasurementCreator[]{new MeasurementCreator(
//                        100.0, MeasurementCreator.Operator.MINUS,new MeasurementCreator(
//                        Metrics.Memory.Free, MeasurementCreator.Operator.ADD,Metrics.Memory.Cache,null
//                ),Metrics.Memory.name()+DETAIL_NAME_USED)}
//        );
    }

    /**
     * 集群CPU信息图数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterCPUDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.CPU.Idle}, null,false
                ,new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100), MeasurementCreator.Operator.MINUS,Metrics.CPU.Idle,Metrics.CPU.name()+DETAIL_NAME_USED)
                },Metrics.CPU.Idle);
    }

    /**
     * 集群网络IO信息图数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterNetworkDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.Network.In,Metrics.Network.Out}
                , null, false,null,Metrics.Network.In,Metrics.Network.Out);
    }

    /**
     * 集群网络IO信息图数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterDiskDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.Disk.Free,Metrics.Disk.Total}, null
                ,true, new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100),MeasurementCreator.Operator.MINUS,Metrics.Disk.Free,Metrics.Disk.name()+DETAIL_NAME_USED)}
                ,Metrics.Disk.Free);
    }
/******************************
    /**
     * 集群内存信息图简单数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterMemorySimpleData(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.Memory.Free,Metrics.Memory.Cache},null,true
                ,new MeasurementCreator[]{new MeasurementCreator(
                        new BigDecimal(100), MeasurementCreator.Operator.MINUS,new MeasurementCreator(
                        Metrics.Memory.Free, MeasurementCreator.Operator.ADD,Metrics.Memory.Cache,null
                ),Metrics.Memory.name()+DETAIL_NAME_USED)}
        );
    }

    /**
     * 集群CPU信息图简单数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterCPUSimpleData(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.CPU.Idle}, null,false
                ,new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100), MeasurementCreator.Operator.MINUS,Metrics.CPU.Idle,Metrics.CPU.name()+DETAIL_NAME_USED)
                } );
    }

    /**
     * 集群硬盘空间信息图简单数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterDiskSimpleData(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.Disk.Free}, null
                ,true, new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100),MeasurementCreator.Operator.MINUS,Metrics.Disk.Free,Metrics.Disk.name()+DETAIL_NAME_USED)}
                );
    }

    /**
     * 节点内存信息图数据
     * @param  clusterName 集群名称
     * @param nodeName 节点名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterNodeMemoryInfo(String clusterName,String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,nodeName,timePeriod,new Measurement.Detail[]{Metrics.Memory.Free,Metrics.Memory.Cache},null,true
                ,new MeasurementCreator[]{new MeasurementCreator(
                        new BigDecimal(100), MeasurementCreator.Operator.MINUS,new MeasurementCreator(
                        Metrics.Memory.Free, MeasurementCreator.Operator.ADD,Metrics.Memory.Cache,null
                ),Metrics.Memory.name()+DETAIL_NAME_USED)});
    }

    /**
     * 节点CPU信息图数据
     * @param clusterName 集群名称
     * @param nodeName 节点名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterNodeCPUInfo(String clusterName,String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,nodeName,timePeriod,new Measurement.Detail[]{Metrics.CPU.Idle}, null,false
                ,new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100), MeasurementCreator.Operator.MINUS,Metrics.CPU.Idle,Metrics.CPU.name()+DETAIL_NAME_USED)
                } );
    }

    /**
     * 节点网络IO信息图数据
     * @param clusterName 集群名称
     * @param nodeName 节点名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterNodeNetworkInfo(String clusterName,String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName,nodeName,timePeriod,new Measurement.Detail[]{Metrics.Network.In,Metrics.Network.Out},
                null ,false, null,Metrics.Network.In,Metrics.Network.Out);
    }

    /**
     * 节点硬盘IO信息图数据
     * @param clusterName 集群名称
     * @param nodeName 节点名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDJsonModel clusterNodeDiskInfo(String clusterName,String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor = new Visitor();
        if (timePeriod == null) {
            timePeriod = RRDVisitorProxy.timePeriod;
        }
        return visitor.visit(clusterName, nodeName, timePeriod, new Measurement.Detail[]{Metrics.Disk.Free}, null
                , true, new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100), MeasurementCreator.Operator.MINUS, Metrics.Disk.Free, Metrics.Disk.name() + DETAIL_NAME_USED)}
        );
    }

}
