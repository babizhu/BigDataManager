package com.bbz.bigdata.platform.filter;

/**
 * Created by liukun on 16/4/23.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.injector.ObjectNavlPairInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.*;
import org.nutz.mvc.upload.injector.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.injector.ObjectNavlPairInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.FastUploading;
import org.nutz.mvc.upload.FieldMeta;
import org.nutz.mvc.upload.Html5Uploading;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadException;
import org.nutz.mvc.upload.UploadingContext;
import org.nutz.mvc.upload.Uploads;
import org.nutz.mvc.upload.injector.FileInjector;
import org.nutz.mvc.upload.injector.FileMetaInjector;
import org.nutz.mvc.upload.injector.InputStreamInjector;
import org.nutz.mvc.upload.injector.MapListInjector;
import org.nutz.mvc.upload.injector.MapSelfInjector;
import org.nutz.mvc.upload.injector.ReaderInjector;
import org.nutz.mvc.upload.injector.TempFileArrayInjector;
import org.nutz.mvc.upload.injector.TempFileInjector;

public class UploadAdaptor extends PairAdaptor{
    private static final Log log = Logs.get();
    private UploadingContext context;

    public UploadAdaptor() throws IOException{
        this.context = new UploadingContext( File.createTempFile("nutz", (String)null).getParent());
    }

    public UploadAdaptor(UploadingContext context) {
        this.context = context;
    }

    public UploadAdaptor(String path) {
        this.context = new UploadingContext(path);
    }

    public UploadAdaptor(String path, int buffer) {
        this(path);
        this.context.setBufferSize(buffer);
    }

    public UploadAdaptor(String path, int buffer, String charset) {
        this(path);
        this.context.setBufferSize(buffer);
        this.context.setCharset(charset);
    }

    public UploadAdaptor(String path, int buffer, String charset, int poolSize) {
        this.context = new UploadingContext(new NutFilePool(path, (long)poolSize));
        this.context.setBufferSize(buffer);
        this.context.setCharset(charset);
    }

    public UploadAdaptor(String path, int buffer, String charset, int poolSize, int maxFileSize) {
        this.context = new UploadingContext(new NutFilePool(path, (long)poolSize));
        this.context.setBufferSize(buffer);
        this.context.setCharset(charset);
        this.context.setMaxFileSize(maxFileSize);
    }

    public UploadingContext getContext() {
        return this.context;
    }

    public Object[] adapt( ServletContext sc, HttpServletRequest req, HttpServletResponse resp, String[] pathArgs) {
        if(!Mvcs.getActionContext().getMethod().toGenericString().equals(this.method.toGenericString())) {
            throw new IllegalArgumentException(String.format("Method miss match: expect %s but %s. using Ioc? set singleton=false, pls", new Object[]{this.method, Mvcs.getActionContext().getMethod()}));
        } else {
            return super.adapt(sc, req, resp, pathArgs);
        }
    }

    protected ParamInjector evalInjectorBy( Type type, Param param) {
        Class clazz = Lang.getTypeClass(type);
        if(clazz == null) {
            if(log.isWarnEnabled()) {
                log.warnf("!!Fail to get Type Class : type=%s , param=%s", new Object[]{type, param});
            }

            return null;
        } else if(Map.class.isAssignableFrom(clazz)) {
            return new MapSelfInjector();
        } else if(null == param) {
            return super.evalInjectorBy(type, (Param)null);
        } else {
            String paramName = param.value();
            return (ParamInjector)(File.class.isAssignableFrom(clazz)?new FileInjector(paramName):(FieldMeta.class.isAssignableFrom(clazz)?new FileMetaInjector(paramName):(TempFile.class.isAssignableFrom(clazz)?new TempFileInjector(paramName):(InputStream.class.isAssignableFrom(clazz)?new InputStreamInjector(paramName):(Reader.class.isAssignableFrom(clazz)?new ReaderInjector(paramName):(List.class.isAssignableFrom(clazz)?(!Strings.isBlank(paramName) && paramName.startsWith("::")?new ObjectNavlPairInjector(paramName.substring(2), type):new MapListInjector(paramName)):(TempFile[].class.isAssignableFrom(clazz)?new TempFileArrayInjector(paramName):super.evalInjectorBy(type, param))))))));
        }
    }

    public Map<String, Object> getReferObject(ServletContext sc, HttpServletRequest request, HttpServletResponse response, String[] pathArgs) {
        Map var7;
        try {
            String e;
//            if(!"POST".equals(request.getMethod()) && !"PUT".equals(request.getMethod())) {
//                e = "Not POST or PUT, Wrong HTTP method! --> " + request.getMethod();
//                throw (IllegalArgumentException)Lang.makeThrow(IllegalArgumentException.class, e, new Object[0]);
//            }

            e = request.getContentType();
            if(e == null) {
//                throw (IllegalArgumentException)Lang.makeThrow(IllegalArgumentException.class, "Content-Type is NULL!!", new Object[0]);
            }

            if(e == null || e.contains("multipart/form-data")) {
                if(log.isDebugEnabled()) {
                    log.debug("Select Html4 Form upload parser --> " + request.getRequestURI());
                }

                FastUploading ing1 = new FastUploading();
                var7 = ing1.parse(request, this.context);
                return var7;
            }

            if(!e.contains("application/octet-stream")) {
                if(e.contains("application/x-www-form-urlencoded")) {
                    log.warn("Using form upload ? You forgot this --> enctype=\'multipart/form-data\' ?");
                }

                throw (IllegalArgumentException)Lang.makeThrow(IllegalArgumentException.class, "Unknow Content-Type : " + e, new Object[0]);
            }

            if(log.isDebugEnabled()) {
                log.debug("Select Html5 Stream upload parser --> " + request.getRequestURI());
            }

            Html5Uploading ing = new Html5Uploading();
            var7 = ing.parse(request, this.context);
        } catch (UploadException var11) {
            throw Lang.wrapThrow(var11);
        } finally {
            Uploads.removeInfo(request);
        }

        return var7;
    }
}
