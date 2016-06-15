package com.bbz.bigdata.platform.rrdtool.measurement;

import com.bbz.bigdata.platform.rrdtool.measurement.impl.CPU;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Disk;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Memory;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.Network;
import com.bbz.bigdata.platform.rrdtool.measurement.impl.hdfs.HDFSCapacity;

/**
 * Created by weiran on 2016/5/20.
 */
public class Metrics {


    public static final com.bbz.bigdata.platform.rrdtool.measurement.impl.CPU CPU=new CPU();
    public static final com.bbz.bigdata.platform.rrdtool.measurement.impl.Memory Memory=new Memory();
    public static final com.bbz.bigdata.platform.rrdtool.measurement.impl.Network Network=new Network();
    public static final com.bbz.bigdata.platform.rrdtool.measurement.impl.Disk Disk=new Disk();
    public static final com.bbz.bigdata.platform.rrdtool.measurement.Measurement.Detail Null_Detail=null;
    public static final com.bbz.bigdata.platform.rrdtool.measurement.impl.hdfs.HDFSCapacity HDFSCapacity=new HDFSCapacity();
}
