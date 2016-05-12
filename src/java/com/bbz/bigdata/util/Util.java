package com.bbz.bigdata.util;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by liu_k on 2016/4/26.
 *
 */
public enum  Util{
    INSTANCE;

    /**
     * 生成错误提示的json内容
     * @return  错误json提示
     */
    public String buildErrorMsg( int errId, String args){
        return "{\"errId\":" + errId + ",\"args\":\"" + args + "\"}";
    }

    /**
     * 统一返回错误信息
     *
     * @param response response
     * @param errId    errid
     * @param args     错误参数
     * @return 格式化好的json错误提示
     */
    public String buildErrorResponse( HttpServletResponse response, int errId, String args ){
        response.setStatus( 500 );
        return buildErrorMsg( errId, args );
    }
}
