package com.bbz.bigdata.platform.module.api;

import com.bbz.bigdata.util.Util;
import com.bbz.tool.common.StrUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liu_k on 2016/4/15.
 * 处理hadoop相关请求，关于权限部分，最简单的方案似乎是在hdfs-site.xml中增加
 * <property>
 * <name>dfs.permissions.enabled</name>
 * <value>false</value>
 * </property>
 */
@At("api/hadoop")
@Ok("json")
@Fail("http:500")
public class HadoopModule{
    public static final int BLOCK_SIZE = 4096;

    @At
    @Ok("raw")
    public Object count( HttpServletRequest req, HttpServletResponse response ){
//        response.addHeader( "Access-Control-Allow-Origin", "*" );
//        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
        return "{'count':'100'}";
    }

    //    @AdaptBy(type=UploadAdaptor.class, args={"${app.root}/WEB-INF/tmp/user_avatar", "8192", "utf-8", "20000", "102400"})
//    @POST
    @Ok("raw")
    @At
    @Filters({@By(type = CrossOriginFilter.class, args = {"*", "get, post, put, delete, options", " X-Requested-With,origin, content-type, accept", "true"})})
    @AdaptBy(type = UploadAdaptor.class, args = {"${app.root}/WEB-INF/tmp"})

    public String upload( @Param("file") TempFile tf,
                          @Param("path") String path,
                          HttpServletRequest req,
                          HttpServletResponse response ) throws IOException{
        response.addHeader( "Access-Control-Allow-Origin", "*" );
        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
        response.addHeader( "Content-Type", "application/json" );

        FileSystem fs = null;
        FSDataOutputStream fsd = null;
        try {
            Configuration conf = new Configuration();
            fs = FileSystem.get( conf );

            Path uploadFilePath = getRealUploadPath( fs, path, tf.getSubmittedFileName() );

            int errId;
            if( (errId = check( fs, uploadFilePath )) != 0 ) {
//                buildErrorResponse( response, errId, uploadFilePath.toString() );
                return buildUploadResult( errId, uploadFilePath.toString() );
            }

            fsd = fs.create( uploadFilePath );
            IOUtils.copyBytes( tf.getInputStream(), fsd.getWrappedStream(), tf.getSize(), false );

        } catch( Exception e ) {
            e.printStackTrace();
            return buildUploadResult( 500, e.getMessage() );


        } finally {
            if( fs != null ) {
                fs.close();
            }
            if( fsd != null ) {
                fsd.close();
            }
        }
        return buildUploadResult( 0, null );
    }

    /**
     * 把客户端上传的currentPath转换为实际的目录，
     * 有可能客户端上传的currentPath其实是一个文件，这样就要获取此文件所在的目录作为要操作的真实目录
     *
     * @param fs
     * @param currentPath
     * @return 目录名末尾自动加上"/"
     */
    private String getRealDirectory( FileSystem fs, String currentPath ) throws IOException{
        Boolean isFile = fs.isFile( new Path( currentPath ) );
        String directory = currentPath;
        if( isFile ) {
            directory = directory.substring( 0, directory.lastIndexOf( "/" ) );
        }
        return directory.endsWith( "/" ) ? directory : directory + "/";
    }

    /**
     * 计算上传文件的真实目录，有可能客户端上传的currentPath其实是一个文件，这样就要获取此文件所在的目录作为上传目录
     *
     * @param fs             fs
     * @param currentPath    path
     * @param uploadFileName 上传的文件名
     * @return 真实目录的path
     */
    private Path getRealUploadPath( FileSystem fs, String currentPath, String uploadFileName ) throws IOException{
        return new Path( getRealDirectory( fs, currentPath ) + uploadFileName );
    }

    /**
     * 检测上传的文件是否合法，比如
     * 文件重名
     *
     * @param fs             fs
     * @param uploadFilePath 要上传的文件路径
     * @return 错误id，0代表成功
     */
    private int check( FileSystem fs, Path uploadFilePath ) throws IOException{

        if( fs.exists( uploadFilePath ) ) {
            return 202;
        }
        return 0;
    }

