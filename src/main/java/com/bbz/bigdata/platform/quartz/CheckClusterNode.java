package com.bbz.bigdata.platform.quartz;

import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by liu_k on 2016/6/13.
 *
 */
@IocBean
public class CheckClusterNode  implements Job{
    @Override
    public void execute( JobExecutionContext jobExecutionContext ) throws JobExecutionException{
        System.out.println( "job running!!!!!!!!!!!!!");
    }
}
