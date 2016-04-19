package com.bbz.bigdata.platform.module;

import com.bbz.tool.common.StrUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        FileSystem fs = null;
        try {
            fs = FileSystem.get( new Configuration() );
            FileStatus[] status = fs.listStatus( new Path( path ) );
            String json = "{\"FileStatuses\":{\"FileStatus\":[";
            String content = "";
            for( FileStatus file : status ) {
                content += this.buildFileListJson( file );
            }
            if( !content.isEmpty() ){
                content = StrUtil.removeLastChar(content);
                json += content;
            }

            json += "]}}";
            System.out.println(json);
            return json;
        } catch( IOException e ) {
            e.printStackTrace();
        }


        return "{\"errId\":500,\"args\":\"test\"}";
    }

    String buildFileListJson( FileStatus file ){
        String name = file.getPath().getName();
        String module = "{\"accessTime\":"+file.getAccessTime()+"," +
                "\"blockSize\":"+file.getBlockSize()+"," +
//                "\"childrenNum\":"+fil+"," +
                "\"fileId\":\""+ name+"\"," +
                "\"group\":\""+file.getGroup()+"\"," +
                "\"length\":"+file.getLen()+"," +
                "\"modificationTime\":"+file.getModificationTime()+"," +
                "\"owner\":\""+file.getOwner()+"\"," +
                "\"pathSuffix\":\"" + name + "\"," +
                "\"permission\":\""+file.getPermission()+"\"" +
                ",\"replication\":"+file.getReplication()+"," +
//                "\"storagePolicy\":"+file.isFile()+"," +
                "\"isFile\":"+ file.isFile()+"},";
        return module;
    }
}
