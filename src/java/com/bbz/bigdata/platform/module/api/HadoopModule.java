package com.bbz.bigdata.platform.module.api;

import com.bbz.tool.common.StrUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liu_k on 2016/4/15.
 * 处理hadoop相关请求
 */
@At( "api/hadoop" )
@Ok( "json" )
@Fail( "http:500" )
public class HadoopModule{
    public static final int BLOCK_SIZE = 4096;

    @At
    @Ok( "raw" )
    public Object count( HttpServletRequest req, HttpServletResponse response ){
//        response.addHeader( "Access-Control-Allow-Origin", "*" );
//        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
        return "{'count':'100'}";
    }

    //    @AdaptBy(type=UploadAdaptor.class, args={"${app.root}/WEB-INF/tmp/user_avatar", "8192", "utf-8", "20000", "102400"})
//    @POST
//    @Ok(">>:/user/profile")
    @Ok( "raw" )
    @At
//    @At("upload")
    @Filters( {@By( type = CrossOriginFilter.class, args = {"*", "get, post, put, delete, options", " X-Requested-With,origin, content-type, accept", "true"} )} )
    @AdaptBy( type = UploadAdaptor.class, args = {"${app.root}/WEB-INF/tmp"} )

    public String upload( @Param( "file" ) TempFile tf,
                          @Param( "path" ) String path,
//                             @Attr(scope= Scope.SESSION, value="me")int userId,
//                             AdaptorErrorContext err,
                          HttpServletRequest req,
                          HttpServletResponse response ) throws IOException{
        response.addHeader( "Access-Control-Allow-Origin", "*" );
        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
        response.addHeader( "Content-Type", "application/json" );

        tf.write( "/Users/liukun/work/temp/" + tf.getSubmittedFileName() );

        return buildUploadResult( tf.getSubmittedFileName() );
    }

    private String buildUploadResult(String fileName){
//        String result = "{" +
//                "  \"file\": {" +
//                "  \"uid\": \"uid\",      " +
//                "  \"name\": \""+fileName+"\",   " +
//                "  \"status\": \"done\",  " +
//                "  \"response\": '{\"status\":\"success\"}'" +
//                "}," +
//                "  fileList: []," +
//                "  event: { }" +
//                "}";
        String result = "{\"response\":{\"status\":\"success\"}}";

        return result;
    }

    @At
    @Ok( "raw" )
    public String getFilesData( @Param( "path" ) String path,
                                @Param( "readAsText" ) boolean readAsText,
                                @Param( "block" ) long block,
                                HttpServletRequest req,
                                HttpServletResponse response ){
//        response.addHeader( "Access-Control-Allow-Origin", "*" );
//        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
//        response.addHeader( "Content-Type", "application/json" );
        String result = "{\"currentPathIsFile\": ";
        result += false + ",";
        result += "\"data\":";
        String ret = "{\"FileStatus\":[\n" +
                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":6,\"fileId\":16392,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1458543698158,\"owner\":\"hadoop\",\"pathSuffix\":\"input\",\"permission\":\"755\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"},\n" +
                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":8,\"fileId\":16412,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1457593033695,\"owner\":\"hadoop\",\"pathSuffix\":\"output\",\"permission\":\"755\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"},\n" +
                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":2,\"fileId\":16386,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1453911076738,\"owner\":\"hadoop\",\"pathSuffix\":\"tmp\",\"permission\":\"770\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"},\n" +
                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":1,\"fileId\":16889,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1456197544321,\"owner\":\"hadoop\",\"pathSuffix\":\"user\",\"permission\":\"755\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"}\n" +
                "]}";
        result += ret;
        result += "}";
//
        return result;
//            return res.getContent();
//        return ret;
//        FileSystem fs;
//        try {
//            fs = FileSystem.get( new Configuration() );
//            Boolean isFile = fs.isFile( new Path( path ) );
//            String result = "{\"currentPathIsFile\": ";
//            result += isFile + ",";
//            result += "\"data\":";
//            if( isFile ) {
//                result += buildFileJson( fs, path, readAsText, block );
//            } else {
//                result += buildDirectoryJson( fs, path );
//            }
//
//            result += "}";
//
//            return result;
//        } catch(org.apache.hadoop.security.AccessControlException e){
//            response.setStatus( 500 );
//            return "{\"errId\":501,\"args\":\"" + path + "\"}";
//        }catch( Exception e ) {
//            e.printStackTrace();
//            response.setStatus( 500 );
//            return "{\"errId\":500,\"args\":\"" + e.getMessage() + "\"}";
//        }

    }

    /**
     * 构建文查看件内容相关的json
     *
     * @param fs         fs
     * @param path       要读取文件的路径
     * @param readAsText 读取模式：true：文本模式 false：二进制模式
     * @param block      读取文件的块（以4096字节为一块）,从0开始计数
     * @return 文件的json字符串
     * @throws IOException
     */
    private String buildFileJson( FileSystem fs, String path, boolean readAsText, long block ) throws Exception{
        FSDataInputStream in = null;
        String json = "{\"fileContent\":{\"content\":\"";
        try{

            FileStatus status = fs.getFileStatus( new Path( path ) );
            in = fs.open( new Path( path ) );

            long fileBlock = status.getLen() / BLOCK_SIZE;//文件的总块数
            if( block > fileBlock ){
                block = fileBlock;
            }

            int realLen;
            if( block < fileBlock ){
                realLen = BLOCK_SIZE;
            }else{
                realLen = (int) (status.getLen() - block * BLOCK_SIZE);//可以放心的转，不会超过BLOCK_SIZE
            }

            byte[] contents = new byte[realLen];//一次最多仅允许读BLOCK_SIZE字节

            long fileOffset = block * BLOCK_SIZE;

            in.readFully( fileOffset, contents );
//            IOUtils.readFully(in, contents, 0, realLen );

            json += buildFileContent( readAsText, contents );
            json += "\"},\"fileStatus\":";
            json += StrUtil.removeLastChar( buildFileStatusJson( status ) );

        }finally{
            if( in != null ){
                in.close();
            }
        }


        json += "}";
        return json;
    }

    private String bytesToHexString( byte[] src ){
        StringBuilder stringBuilder = new StringBuilder( "" );
        if( src == null || src.length <= 0 ){
            return null;
        }
        for( byte aSrc : src ){
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString( v );
            if( hv.length() < 2 ){
                stringBuilder.append( 0 );
            }
            stringBuilder.append( hv );
        }
        return stringBuilder.toString();
    }

    /**
     * 以文本方式或者二进制方式获取文件内容
     *
     * @param readAsText 读取模式：true：文本模式 false：二进制模式
     * @param contents   文件内容数组
     * @return 文件内容字符串
     */
    private String buildFileContent( boolean readAsText, byte[] contents ){
        if( readAsText ){
            String json = new String( contents ).replace( "\"", "\\\"" );//处理文件里面的"
            json = json.replace( "\n", "\\n" );
            json = json.replace( "\r", "\\r" );
            json = json.replace( "\t", "\\t" );
            return json;
        }else{
            return bytesToHexString( contents );
        }
    }

    private String buildDirectoryJson( FileSystem fs, String path ) throws IOException{
        FileStatus[] status = fs.listStatus( new Path( path ) );

        String json = "{\"FileStatus\":[";
        String content = "";
        for( FileStatus file : status ){
            content += this.buildFileStatusJson( file );
        }
        if( !content.isEmpty() ){
            content = StrUtil.removeLastChar( content );
            json += content;
        }

        json += "]}";
        System.out.println( json );
        return json;
    }

    String buildFileStatusJson( FileStatus file ){
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
