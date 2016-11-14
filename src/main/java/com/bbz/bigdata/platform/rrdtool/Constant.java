package com.bbz.bigdata.platform.rrdtool;

import java.math.BigDecimal;

/**
 * from setting
 */
public class Constant {

	/**
	 * rrdtool程序位置
	 */
	public static String rrdToolLocation="/usr/bin/rrdtool";
	/**
	 * rrd数据存放位置
	 */
	public static String rrdDataLocation="/var/lib/ganglia/rrds/";
	/**
	 * 数据采集间隔
	 * unit:second
	 */
	public static int dataIntervalTime=10;

	/**
	 * 默认数据量阈值
	 * unit:second
	 */
	public static int defaultPointAmountThreshold=30;
	/**
	 * 数值精度
	 */
	public static int numberScale=2;
	/**
	 * 单位精度
	 */
	public static int unitNumberScale=20;
	/**
	 * 数值近似处理方法
	 */
	public static int roundingMode=BigDecimal.ROUND_CEILING;
	/**
	 * 用于寻找最新数据点的时间阈值 单位:秒
	 */
	public static int thresholdTimeForNewestData=30;
}
