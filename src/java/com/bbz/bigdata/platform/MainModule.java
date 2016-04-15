package com.bbz.bigdata.platform;

import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;

/**
 * Created by liu_k on 2016/4/15.
 */

@Modules(scanPackage = true)
@SetupBy(value = MainSetup.class)
//@Filters(#  CrossOrginFilter)
//@Filters({@By(type = CrossOriginFilter.class)})
public class MainModule{
}
