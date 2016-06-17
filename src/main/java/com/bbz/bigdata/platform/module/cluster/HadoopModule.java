package com.bbz.bigdata.platform.module.cluster;

import com.bbz.bigdata.platform.module.cluster.modelview.rrdjsonmodel.RRDJsonModel;
import com.bbz.bigdata.platform.rrdtool.exception.BussException;
import com.bbz.bigdata.platform.rrdtool.rrdmodel.RRDModel;
import com.bbz.bigdata.platform.service.ClusterService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by liu_k on 2.
 * 集群相关操作
 */

@IocBean
@At("api/hadoop")
@Ok("json")
@Fail("http:500")
public class HadoopModule {
    @Inject
    protected ClusterService clusterService;

    @At
    public RRDJsonModel hdfsCapacityInfo(@Param("nodeId") int nodeId, @Param("timePeriod") Integer timePeriod) throws ParseException, BussException {
        RRDModel rrdJM = clusterService.hdfsCapacityInfo(nodeId,timePeriod);
        return new RRDJsonModel(rrdJM);
    }

    @At
    public Map<String, Object> newestHdfsCapacityData(@Param("nodeId") int nodeId) throws ParseException, BussException {
        Map<String, Object> stringObjectMap = clusterService.newestHdfsCapacityData(nodeId);
        return stringObjectMap;
    }

}
