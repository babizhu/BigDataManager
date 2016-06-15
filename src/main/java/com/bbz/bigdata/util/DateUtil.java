package com.bbz.bigdata.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by weiran on 2016/6/15.
 */
public class DateUtil {

    public static Date timeAfter(int field,int amount){
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        c.add(field,amount);
        return c.getTime();
    }
}
