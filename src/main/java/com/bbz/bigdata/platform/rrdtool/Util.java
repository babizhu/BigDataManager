package com.bbz.bigdata.platform.rrdtool;

/**
 * Created by weiran on 2016/6/14.
 */
public class Util {

    public static <T> T newestData(T[] data,int dataIntervalTime){
        if(data==null){
            return null;
        }
        for (int i=data.length-1;i>=0&&i>=data.length-Constant.thresholdTimeForNewestData/dataIntervalTime;i--){
            if (data[i]!=null){
                return data[i];
            }
        }
        return null;
    }
}