    /**
     * 上传组件的专用返回结果函数
     *
     * @param errId 错误代码 =0 表示成功
     * @param args  参数
     * @return
     */
    private String buildUploadResult( int errId, String args ){

        if( errId != 0 ) {
            return "{\"error\":{\"errorId\":" + errId + ",\"args\":\"" + args + "\"}}";

        }
        return "{\"response\":{\"status\":\"success\"}}";
    }

    /**
     * 统一入口处理
     * 1、修改文件（夹）名称
     * 2、删除文件（夹）
     * 3、添加文件夹
     *
     * @param path          要修改的路径
     * @param op            操作id，见上
     * @param args          操作参数
     * @param req           req
     * @param response      response
     * @return              返回的json
     */
    @At
    @Ok("raw")
    public String operation( @Param("path") String path,
                             @Param("op") int op,
                             @Param("args") String args,
                             HttpServletRequest req,
                             HttpServletResponse response ){

        FileSystem fs = null;
        int errId = 0;
        try {
            fs = FileSystem.get( new Configuration() );
            String realDirector = this.getRealDirectory( fs, path );
            switch( op ) {
                case 3://添加文件夹

                    Path dst = new Path( realDirector + args );
                    errId = addDirectory( fs, dst );
                    break;
                case 2://删除文件（夹）
                    boolean recursiveDelete = Boolean.parseBoolean( args );
                    errId = deleteFile( fs,path,recursiveDelete );
                    break;
                case 1://重命名
                    errId = rename( fs,path,args );
                    break;
            }

            if( errId == 0 ){
                return buildSuccessResponse();
            }else {
                return buildErrorResponse( response,errId,"" );
            }
        } catch( Exception e ) {
            e.printStackTrace();

        } finally {
            if( fs != null ) {
                try {
                    fs.close();
                } catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        return buildSuccessResponse();

    }

    /**
     * 删除文件（夹）
     * @param fs                fs
     * @param path              要删除的文件路径
     * @param recursiveDelete   如果是文件夹，那么是否递归删除子目录
     * @return                  错误id，0为执行成功
     */
    private int deleteFile( FileSystem fs, String path, boolean recursiveDelete ){
        try {
            fs.delete( new Path( path ), recursiveDelete );//false 为是否递归删除
        }catch( Exception e ){
            return 203;
        }
        return 0;

    }

    private int rename( FileSystem fs, String fromFile, String toFile ) throws IOException{
        fs.rename( new Path( fromFile ), new Path( toFile ) );
        return 0;

    }

    private int addDirectory( FileSystem fs, Path dst) throws IOException{
        if( fs.exists( dst ) ) {
//            return buildErrorResponse( response, 202, dst.toString() );
            return 202;
        }
        fs.mkdirs( dst );
        return 0;
    }

    @At
    @Ok("raw")
    public String getFilesData( @Param("path") String path,
                                @Param("readAsText") boolean readAsText,
                                @Param("block") long block,
                                HttpServletRequest req,
                                HttpServletResponse response ) throws IOException{

//        String result = "{\"currentPathIsFile\": ";
//        result += false + ",";
//        result += "\"data\":";
//        String ret = "{\"FileStatus\":[\n" +
//                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":6,\"fileId\":16392,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1458543698158,\"owner\":\"hadoop\",\"pathSuffix\":\"input\",\"permission\":\"755\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"},\n" +
//                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":8,\"fileId\":16412,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1457593033695,\"owner\":\"hadoop\",\"pathSuffix\":\"output\",\"permission\":\"755\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"},\n" +
//                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":2,\"fileId\":16386,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1453911076738,\"owner\":\"hadoop\",\"pathSuffix\":\"tmp\",\"permission\":\"770\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"},\n" +
//                "{\"accessTime\":0,\"blockSize\":0,\"childrenNum\":1,\"fileId\":16889,\"group\":\"supergroup\",\"length\":0,\"modificationTime\":1456197544321,\"owner\":\"hadoop\",\"pathSuffix\":\"user\",\"permission\":\"755\",\"replication\":0,\"storagePolicy\":0,\"type\":\"DIRECTORY\"}\n" +
//                "]}";
//        result += ret;
//        result += "}";
////
//        return result;
//            return res.getContent();
//        return ret;
        FileSystem fs = null;
        try {
            fs = FileSystem.get( new Configuration() );
            Boolean isFile = fs.isFile( new Path( path ) );
            String result = "{\"currentPathIsFile\": ";
            result += isFile + ",";
            result += "\"data\":";
            if( isFile ) {
                result += buildFileJson( fs, path, readAsText, block );
            } else {
                result += buildDirectoryJson( fs, path );
            }

            result += "}";

            return result;
        } catch( org.apache.hadoop.security.AccessControlException e ) {
            response.setStatus( 500 );
            return "{\"errId\":501,\"args\":\"" + path + "\"}";
        } catch( Exception e ) {
            e.printStackTrace();
            response.setStatus( 500 );
            return "{\"errId\":500,\"args\":\"" + e.getMessage() + "\"}";
        } finally {
            if( fs != null ) {
                fs.close();
            }
        }

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
        try {

            FileStatus status = fs.getFileStatus( new Path( path ) );
            in = fs.open( new Path( path ) );

            long fileBlock = status.getLen() / BLOCK_SIZE;//文件的总块数
            if( block > fileBlock ) {
                block = fileBlock;
            }

            int realLen;
            if( block < fileBlock ) {
                realLen = BLOCK_SIZE;
            } else {
                realLen = (int) (status.getLen() - block * BLOCK_SIZE);//可以放心的转，不会超过BLOCK_SIZE
            }

            byte[] contents = new byte[realLen];//一次最多仅允许读BLOCK_SIZE字节

            long fileOffset = block * BLOCK_SIZE;

            in.readFully( fileOffset, contents );
//            IOUtils.readFully(in, contents, 0, realLen );

            json += buildFileContent( readAsText, contents );
            json += "\"},\"fileStatus\":";
            json += StrUtil.removeLastChar( buildFileStatusJson( status ) );

        } finally {
            if( in != null ) {
                in.close();
            }
        }


        json += "}";
        return json;
    }

    private String bytesToHexString( byte[] src ){
        StringBuilder stringBuilder = new StringBuilder( "" );
        if( src == null || src.length <= 0 ) {
            return null;
        }
        for( byte aSrc : src ) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString( v );
            if( hv.length() < 2 ) {
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
        if( readAsText ) {
//            String json = new String( contents ).replace( "\"", "\\\"" );//处理文件里面的"
//            json = json.replace( "\n", "\\n" );
//            json = json.replace( "\r", "\\r" );
//            json = json.replace( "\t", "\\t" );
//

            String json = Base64.encodeBase64String( contents );
            json = json.replace( "\n", "" );
            json = json.replace( "\r", "" );
            json = json.replace( "\t", "" );
            return json;
        } else {
            return bytesToHexString( contents );
        }
    }

    private String buildDirectoryJson( FileSystem fs, String path ) throws IOException{
        FileStatus[] status = fs.listStatus( new Path( path ) );

        String json = "{\"FileStatus\":[";
        String content = "";
        for( FileStatus file : status ) {
            content += this.buildFileStatusJson( file );
        }
        if( !content.isEmpty() ) {
            content = StrUtil.removeLastChar( content );
            json += content;
        }

        json += "]}";
        System.out.println( json );
        return json;
    }

    private String buildFileStatusJson( FileStatus file ){
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

    /**
     * 统一返回错误信息
     *
     * @param response response
     * @param errId    errid
     * @param args     错误参数
     * @return 格式化好的json错误提示
     */
    private String buildErrorResponse( HttpServletResponse response, int errId, String args ){
        response.setStatus( 500 );
        return Util.INSTANCE.buildErrorMsg( errId, args );
    }


    private String buildSuccessResponse(){
        return "{\"success\":true}";
    }
}
