package com.bbz.bigdata.platform;

import com.bbz.bigdata.platform.filter.CrossOriginFilter;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * Created by liu_k on 2016/4/15.
 * 很多配置都在这里
 */

@Modules(scanPackage = true)
@SetupBy(value = MainSetup.class)
@IocBy(type = ComboIocProvider.class, args = {"*js", "ioc/",
        "*anno", "com.bbz.bigdata",
        "*tx",
        "*org.nutz.integration.quartz.QuartzIocLoader"})
//@Filters(#  CrossOrginFilter)
@Filters({@By(type = CrossOriginFilter.class)})
@Ok("json:full")
//@Fail("jsp:jsp.500")
public class MainModule{
}
