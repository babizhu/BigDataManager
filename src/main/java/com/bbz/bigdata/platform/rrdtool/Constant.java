package com.bbz.bigdata.platform.rrdtool;

import java.math.BigDecimal;

public class Constant {

	/**
	 * rrdtool程序位置
	 * from setting
	 */
	public static String rrdToolLocation="/usr/local/rrdtool/bin/rrdtool";
	/**
	 * rrd数据存放位置
	 */
	public static String rrdDataLocation="/var/lib/ganglia/rrds/";
	/**
	 * 数据采集间隔
	 * from setting
	 * unit:second
	 */
	public static int dataIntervalTime=10;

	/**
	 * 默认数据
	 * from setting
	 * unit:second
	 */
	public static int defaultPointAmountThreshold=10;
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
}
