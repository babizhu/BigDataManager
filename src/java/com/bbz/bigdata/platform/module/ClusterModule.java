package com.bbz.bigdata.platform.module;

import com.bbz.bigdata.platform.bean.Cluster;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

/**
 * Created by liu_k on 2016/5/11.
 *
 */

@IocBean
@At("api/cluster")
@Ok("json")
@Fail("http:500")
public class ClusterModule{
    @Inject
    protected Dao dao;
    @At
    public int count() {
        return dao.count(Cluster.class);
    }
}
