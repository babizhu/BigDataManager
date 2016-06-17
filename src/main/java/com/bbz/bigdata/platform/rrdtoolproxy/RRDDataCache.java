package com.bbz.bigdata.platform.rrdtoolproxy;

import com.bbz.bigdata.platform.rrdtool.rrdmodel.DataModel;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
import com.bbz.bigdata.util.DateUtil;
import lombok.Data;

import java.util.*;

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
        private Date aliveDeadTime;
        private Boolean alive;
        /**
         * Map<detailFullName,{Value,deadTime}>
         */
        private Map<String,Object[]> newestDetails=new HashMap<>();


        private boolean isAlive(Date date){
            return alive!=null&&alive&&aliveDeadTime!=null&&aliveDeadTime.after(date);
        }
        private boolean isAlive(){
            return isAlive(new Date());
        }
        private Object aliveData(String detailFullName,Date date){
            Object[] objects = newestDetails.get(detailFullName);
            if (objects==null){
                return null;
            }
            if(((Date)objects[1]).before(date)){
                return null;
            }else{
                return objects[0];
            }
        }
        private Object aliveData(String detailFullName){
            return aliveData(detailFullName,new Date());
        }
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
        if(!hosts.containsKey(hostName)){
            hosts.put(hostName,cacheData);
        }else{
            CacheData existeCD = hosts.get(hostName);
            if(cacheData.getAlive()!=null){
                existeCD.setAlive(cacheData.getAlive());
                existeCD.setAliveDeadTime(cacheData.getAliveDeadTime());
            }
            cacheData.getNewestDetails().forEach((name,dm)->{
                existeCD.getNewestDetails().put(name,dm);
            });
        }
    }

    private CacheData get(String clusterName,String hostName){
        Map<String, CacheData> hosts = cacheDatas.get(clusterName);
        if(hosts==null){
            return null;
        }
        return hosts.get(hostName);
    }

    /*********************   存取方法   ********************/

    public void record(String clusterName,String hostName,RRDModel rrdModel){
        if(rrdModel==null) return;
        CacheData cacheData=new CacheData();
        Map<String, Object[]> datas = createData(rrdModel);
        cacheData.setNewestDetails(datas);
        cacheData.setAlive(isAlive(rrdModel));
        put(clusterName,hostName,cacheData);
    }

    public void recordData(String clusterName,String hostName,RRDModel rrdModel){
        Map<String, Object[]> datas = createData(rrdModel);
        CacheData cacheData=new CacheData();
        cacheData.setNewestDetails(datas);
        put(clusterName,hostName,cacheData);
    }

    public void recordAlive(String clusterName,String hostName,RRDModel rrdModel){
        if(rrdModel==null) return;
        CacheData cacheData=new CacheData();
        cacheData.setAlive(isAlive(rrdModel));
        put(clusterName,hostName,cacheData);
    }

    /**
     * 获取最新数据，若结果中没有给定的key，则表示没有该key的缓存数据
     * @param clusterName
     * @param hostName
     * @param detailFullName
     * @return
     */
    public Map<String,Object> newestData(String clusterName,String hostName,String... detailFullName){
        Map<String, CacheData> hosts = cacheDatas.get(clusterName);
        if (hosts==null) return null;
        CacheData cacheData = hosts.get(hostName);
        if (cacheData==null) return null;
        Date now=new Date();
        Map<String,Object> res=new HashMap<>();
        Arrays.stream(detailFullName).forEach(name->{
            Object o = cacheData.aliveData(name, now);
            if (o!=null){
                res.put(name,o);
            }
        });
        return res;
    }

    /*********************       **********************/

    private Map<String,Object[]> createData(RRDModel rrdModel){
        if(rrdModel==null) return new HashMap<String,Object[]>();
        Date deadTime= DateUtil.timeAfter(Calendar.SECOND,Constant.cacheValidPeriod);
        Map<String,Object[]> newestDetails=new HashMap<>();
        if (rrdModel.getList()!=null){
            rrdModel.getList().forEach(rdm->{
                if(rdm.getNewestData()!=null){
                    newestDetails.put(rdm.getName(),new Object[]{rdm.getNewestData(),deadTime});
                }
            });
        }
        return newestDetails;
    }

    private boolean isAlive(RRDModel rrdModel){
        if (rrdModel==null||rrdModel.getList()==null){
            return false;
        }
        boolean alive=false;
        for (DataModel dm:rrdModel.getList()
             ) {
            if (dm.getNewestData()!=null){
                alive=true;
                break;
            }
        }
        return alive;
    }
}
