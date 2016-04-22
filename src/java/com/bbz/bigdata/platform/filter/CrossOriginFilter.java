package com.bbz.bigdata.platform.filter;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.VoidView;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by liu_k on 2016/4/15.
 */
public class CrossOriginFilter implements ActionFilter{
    private static final Log log = Logs.get();
    protected String origin;
    protected String methods;
    protected String headers;
    protected String credentials;

    public CrossOriginFilter() {
        this("*", "get, post, put, delete, options", "origin, content-type, accept", "true");
    }

    public CrossOriginFilter(String origin, String methods, String headers, String credentials) {
        this.origin = origin;
        this.methods = methods;
        this.headers = headers;
        this.credentials = credentials;
    }

    public View match( ActionContext ac) {
        if("OPTIONS".equals(ac.getRequest().getMethod()) || true ) {
            if(log.isDebugEnabled()) {
                log.debugf("Feedback -- [%s] [%s] [%s] [%s]", new Object[]{this.origin, this.methods, this.headers, this.credentials});
            }

            HttpServletResponse resp = ac.getResponse();
            if(!Strings.isBlank(this.origin)) {
                resp.addHeader("Access-Control-Allow-Origin", this.origin);
            }

            if(!Strings.isBlank(this.methods)) {
                resp.addHeader("Access-Control-Allow-Methods", this.methods);
            }

            if(!Strings.isBlank(this.headers)) {
                resp.addHeader("Access-Control-Allow-Headers", this.headers);
            }

            if(!Strings.isBlank(this.credentials)) {
                resp.addHeader("Access-Control-Allow-Credentials", this.credentials);
            }

            resp.addHeader( "Content-Type", "application/json" );


            return null;
        } else {
            return null;
        }
    }
}
