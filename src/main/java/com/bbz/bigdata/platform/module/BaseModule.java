package com.bbz.bigdata.platform.module;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;

public abstract class BaseModule {

    /** 注入同名的一个ioc对象 */
    @Inject protected Dao dao;

}
