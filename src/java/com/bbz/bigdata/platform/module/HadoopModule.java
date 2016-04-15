package com.bbz.bigdata.platform.module;

import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liu_k on 2016/4/15.
 */
@At("/hadoop")
@Ok("json")
@Fail("http:500")
public class HadoopModule{
    @At
    @Ok("raw")
    public Object count( HttpServletRequest req, HttpServletResponse response ){
        response.addHeader( "Access-Control-Allow-Origin", "*" );
        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
        return "{'count':'100'}";
    }

    @At
    @Ok("raw")

    public Object fileList( @Param("path") String path, HttpServletRequest req, HttpServletResponse response ){
        response.addHeader( "Access-Control-Allow-Origin", "*" );
        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
        response.addHeader( "Content-Type", "application/json" );
//        System.out.println("path=" + path );
        Response res = Http.get( "http://master:50070/webhdfs/v1" + path +"?op=LISTSTATUS" );
        return res.getContent();
    }
}
