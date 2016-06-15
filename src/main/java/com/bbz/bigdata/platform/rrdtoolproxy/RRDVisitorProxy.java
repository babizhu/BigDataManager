package com.bbz.bigdata.platform.rrdtoolproxy;

import com.bbz.bigdata.platform.rrdtool.api.Visitor;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
import com.bbz.bigdata.platform.rrdtool.measurement.Measurement;
import com.bbz.bigdata.platform.rrdtool.measurement.MeasurementCreator;
import com.bbz.bigdata.platform.rrdtool.measurement.Metrics;

import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Created by weiran on 2016/5/27.
 */
public class RRDVisitorProxy {
    /**
     * 默认查询时间段 单位：秒
     */
    public static final int timePeriod=1800;

    public static final String DETAIL_NAME_USED=".Used";

    /**
     * 集群内存信息图数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDModel clusterMemoryDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        RRDModel rrdModel = visitor.visit(clusterName,"",timePeriod,new Measurement.Detail[]{Metrics.Memory.Free,Metrics.Memory.Use,Metrics.Memory.Total,Metrics.Memory.Share
        ,Metrics.Memory.Cache,Metrics.Memory.Swap,Metrics.Memory.Buffer},null,false,null,Metrics.Memory.Free,Metrics.Memory.Use,Metrics.Memory.Share
                ,Metrics.Memory.Cache,Metrics.Memory.Swap,Metrics.Memory.Buffer);
        return  rrdModel;
    }

    /**
     * 集群CPU信息图数据
     * @param clusterName 集群名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDModel clusterCPUDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterNetworkDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterDiskDataValue(String clusterName, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterMemorySimpleData(String clusterName, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterCPUSimpleData(String clusterName, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterDiskSimpleData(String clusterName, Integer timePeriod) throws ParseException, BussException {
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
    public RRDModel clusterNodeMemoryInfo(String clusterName, String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        RRDModel rrdModel = visitor.visit(clusterName,nodeName,timePeriod,new Measurement.Detail[]{Metrics.Memory.Free,Metrics.Memory.Cache},null,true
                ,new MeasurementCreator[]{new MeasurementCreator(
                        new BigDecimal(100), MeasurementCreator.Operator.MINUS,new MeasurementCreator(
                        Metrics.Memory.Free, MeasurementCreator.Operator.ADD,Metrics.Memory.Cache,null
                ),Metrics.Memory.name()+DETAIL_NAME_USED)});
        RRDDataCache.instance().record(clusterName,nodeName,rrdModel);
        return  rrdModel;
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
    public RRDModel clusterNodeCPUInfo(String clusterName, String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        RRDModel rrdModel = visitor.visit(clusterName,nodeName,timePeriod,new Measurement.Detail[]{Metrics.CPU.Idle,Metrics.CPU.Speed}, null,false
                ,new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100), MeasurementCreator.Operator.MINUS,Metrics.CPU.Idle,Metrics.CPU.name()+DETAIL_NAME_USED)
                });
        RRDDataCache.instance().record(clusterName,nodeName,rrdModel);
        return  rrdModel;
    }

//    /**
//     * @param clusterName 集群名称
//     * @param nodeName 节点名称
//     * @param timePeriod 到目前为止的时间段
//     * @return
//     * @throws ParseException
//     * @throws BussException
//     */
//    public RRDModel clusterNodeCPUSpeed(String clusterName, String nodeName, Integer timePeriod) throws ParseException, BussException {
//        Visitor visitor=new Visitor();
//        if (timePeriod==null){
//            timePeriod= RRDVisitorProxy.timePeriod;
//        }
//        RRDModel rrdModel = visitor.visit(clusterName,nodeName,timePeriod,new Measurement.Detail[]{Metrics.CPU.Speed}, null,false
//                ,new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100), MeasurementCreator.Operator.MINUS,Metrics.CPU.Idle,Metrics.CPU.name()+DETAIL_NAME_USED)
//                },Metrics.CPU.Speed );
//        RRDDataCache.instance().record(clusterName,nodeName,rrdModel);
//        return  rrdModel;
//    }

    /**
     * 节点网络IO信息图数据
     * @param clusterName 集群名称
     * @param nodeName 节点名称
     * @param timePeriod 到目前为止的时间段
     * @return
     * @throws ParseException
     * @throws BussException
     */
    public RRDModel clusterNodeNetworkInfo(String clusterName, String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor=new Visitor();
        if (timePeriod==null){
            timePeriod= RRDVisitorProxy.timePeriod;
        }
        RRDModel rrdModel = visitor.visit(clusterName,nodeName,timePeriod,new Measurement.Detail[]{Metrics.Network.In,Metrics.Network.Out},
                null ,false, null,Metrics.Network.In,Metrics.Network.Out);
        RRDDataCache.instance().record(clusterName,nodeName,rrdModel);
        return  rrdModel;
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
    public RRDModel clusterNodeDiskInfo(String clusterName, String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor = new Visitor();
        if (timePeriod == null) {
            timePeriod = RRDVisitorProxy.timePeriod;
        }
        RRDModel rrdModel = visitor.visit(clusterName, nodeName, timePeriod, new Measurement.Detail[]{Metrics.Disk.Free}, null
                , true, new MeasurementCreator[]{new MeasurementCreator(new BigDecimal(100), MeasurementCreator.Operator.MINUS, Metrics.Disk.Free, Metrics.Disk.name() + DETAIL_NAME_USED)}
        );
        RRDDataCache.instance().record(clusterName,nodeName,rrdModel);
        return  rrdModel;
    }

    /*****************    hdfs      ****************/

    public RRDModel hdfsCapacityData(String clusterName, String nodeName, Integer timePeriod) throws ParseException, BussException {
        Visitor visitor = new Visitor();
        if (timePeriod == null) {
            timePeriod = RRDVisitorProxy.timePeriod;
        }
        RRDModel rrdModel = visitor.visit(clusterName, nodeName, timePeriod, new Measurement.Detail[]{Metrics.HDFSCapacity.Total,Metrics.HDFSCapacity.Used}, null
                , false, null, Metrics.HDFSCapacity.All()
        );
        return rrdModel;
    }

}
