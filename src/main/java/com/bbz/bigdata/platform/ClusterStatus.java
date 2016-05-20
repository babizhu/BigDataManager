package com.bbz.bigdata.platform;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu_k on 2016/5/20.
 * 集群的状态
 */
public enum ClusterStatus{
    RUN( 1 ),//正常运行
    FALURE( 2 ),//部分服务失败
    STOP( 3 ),//停止运行
    ;
    private final int number;

    private static final Map<Integer, ClusterStatus> numToEnum = new HashMap<Integer, ClusterStatus>();

    static{
        for( ClusterStatus t : values() ) {

            ClusterStatus s = numToEnum.put( t.number, t );
            if( s != null ) {
                throw new RuntimeException( t.number + "重复了" );
            }
        }
    }

    ClusterStatus( int number ){
        this.number = number;
    }

    public int toNum(){
        return number;
    }

    public static ClusterStatus fromNum( int n ){
        return numToEnum.get( n );
    }
}
