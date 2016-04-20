package com.bbz.bigdata.platform.module;

import com.bbz.tool.common.StrUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by liu_k on 2016/4/15.
 * 处理hadoop相关请求
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
    public Object getFilesData( @Param("path") String path, HttpServletRequest req, HttpServletResponse response ){
        response.addHeader( "Access-Control-Allow-Origin", "*" );
        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
        response.addHeader( "Content-Type", "application/json" );

        FileSystem fs;
        try {
            fs = FileSystem.get( new Configuration() );
            Boolean isFile = fs.isFile( new Path( path ) );
            String result = "{\"currentPathIsFile\": ";
            result += isFile + ",";
            result += "\"data\":";
            if( isFile ) {
                result += buildFileJson( fs, path );
            } else {
                result += buildDirectoryJson( fs, path );
            }

            result += "}";

            return result;
        } catch( IOException e ) {
            e.printStackTrace();
            response.setStatus( 500 );
            return "{\"errId\":500,\"args\":\"" + e.getMessage() + "\"}";
        }

    }

    private String buildFileJson( FileSystem fs, String path ) throws IOException{

        fs = FileSystem.get( new Configuration() );
        FSDataInputStream fin = fs.open( new Path( path ) );
        BufferedReader in = null;
        String line;
        String json = "{\"FileContent\":{\"content\":\"";
        try {
            in = new BufferedReader( new InputStreamReader( fin, "UTF-8" ) );
            while( (line = in.readLine()) != null ) {
                System.out.println( line );
                json += line.replace( "\"","\\\"" );
            }
        } finally {
            if( in != null ) {
                in.close();
            }
        }

        json += "\"}}";
        return json;
    }

    private String buildDirectoryJson( FileSystem fs, String path ) throws IOException{
        FileStatus[] status = fs.listStatus( new Path( path ) );

        String json = "{\"FileStatus\":[";
        String content = "";
        for( FileStatus file : status ) {
            content += this.buildFileListJson( file );
        }
        if( !content.isEmpty() ) {
            content = StrUtil.removeLastChar( content );
            json += content;
        }

        json += "]}";
        System.out.println( json );
        return json;
    }

    String buildFileListJson( FileStatus file ){
        String name = file.getPath().getName();
        return "{\"accessTime\":" + file.getAccessTime() + "," +
                "\"blockSize\":" + file.getBlockSize() + "," +
//                "\"childrenNum\":"+fil+"," +
                "\"fileId\":\"" + name + "\"," +
                "\"group\":\"" + file.getGroup() + "\"," +
                "\"length\":" + file.getLen() + "," +
                "\"modificationTime\":" + file.getModificationTime() + "," +
                "\"owner\":\"" + file.getOwner() + "\"," +
                "\"pathSuffix\":\"" + name + "\"," +
                "\"permission\":\"" + file.getPermission() + "\"" +
                ",\"replication\":" + file.getReplication() + "," +
//                "\"storagePolicy\":"+file.isFile()+"," +
                "\"isFile\":" + file.isFile() + "},";
    }
}
