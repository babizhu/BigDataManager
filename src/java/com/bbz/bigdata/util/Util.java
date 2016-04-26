package com.bbz.bigdata.util;

/**
 * Created by liu_k on 2016/4/26.
 */
public enum  Util{
    INSTANCE;

    /**
     * 生成错误提示的json内容
     * @return
     */
    public String buildErrorMsg( int errId, String args){
        return "{\"errId\":" + errId + ",\"args\":\"" + args + "\"}";
    }


}
