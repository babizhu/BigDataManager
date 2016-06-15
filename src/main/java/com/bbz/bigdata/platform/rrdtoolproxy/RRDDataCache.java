package com.bbz.bigdata.platform.rrdtoolproxy;

import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
import com.bbz.bigdata.util.DateUtil;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiran on 2016/6/15.
 */
@Data
public class RRDDataCache {

    public static RRDDataCache instance(){
        return instance;
    }

    private static RRDDataCache instance=new RRDDataCache();

    private RRDDataCache(){}

    @Data
    class CacheData{
        private Date deadTime;
        private boolean alive;
    }

    /**
     * Map<clusterName,Map<hostName,CacheData>>
     */
    private Map<String,Map<String,CacheData>> cacheDatas=new HashMap<>();

    private void put(String clusterName,String hostName,CacheData cacheData){
        Map<String, CacheData> hosts = cacheDatas.get(clusterName);
        if (hosts==null){
            hosts=new HashMap<>();
            synchronized (cacheDatas){
                cacheDatas.put(clusterName,hosts);
            }
        }
        hosts.put(hostName,cacheData);
    }

    private CacheData get(String clusterName,String hostName){
        Map<String, CacheData> hosts = cacheDatas.get(clusterName);
        if(hosts==null){
            return null;
        }
        return hosts.get(hostName);
    }

    public void record(String clusterName,String hostName,RRDModel rrdModel){
        if(rrdModel==null) return;
        CacheData cacheData=new CacheData();
        cacheData.deadTime= DateUtil.timeAfter(Calendar.SECOND,Constant.cacheValidPeriod);
        if (rrdModel.getList()!=null){
            rrdModel.getList().forEach(rdm->{
                if(rdm.getNewestData()!=null){
                    cacheData.alive=true;
                }
            });
        }
        put(clusterName,hostName,cacheData);
    }
}
